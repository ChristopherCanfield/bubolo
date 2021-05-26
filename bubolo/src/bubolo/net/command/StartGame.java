package bubolo.net.command;

import bubolo.net.Network;
import bubolo.net.NetworkCommand;
import bubolo.net.NetworkSystem;
import bubolo.net.WorldOwner;

/**
 * Notifies the clients that the game is starting. The map must have already been sent to the client before sending
 * this command.
 *
 * @author Christopher D. Canfield
 */
public class StartGame extends NetworkCommand {
	private static final long serialVersionUID = 1L;

	private final byte secondsUntilStart;

	/**
	 * Notifies clients that the game is starting. The map must have already been sent to the client before sending
	 * this command.
	 *
	 * @param secondsUntilStart the number of seconds until the game starts. Must be equal to or less than 255.
	 */
	public StartGame(int secondsUntilStart) {
		assert secondsUntilStart <= (Byte.MAX_VALUE * 2 + 1);
		this.secondsUntilStart = (byte) secondsUntilStart;
	}

	@Override
	public void execute(WorldOwner worldOwner) {
		// Notify NetworkObservers about the game start time.
		Network net = NetworkSystem.getInstance();
		net.getNotifier().notifyGameStart(Byte.toUnsignedInt(secondsUntilStart));
	}
}
