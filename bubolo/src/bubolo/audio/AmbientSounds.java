package bubolo.audio;

import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_REFERENCE_DISTANCE;
import static org.lwjgl.openal.AL10.AL_ROLLOFF_FACTOR;
import static org.lwjgl.openal.AL10.alSource3f;
import static org.lwjgl.openal.AL10.alSourcef;

import java.lang.reflect.Field;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALMusic;

import bubolo.util.Units;
import bubolo.util.GameRuntimeException;

/**
 * Plays ambient environmental sounds.
 *
 * @author Christopher D. Canfield
 */
class AmbientSounds implements Music.OnCompletionListener {
	private int soundIndex;
	private final Music ambientSounds[];
	private final Music oceanWaves;

	private static final String ambientSoundsFolder = "res/ambient/";

	private float volume;
	private float oceanWavesVolume;

	private float listenerDistanceToDeepWater;

	private int ambientSoundOpenALSourceId = -1;
	private int wavesOpenALSourceId = -1;

	/* Used to access the OpenAL source ID related to libGDX Music (streaming audio) objects.
	 * This is needed to update their position, since libGDX is not designed to work with audio listener movement. */
	private final Field libGdxOpenALMusicSourceIdField;

	private static final float clampDistanceWaves = 15 * Units.TileToWorldScale;
	private static final float rolloffFactorWaves = 1f;
	private static final float referenceDistanceWaves = 100;

	AmbientSounds() {
		ambientSounds = new Music[2];
		ambientSounds[0] = Gdx.audio.newMusic(Gdx.files.internal(ambientSoundsFolder + "464889__klankbeeld__marsh-and-woods-march-nl-kampina-01-190327-1323.ogg"));
		ambientSounds[1] = Gdx.audio.newMusic(Gdx.files.internal(ambientSoundsFolder + "532424__klankbeeld__forest-summer-roond-022-200619-0186.ogg"));

		soundIndex = (new Random()).nextInt(2);

		oceanWaves = Gdx.audio.newMusic(Gdx.files.internal(ambientSoundsFolder + "531015__noted451__ocean-waves.ogg"));

		try {
			libGdxOpenALMusicSourceIdField = OpenALMusic.class.getDeclaredField("sourceID");
			libGdxOpenALMusicSourceIdField.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException e) {
			throw new GameRuntimeException(e);
		}
	}

	void setListenerDistanceToDeepWater(float distanceWorldUnits) {
		listenerDistanceToDeepWater = distanceWorldUnits;

		if (listenerDistanceToDeepWater < 0 || listenerDistanceToDeepWater > clampDistanceWaves) {
			oceanWavesVolume = 0;
		} else {
			oceanWavesVolume = referenceDistanceWaves / (referenceDistanceWaves + rolloffFactorWaves * (listenerDistanceToDeepWater - referenceDistanceWaves));
			oceanWavesVolume = oceanWavesVolume < 0 ? 0 : oceanWavesVolume;
			oceanWavesVolume = oceanWavesVolume > 1 ? 1 : oceanWavesVolume;
		}
		oceanWaves.setVolume(oceanWavesVolume * volume);
	}

	void setListenerPosition(float x, float y) {
		if (wavesOpenALSourceId > -1) {
			alSource3f(wavesOpenALSourceId, AL_POSITION, x, y, 10);
			alSourcef(wavesOpenALSourceId, AL_GAIN, oceanWavesVolume * volume);
		}
		if (ambientSoundOpenALSourceId > -1) {
			alSource3f(ambientSoundOpenALSourceId, AL_POSITION, x, y, 10);
			alSourcef(ambientSoundOpenALSourceId, AL_GAIN, volume);
		}
	}

	void play(float volume) {
		this.volume = volume;

		ambientSounds[soundIndex].play();
		ambientSounds[soundIndex].setVolume(volume);
		try {
			ambientSoundOpenALSourceId = libGdxOpenALMusicSourceIdField.getInt(ambientSounds[soundIndex]);
			alSourcef(ambientSoundOpenALSourceId, AL_ROLLOFF_FACTOR, 1);
			alSourcef(ambientSoundOpenALSourceId, AL_REFERENCE_DISTANCE, 1000);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new GameRuntimeException(e);
		}

		if (!oceanWaves.isPlaying()) {
			oceanWaves.play();
			oceanWaves.setLooping(true);

			try {
				wavesOpenALSourceId = libGdxOpenALMusicSourceIdField.getInt(oceanWaves);
				alSourcef(wavesOpenALSourceId, AL_ROLLOFF_FACTOR, 1);
				alSourcef(wavesOpenALSourceId, AL_REFERENCE_DISTANCE, 1000);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new GameRuntimeException(e);
			}
		}
	}

	@Override
	public void onCompletion(Music music) {
		if (!music.equals(oceanWaves)) {
			soundIndex++;
			if (soundIndex >= ambientSounds.length) {
				soundIndex = 0;
			}

			play(volume);
		}
	}

	void dispose() {
		for (Music ambientSound : ambientSounds) {
			ambientSound.dispose();
		}
		oceanWaves.dispose();
	}
}
