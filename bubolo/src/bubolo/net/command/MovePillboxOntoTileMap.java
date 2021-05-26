package bubolo.net.command;

import java.util.UUID;

import bubolo.Config;
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
	private final byte column;
	private final byte row;

	public MovePillboxOntoTileMap(Pillbox pillbox, int column, int row) {
		assert pillbox.isOwnedByLocalPlayer();
		assert column <= Config.MaxWorldColumns;
		assert row <= Config.MaxWorldRows;

		this.id = pillbox.id();
		this.column = (byte) column;
		this.row = (byte) row;
	}

	@Override
	protected void execute(World world) {
		var pillbox = (Pillbox) world.getEntity(id);
		world.movePillboxOntoTileMap(pillbox, Byte.toUnsignedInt(column), Byte.toUnsignedInt(row));
	}
}
