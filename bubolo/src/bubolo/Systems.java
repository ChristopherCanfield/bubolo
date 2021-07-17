package bubolo;

import bubolo.audio.Audio;
import bubolo.audio.AudioSystem;
import bubolo.audio.NullAudio;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.NullNetwork;

public class Systems {
	public enum NetworkType {
		Null,
		Real
	}

	private static Audio audio = new NullAudio();
	private static Network network = new NullNetwork();

	/**
	 * Initializes the sound system.
	 *
	 * @param worldWidth the world's width, in world units.
	 * @param worldHeight the world's height, in world units.
	 * @param viewportWidth the viewport's width, in world units.
	 * @param viewportHeight the viewport's height, in world units.
	 */
	public static void initializeAudio(float worldWidth, float worldHeight, float viewportWidth, float viewportHeight) {
		audio = new AudioSystem(worldWidth, worldHeight, viewportWidth, viewportHeight);
	}

	public static void initializeNetwork() {
		initializeNetwork(NetworkType.Real);
	}

	public static void initializeNetwork(NetworkType type) {
		if (type == NetworkType.Real) {
			// Don't construct a new NetworkSystem instance if one was already initialized.
			if (!(network instanceof NetworkSystem)) {
				network = new NetworkSystem();
			}
		} else {
			network = new NullNetwork();
		}
	}

	public static Audio audio() {
		return audio;
	}

	public static Network network() {
		return network;
	}

	/**
	 * Disposes the subsystems that are owned by this static object.
	 */
	public static void dispose() {
		audio.dispose();
		network.dispose();
	}
}
