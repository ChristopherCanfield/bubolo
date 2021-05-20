package bubolo.net.command;

import java.util.UUID;

import bubolo.net.NetworkCommand;
import bubolo.world.Pillbox;
import bubolo.world.World;

/**
 * Moves a pillbox back onto the tile map. Used when a tank puts down a carried pillbox.
 *
 * @author Christopher D. Canfield
 */
public class MovePillboxOntoTileMap extends NetworkCommand {
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final int column;
	private final int row;

	public MovePillboxOntoTileMap(Pillbox pillbox, int column, int row) {
		this.id = pillbox.id();
		this.column = column;
		this.row = row;
	}

	@Override
	protected void execute(World world) {
		var pillbox = (Pillbox) world.getEntity(id);
		world.movePillboxOntoTileMap(pillbox, column, row);
	}
}
