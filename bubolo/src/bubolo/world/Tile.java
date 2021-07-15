package bubolo.world;

import bubolo.Config;

/**
 * A tile address on the game map.
 *
 * @param column the tile's column. >= 0 and < Config.MaxWorldColumns.
 * @param row the tile's row. >= 0 and < Config.MaxWorldRows.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public record Tile(int column, int row) {
	public Tile {
		assert column >= 0;
		assert column < Config.MaxWorldColumns;
		assert row >= 0;
		assert row < Config.MaxWorldRows;
	}
}
