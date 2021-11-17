package bubolo.net.command;

import java.util.UUID;

import bubolo.net.NetworkGameCommand;
import bubolo.world.Tank;
import bubolo.world.World;

/**
 * A NetworkGameCommand that targets a specific player. Uses the player's tank entity ID. Subclasses should override the
 * {@code onExecute} method, which provides references to the world and the target player's tank.
 *
 * @author Christopher D. Canfield
 */
public abstract class ToPlayerNetworkGameCommand implements NetworkGameCommand {
	private static final long serialVersionUID = 1L;

	private final UUID targetPlayerId;

	protected ToPlayerNetworkGameCommand(UUID targetPlayerId) {
		assert targetPlayerId != null;
		this.targetPlayerId = targetPlayerId;
	}

	@Override
	public final void execute(World world) {
		Tank tank = (Tank) world.getEntity(targetPlayerId);
		// If the tank is owned by the local player, this is the target player. Otherwise, discard the command.
		if (tank.isOwnedByLocalPlayer()) {
			onExecute(world, tank);
		}
	}

	protected abstract void onExecute(World world, Tank localTank);
}
