package bubolo;

import bubolo.audio.Audio;
import bubolo.audio.AudioImpl;
import bubolo.audio.NullAudio;

public class Systems {
	private static Audio audio = new NullAudio();

	/**
	 * Initializes the sound system.
	 *
	 * @param worldWidth the world's width, in world units.
	 * @param worldHeight the world's height, in world units.
	 * @param viewportWidth the viewport's width, in world units.
	 * @param viewportHeight the viewport's height, in world units.
	 */
	public static void initializeAudio(float worldWidth, float worldHeight, float viewportWidth, float viewportHeight) {
		audio = new AudioImpl(worldWidth, worldHeight, viewportWidth, viewportHeight);
	}

	public static Audio audio() {
		return audio;
	}

	/**
	 * Disposes the subsystems that are owned by this static object.
	 */
	public static void dispose() {
		audio.dispose();
	}
}
