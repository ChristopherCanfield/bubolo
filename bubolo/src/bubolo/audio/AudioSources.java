package bubolo.audio;

import static org.lwjgl.openal.AL10.AL_INITIAL;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.AL_STOPPED;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alGetSourcei;

import org.lwjgl.openal.AL10;

/**
 * Loads and manages OpenAL audio sources. Call nextId() to get the next available audio source ID.
 *
 * @author Christopher D. Canfield
 */
class AudioSources {
	private final int[] sources;
	private int nextIndex;

	/**
	 * Generates OpenAL audio sources.
	 *
	 * @param numberToGenerate the number of sources to generate.
	 * @throws GameAudioException if OpenAL reports an error.
	 */
	AudioSources(int numberToGenerate) {
		assert numberToGenerate > 0;

		// Reset error state.
		alGetError();

		// Get the sources.
		sources = new int[numberToGenerate];
		alGenSources(sources);

		// Check for errors.
		int errorCode = alGetError();
		if (errorCode != 0) {
			String errorText = AL10.alGetString(errorCode);
			throw new GameAudioException(String.format("OpenAL error %s (%d) when attempting to generate %d sources.", errorText, errorCode, numberToGenerate));
		}
	}

	/**
	 * @return the next OpenAL source ID that is in either the AL_STOPPED or AL_INITIAL state.
	 */
	int nextId() {
		final int maxAttempts = sources.length;
		int attempts = 0;

		int id = -1;
		do {
			id = sources[nextIndex++];
			nextIndex = nextIndex >= sources.length ? 0 : nextIndex;
			attempts++;

			// Ensure that the source is either in the initial or stopped state.
			var sourceState = alGetSourcei(id, AL_SOURCE_STATE);
			if (sourceState != AL_STOPPED && sourceState != AL_INITIAL) {
				id = -1;
			}

			if (attempts > maxAttempts) {
				throw new GameAudioException("AudioSource.nextId: No available ID found.");
			}
		} while (id == -1);

		return id;
	}

	void dispose() {
		AL10.alDeleteSources(sources);
	}
}
