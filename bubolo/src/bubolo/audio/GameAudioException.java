package bubolo.audio;

import bubolo.util.GameRuntimeException;

/**
 * Thrown by the audio system.
 *
 * @author Christopher D. Canfield
 */
public class GameAudioException extends GameRuntimeException {
	private static final long serialVersionUID = 1L;

	GameAudioException(String message) {
		super(message);
	}
}
