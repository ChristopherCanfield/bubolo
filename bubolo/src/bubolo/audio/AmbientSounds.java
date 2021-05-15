package bubolo.audio;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import bubolo.util.Coords;

class AmbientSounds implements Music.OnCompletionListener {
	private int soundIndex;
	private final Music ambientSounds[];
	private final Music oceanWaves;

	private static final String ambientSoundsFolder = "res/ambient/";

	private float volume;

	private float listenerDistanceToDeepWater;

	private static final float clampDistanceWaves = 15 * Coords.TileToWorldScale;
	private static final float rolloffFactorWaves = 2f;
	private static final float referenceDistanceWaves = 100;

	AmbientSounds() {
		ambientSounds = new Music[2];
		ambientSounds[0] = Gdx.audio.newMusic(Gdx.files.internal(ambientSoundsFolder + "464889__klankbeeld__marsh-and-woods-march-nl-kampina-01-190327-1323.ogg"));
		ambientSounds[1] = Gdx.audio.newMusic(Gdx.files.internal(ambientSoundsFolder + "532424__klankbeeld__forest-summer-roond-022-200619-0186.ogg"));

		soundIndex = (new Random()).nextInt(2);

		oceanWaves = Gdx.audio.newMusic(Gdx.files.internal(ambientSoundsFolder + "531015__noted451__ocean-waves.ogg"));
	}

	void setListenerDistanceToDeepWater(float distanceWorldUnits) {
		listenerDistanceToDeepWater = distanceWorldUnits;
		System.out.printf("Distance to water: %f%n", listenerDistanceToDeepWater);

		float oceanWavesVolume;
		if (listenerDistanceToDeepWater < 0 || listenerDistanceToDeepWater > clampDistanceWaves) {
			oceanWavesVolume = 0;
		} else {
			oceanWavesVolume = referenceDistanceWaves /(referenceDistanceWaves + rolloffFactorWaves * (listenerDistanceToDeepWater - referenceDistanceWaves));
			oceanWavesVolume = oceanWavesVolume < 0 ? 0 : oceanWavesVolume;
			oceanWavesVolume = oceanWavesVolume > 1 ? 1 : oceanWavesVolume;
		}
		oceanWaves.setVolume(oceanWavesVolume * volume);
		System.out.printf("Ocean waves volume: %f%n", oceanWaves.getVolume());
	}

	void play(float volume) {
		this.volume = volume;
		ambientSounds[soundIndex].play();
		ambientSounds[soundIndex].setVolume(volume);

		if (!oceanWaves.isPlaying()) {
			oceanWaves.play();
			oceanWaves.setLooping(true);
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
