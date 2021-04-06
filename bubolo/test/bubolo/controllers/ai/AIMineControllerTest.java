package bubolo.controllers.ai;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import bubolo.controllers.Controller;
import bubolo.world.GameWorld;
import bubolo.world.Mine;
import bubolo.world.Tile;


public class AIMineControllerTest
{
	@Test
	public void test()
	{
		Controller c = new AiMineController(mock(Mine.class));

		GameWorld world = new GameWorld(100, 100);
		Tile[][] tiles = createTiles(100, 100);
		world.setTiles(tiles);

		c.update(world);
	}

	private static Tile[][] createTiles(int rows, int columns) {
		Tile[][] tiles = new Tile[rows][columns];

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				tiles[row][col] = new Tile(row, col);
			}
		}

		return tiles;
	}
}
