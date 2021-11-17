package bubolo.net.command;

import bubolo.GameApplication;
import bubolo.net.NetworkApplicationCommand;
import bubolo.net.NetworkObserverNotifier;
import bubolo.world.Spawn;

/**
 * Notifies the clients that the game is starting. The map must have already been sent to the client before sending
 * this command.
 *
 * @author Christopher D. Canfield
 */
public class StartGame implements NetworkApplicationCommand {
	private static final long serialVersionUID = 1L;

	private final byte secondsUntilStart;

	private final short initialSpawnColumn;
	private final short initialSpawnRow;

	/**
	 * Notifies clients that the game is starting. The map must have already been sent to the client before sending
	 * this command.
	 *
	 * @param secondsUntilStart the number of seconds until the game starts. Must be equal to or less than 255.
	 * @param initialSpawnPoint this player's initial spawn point.
	 */
	public StartGame(int secondsUntilStart, Spawn initialSpawnPoint) {
		assert secondsUntilStart <= (Byte.MAX_VALUE * 2 + 1);
		this.secondsUntilStart = (byte) secondsUntilStart;

		this.initialSpawnColumn = (short) initialSpawnPoint.tileColumn();
		this.initialSpawnRow = (short) initialSpawnPoint.tileRow();
	}

	@Override
	public void execute(GameApplication app, NetworkObserverNotifier notifier) {
		// Notify NetworkObservers about the game start time.
		notifier.notifyGameStart(Byte.toUnsignedInt(secondsUntilStart),
				Short.toUnsignedInt(initialSpawnColumn),
				Short.toUnsignedInt(initialSpawnRow));
	}
}
