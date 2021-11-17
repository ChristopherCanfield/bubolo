package bubolo.net.command;

import java.util.UUID;

import bubolo.net.NetworkGameCommand;
import bubolo.world.Pillbox;
import bubolo.world.World;

/**
 * Moves a pillbox off of the tile map. Used when a tank is carrying a pillbox.
 *
 * @author Christopher D. Canfield
 */
public class MovePillboxOffTileMap implements NetworkGameCommand {
	private static final long serialVersionUID = 1L;

	private final UUID id;

	public MovePillboxOffTileMap(Pillbox pillbox) {
		assert pillbox.isOwnedByLocalPlayer();
		this.id = pillbox.id();
	}

	@Override
	public void execute(World world) {
		var pillbox = (Pillbox) world.getEntity(id);
		world.movePillboxOffTileMap(pillbox);
	}
}
