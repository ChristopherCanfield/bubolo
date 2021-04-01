/**
 *
 */

package bubolo.net.command;

import static com.google.common.base.Preconditions.checkNotNull;

import bubolo.controllers.ai.AiTreeController;
import bubolo.net.Network;
import bubolo.net.NetworkCommand;
import bubolo.net.NetworkSystem;
import bubolo.net.WorldOwner;
import bubolo.world.World;

/**
 * Command that is used to notify the clients to start the game.
 *
 * @author BU CS673 - Clone Productions
 */
public class StartGame implements NetworkCommand
{
	private static final long serialVersionUID = 1L;

	private final int secondsUntilStart;
	private final SendMap sendMapCommand;

	/**
	 * Notifies clients that the game is starting.
	 *
	 * @param secondsUntilStart
	 *            the number of seconds until the game starts.
	 * @param sendMapCommand
	 *            an instantiated send map command.
	 */
	public StartGame(int secondsUntilStart, SendMap sendMapCommand)
	{
		this.secondsUntilStart = secondsUntilStart;
		this.sendMapCommand = checkNotNull(sendMapCommand);
	}

	@Override
	public void execute(WorldOwner worldOwner)
	{
		World world = worldOwner.world();

		// Only the server has a Tree controller, to eliminate the risk of synchronization issues.
		world.removeController(AiTreeController.class);

		// Build the map on the client.
		sendMapCommand.execute(world);

		// Notify NetworkObservers about the game start time.
		Network net = NetworkSystem.getInstance();
		net.getNotifier().notifyGameStart(secondsUntilStart);
	}
}
