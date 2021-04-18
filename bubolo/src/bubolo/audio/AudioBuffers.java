package bubolo.audio;

import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alGetString;
import static org.lwjgl.openal.AL10.alSourcei;

import org.lwjgl.openal.AL10;

/**
 * Generates and manages OpenAL buffers.
 *
 * @author Christopher D. Canfield
 */
class AudioBuffers {
	private final int[] bufferIds;

	/**
	 * Generates one OpenAL buffer for each sound effect in the Sfx enum.
	 */
	AudioBuffers() {
		// Reset error state.
		alGetError();

		// Generate the buffers.
		bufferIds = new int[Sfx.values().length];
		alGenBuffers(bufferIds);

		// Check for errors.
		int errorCode = alGetError();
		if (errorCode != 0) {
			String errorText = alGetString(errorCode);
			throw new GameAudioException(String.format("OpenAL error %s (%d) when attempting to generate %d buffers.", errorText, errorCode, numberToGenerate));
		}
	}

	void attachBufferToSource(Sfx soundEffect, int source) {
		alSourcei(source, AL10.AL_BUFFER, bufferIds[soundEffect.ordinal()]);

		int errorCode = alGetError();
		if (errorCode != 0) {
			String errorText = alGetString(errorCode);
			throw new GameAudioException(
					String.format("OpenAL error %s (%d) when attempting to attach buffer %d to source %d.", errorText, errorCode, source, soundEffect.ordinal()));
		}
	}

	void dispose() {
		alDeleteBuffers(bufferIds);
	}
}
