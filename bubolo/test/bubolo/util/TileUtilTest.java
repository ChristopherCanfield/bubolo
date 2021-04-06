package bubolo.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import bubolo.world.GameWorld;
import bubolo.world.Grass;
import bubolo.world.Tank;
import bubolo.world.Tile;
import bubolo.world.Wall;
import bubolo.world.Water;
import bubolo.world.entity.OldEntity;

/**
 * @author BU CS673 - Clone Productions
 */
public class TileUtilTest
{
	static GameWorld world;
	static Tile[][] tiles;
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
	public void isValidTile(){
		assertTrue(TileUtil.isValidTile(0,0, world));
		assertFalse(TileUtil.isValidTile(3,3, world));
		assertFalse(TileUtil.isValidTile(-1,0, world));
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

	@Test
	public void getLocalEntites(){
		var l = TileUtil.getLocalEntities(0, 0, world);
		assertTrue("List of local Entities does not contain correct objects!",  l.contains(tiles[0][0].getTerrain()));
		l = TileUtil.getLocalEntities(3*32f + 16f, 3*32f + 16f, world);
		assertTrue("List of local Entities does not contain correct objects!", l.contains(tiles[1][1].getTerrain()));
		assertFalse("List of local Entities contains incorrect objects!", l.contains(tiles[0][0].getTerrain()));

	}

	@Test
	public void getClosestTile(){
		int i = TileUtil.getClosestTileX(31);
		int j = TileUtil.getClosestTileY(42);
		assertEquals("Returned incorrect tile index for x float value.", 0, i);
		assertEquals("Returned incorrect tile index for x float value.", 1, j);

	}

	@Test
	public void getLocalCollisions(){
		var l = TileUtil.getLocalEntities(0, 0, world);
		assertTrue("List of local Entities does not contain correct objects!", l.contains(tiles[0][0].getTerrain()));
		l = TileUtil.getLocalEntities(3*32f + 16f, 3*32f + 16f, world);
		assertTrue("List of local Entities does not contain correct objects!", l.contains(tiles[1][1].getTerrain()));
		assertFalse("List of local Entities contains incorrect objects!", l.contains(tiles[0][0].getTerrain()));

		var c = TileUtil.getLocalCollisions(tank, world);
		assertTrue("List of local collisions does not contain correct objects", c.contains(tiles[0][0].getElement()));
	}
	@Test
	public void getEntityTile()
	{
		OldEntity entity = new Tank().setTransform(31, 42, 0);
		Tile tile = TileUtil.getEntityTile(entity, world);
		assertEquals("returned incorrect tile", 0, tile.getGridX());
		assertEquals("returned incorrect tile", 1, tile.getGridY());
	}
}
