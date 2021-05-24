package bubolo.net.command;

import java.util.UUID;

import bubolo.net.NetworkCommand;
import bubolo.world.Pillbox;
import bubolo.world.World;

/**
 * Moves a pillbox off of the tile map. Used when a tank is carrying a pillbox.
 *
 * @author Christopher D. Canfield
 */
public class MovePillboxOffTileMap extends NetworkCommand {
	private static final long serialVersionUID = 1L;

	private final UUID id;

	public MovePillboxOffTileMap(Pillbox pillbox) {
		this.id = pillbox.id();
	}

	@Override
	protected void execute(World world) {
		var pillbox = (Pillbox) world.getEntity(id);
		world.movePillboxOffTileMap(pillbox);
	}
}
