package bubolo.audio;

import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetError;

import org.lwjgl.openal.AL10;

class AudioSources {
	private int[] sources;
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

	int nextId() {
		int index = sources[nextIndex++];
		nextIndex = nextIndex >= sources.length ? 0 : nextIndex;
		return index;
	}

	void dispose() {
		AL10.alDeleteSources(sources);
	}
}
