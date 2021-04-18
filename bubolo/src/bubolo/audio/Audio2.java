package bubolo.audio;


import static com.google.common.base.Preconditions.checkArgument;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.alDistanceModel;

import java.io.File;
import java.util.Random;
import java.util.logging.Logger;

import org.lwjgl.openal.AL10;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import bubolo.Config;

/**
 * The top-level class for the Sound system.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Audio2
{
	private static final Logger logger = Logger.getLogger(Config.AppProgramaticTitle);

	/**
	 * Instances of this class should not be directly constructed.
	 */
	private Audio2() {}

	/** The path to the sound effect files. */
	private static final String sfxPath = "res/sfx/";

	private static boolean initialized = false;


	// The sound effects volume. The default is 100%.
	private static float soundEffectVolume = 1.0f;

	// OpenAL reference distance: the distance that the sound is played at its highest volume.
	private static float referenceDistance;
	// OpenAL rolloff factor.
	private static float rolloffFactor;

	private static float listenerX;
	private static float listenerY;

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
		preloadCoreSoundEffects();

		alDistanceModel(AL10.AL_INVERSE_DISTANCE);

		referenceDistance = Math.max(viewportWidth, viewportHeight) * 0.5f;
		rolloffFactor = Math.min(worldWidth / viewportWidth, worldHeight / viewportHeight);
	}

	public static void setListenerPosition(float x, float y) {
		listenerX = x;
		listenerY = y;
	}

	/**
	 * Plays a sound effect. This should be called in the following way:<br><br>
	 * <code>Audio.play(Sfx.TankExplosion);<br>
	 * Audio.play(Sfx.TankHit);</code>
	 *
	 * @param soundEffect the sound effect to play.
	 * @param x the source's x world position.
	 * @param y the source's y world position.
	 */
	public static void play(Sfx soundEffect, float x, float y)
	{
		if (initialized) {


				AL10.alListener3f(AL_POSITION, listenerX, listenerY, 0f);
				System.out.println("Error1: " + AL10.alGetError());

				int sourceId = (int) id;
				AL10.alSourcef(sourceId, AL10.AL_PITCH, getRandomPitch(soundEffect.pitchRangeMin, soundEffect.pitchRangeMax));
				System.out.println("Error2: " + AL10.alGetError());
				AL10.alSource3f(sourceId, AL_POSITION, x, y, 1f);
				System.out.println("Error3: " + AL10.alGetError());
				AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, rolloffFactor);
				System.out.println("Error4: " + AL10.alGetError());
				AL10.alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, referenceDistance);
				System.out.println("Error5: " + AL10.alGetError());
				System.out.printf("Setting alSource3f AL_POSITION: %f,%f%n", x, y);

				float[] l1 = new float[1];
				float[] l2 = new float[1];
				float[] l3 = new float[1];
				AL10.alGetListener3f(AL_POSITION, l1, l2, l3);
				System.out.printf("Listener location: %f,%f,%f%n", l1[0], l2[0], l3[0]);
//				AL10.alSourcePlay(sourceId);
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
	 * @param volume the new sound effect volume, ranging from 0 to 1.
	 * @throws IllegalArgumentException if volume is less than 0 or greater than 1.
	 */
	public static void setSoundEffectVolume(float volume)
	{
		checkArgument(volume >= 0, "Sound effect volume was less than zero: %s", volume);
		checkArgument(volume <= 1, "Sound effect volume was greater than one: %s", volume);

		soundEffectVolume = volume;
	}

	/**
	 * Gets the sound effect volume, in the range [0, 1].
	 * @return the sound effect volume.
	 */
	public static float getSoundEffectVolume()
	{
		return soundEffectVolume;
	}

	/**
	 * Preloads the core sound effects, to prevent slight hickups that can occur when a sound is first used.
	 */
	private static void preloadCoreSoundEffects() {
		getSoundEffect(Sfx.CannonFired);
		getSoundEffect(Sfx.MineExplosion);
		getSoundEffect(Sfx.TankExplosion);
		getSoundEffect(Sfx.PillboxHit);
		getSoundEffect(Sfx.TreeHit);
		getSoundEffect(Sfx.WallHit);
		getSoundEffect(Sfx.TankHit);
	}

	/**
	 * Gets the specified sound effect. Loads the sound file and stores it in memory if needed.
	 * @param sfx the sound effect to get.
	 * @return reference to the loaded sound effect.
	 */
	private static Sound getSoundEffect(Sfx sfx) {
		Sound soundEffect = soundEffects.get(sfx);
		if (soundEffect == null) {
			soundEffect = loadSoundEffect(sfx);
			soundEffects.put(sfx, soundEffect);
		}
		return soundEffect;
	}

	private static Sound loadSoundEffect(Sfx sfx) {
		FileHandle soundFile = new FileHandle(new File(Audio2.sfxPath + sfx.fileName));
		Sound sound = Gdx.audio.newSound(soundFile);
		return sound;
	}

	/**
	 * Shuts down and cleans up the audio system.
	 */
	public static void dispose()
	{
	}
}
