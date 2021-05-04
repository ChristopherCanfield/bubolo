package bubolo.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import bubolo.world.GameWorld;
import bubolo.world.Grass;
import bubolo.world.Tank;
import bubolo.world.Wall;
import bubolo.world.Water;

/**
 * @author BU CS673 - Clone Productions
 */
public class TileUtilTest
{
	static GameWorld world;
	static Tank tank;

	@BeforeClass
	public static void setup()
	{
		world = new GameWorld(32 * 4, 32 * 4);
		tiles = new Tile[2][2];
		tiles[0][0] = new Tile(0, 0, new Grass());
		tiles[0][1] = new Tile(0, 1, new Grass());
		tiles[1][0] = new Tile(1, 0, new Grass());
		tiles[1][1] = new Tile(0, 1, new Water());
		world.setTiles(tiles);
		tank = world.addEntity(Tank.class);
		tank.setTransform(16, 16, 0);

		var wall = world.addEntity(Wall.class);
		tiles[0][0].setElement(wall, world);
	}

	@Test
	public void getCornerStates()
	{
		boolean[] corners = TileUtil.getCornerMatches(tiles[0][0], world, new Class[] { Water.class });
		assertTrue("Tile (0,0) has wrong top-right corner state!", corners[1]);
		assertFalse("Tile (1,0) has wrong top-right corner state!", corners[0]);
	}

	@Test
	public void getEdgeMatches()
	{
		boolean[] edges = TileUtil.getEdgeMatches(tiles[1][0], world, new Class[] { Grass.class });
		assertFalse("Tile (1,0) has wrong top state!", edges[0]);
		assertTrue("Tile (1,0) has wrong bottom state!", edges[2]);
	}

	@Test
	public void getTilingState()
	{
		assertEquals("Tile (1,0) has the wrong tiling state!", 9, TileUtil.getTilingState(tiles[0][0], world, new Class[] { Grass.class }));
	}
}
