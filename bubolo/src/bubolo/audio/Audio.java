package bubolo.audio;

import static com.google.common.base.Preconditions.checkArgument;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_MAX_DISTANCE;
import static org.lwjgl.openal.AL10.AL_PITCH;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_REFERENCE_DISTANCE;
import static org.lwjgl.openal.AL10.AL_ROLLOFF_FACTOR;
import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.AL10.alListener3f;
import static org.lwjgl.openal.AL10.alSource3f;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourcef;

import java.util.Random;
import java.util.logging.Logger;

import org.lwjgl.openal.AL10;

import bubolo.Config;

/**
 * The top-level class for the Sound system.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Audio {
	private static final Logger logger = Logger.getLogger(Config.AppProgramaticTitle);

	/** Instances of this class should not be directly constructed. */
	private Audio() {
	}

	/** The path to the sound effect files. */
	private static final String sfxPath = "res/sfx/";

	private static boolean initialized = false;

	private static AudioSources sources;
	private static AudioBuffers buffers;

	// The sound effects volume. The default is 100%.
	private static float soundEffectVolume = 1.0f;
	// The ambient sounds volume. The default is 75%.
	private static float ambientSoundsVolume = 0.75f;

	// OpenAL reference distance: the distance that the sound is played at its highest volume.
	private static float referenceDistance;
	// OpenAL rolloff factor: how quickly a sound fades out as its distance increases.
	private static float rolloffFactor;
	private static float maxDistance;

	private static float listenerX;
	private static float listenerY;

	/* The z position is set high so that cannon fire sounds from the tank's own cannon sound like it is coming from the tank itself,
		rather than from the east or west. */
	private static final float sourceZPosition = 100.0f;

	private static AmbientSounds ambientSounds;

	/**
	 * Initializes the sound system.
	 *
	 * @param worldWidth the world's width, in world units.
	 * @param worldHeight the world's height, in world units.
	 * @param viewportWidth the viewport's width, in world units.
	 * @param viewportHeight the viewport's height, in world units.
	 */
	public static void initialize(float worldWidth, float worldHeight, float viewportWidth, float viewportHeight) {
		initialized = true;

		sources = new AudioSources(125);
		buffers = new AudioBuffers();
		loadSoundEffects();

		alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);

		referenceDistance = Math.max(viewportWidth, viewportHeight) * 0.4f;
		rolloffFactor = Math.min(worldWidth / viewportWidth, worldHeight / viewportHeight);
		maxDistance = Math.min(viewportWidth * 3, viewportHeight * 3);

		ambientSounds = new AmbientSounds();
		ambientSounds.play(ambientSoundsVolume);
	}

	public static void setListenerPosition(float x, float y) {
		listenerX = x;
		listenerY = y;

		ambientSounds.setListenerPosition(x, y);
	}

	public static void setListenerDistanceToDeepWater(float distanceWorldUnits) {
		ambientSounds.setListenerDistanceToDeepWater(distanceWorldUnits);
	}

	/**
	 * Plays a sound effect. This should be called in the following way:<br>
	 * <br>
	 * <code>Audio.play(Sfx.TankExplosion, sourceWorldX, sourceWorldY);<br>
	 * Audio.play(Sfx.TankHit, sourceWorldX, sourceWorldY);</code>
	 *
	 * @param soundEffect the sound effect to play.
	 * @param x the source's x world position.
	 * @param y the source's y world position.
	 */
	public static void play(Sfx soundEffect, float x, float y) {
		if (initialized) {
			alListener3f(AL_POSITION, listenerX, listenerY, 0f);

			int sourceId = sources.nextId();
			buffers.attachBufferToSource(soundEffect, sourceId);

			alSourcef(sourceId, AL_GAIN, soundEffectVolume * soundEffect.volumeAdjustment);
			alSourcef(sourceId, AL_PITCH, getRandomPitch(soundEffect.pitchRangeMin, soundEffect.pitchRangeMax));

			alSource3f(sourceId, AL_POSITION, x, y, sourceZPosition);
			alSourcef(sourceId, AL_ROLLOFF_FACTOR, rolloffFactor);
			alSourcef(sourceId, AL_REFERENCE_DISTANCE, referenceDistance);
			alSourcef(sourceId, AL_MAX_DISTANCE, maxDistance);

			alSourcePlay(sourceId);
		} else {
			logger.warning("Audio.play called before audio system was initialized.");
		}
	}

	private static final Random random = new Random();

	private static float getRandomPitch(float min, float max) {
		float adjustment = random.nextFloat() * (max - min);
		float pitch = max - adjustment;
		assert pitch >= 0.5f && pitch <= 2.0f;
		return max - adjustment;
	}

	/**
	 * Sets the sound effect volume, from 0 (mute) to 1 (max volume).
	 *
	 * @param volume the new sound effect volume, ranging from 0 to 1.
	 * @throws IllegalArgumentException if volume is less than 0 or greater than 1.
	 */
	public static void setSoundEffectVolume(float volume) {
		checkArgument(volume >= 0, "Sound effect volume was less than zero: %s", volume);
		checkArgument(volume <= 1, "Sound effect volume was greater than one: %s", volume);

		soundEffectVolume = volume;
	}

	/**
	 * Gets the sound effect volume, in the range [0, 1].
	 *
	 * @return the sound effect volume.
	 */
	public static float soundEffectVolume() {
		return soundEffectVolume;
	}

	/**
	 * Loads all sound effects.
	 */
	private static void loadSoundEffects() {
		var soundEffects = Sfx.values();
		for (Sfx sfx : soundEffects) {
			buffers.loadOgg(sfx, sfxPath);
		}
	}

	/**
	 * Shuts down and cleans up the audio system.
	 */
	public static void dispose() {
		if (initialized) {
			sources.dispose();
			buffers.dispose();
			ambientSounds.dispose();
		}
		initialized = false;
	}
}
