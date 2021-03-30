package bubolo.world;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import bubolo.util.GameLogicException;
import bubolo.world.entity.StationaryElement;
import bubolo.world.entity.OldTerrain;
import bubolo.world.entity.concrete.Grass;
import bubolo.world.entity.concrete.Tree;

public class TileTest
{
	private World world;
	private Tile testTile;
	private static final int TARGET_X_GRID = 2;
	private static final int TARGET_Y_GRID = 3;
	private OldTerrain tempTerrain = new Grass();
	private StationaryElement tempElement = new Tree();
	private final OldTerrain t = new Grass();
	private final StationaryElement e = new Tree();

	@Before
	public void setup()
	{
		world = new GameWorld(10, 10);
		testTile = new Tile(TARGET_X_GRID, TARGET_Y_GRID, t);
		testTile.setElement(e, world);
	}

	@Test
	public void getX()
	{
		assertEquals("Grid returns correct world X", testTile.getX(), TARGET_X_GRID * 32 + 16, .01);
	}

	@Test
	public void getY()
	{
		assertEquals("Grid returns correct world Y", testTile.getY(), TARGET_Y_GRID * 32 + 16, .01);
	}

	@Test
	public void getStationaryElementX()
	{
		assertEquals("StationaryEntities in this Tile return this Tile's position",
				testTile.getX(), t.getX(), .01);
	}

	@Test
	public void getStationaryElementY()
	{
		assertEquals("StationaryEntities in this Tile return this Tile's position",
				testTile.getY(), t.getY(), .01);
	}

	@Test
	public void getGridX()
	{
		assertEquals("Grid returns correct grid X", testTile.getGridX(), TARGET_X_GRID);
	}

	@Test
	public void getGridY()
	{
		assertEquals("Grid returns correct grid Y", testTile.getGridY(), TARGET_Y_GRID);
	}

	@Test
	public void getTerrain()
	{
		assertEquals("Tile returns Terrain object correctly.", testTile.getTerrain(), t);
	}

	@Test
	public void getElement()
	{
		assertEquals("Tile returns StationaryElement object correctly.", testTile.getElement(), e);
	}

	@Test
	public void hasElement()
	{
		assertEquals("Tile checks element presence correctly.", testTile.hasElement(), true);
	}

	@Test
	public void hasNoElement()
	{

		assertEquals("Tile checks element absence correctly.",
				(new Tile(0, 1, tempTerrain)).hasElement(), false);
	}

	@Test
	public void setTerrain()
	{
		assertEquals("Tile sets Terrain correctly.",
				(new Tile(0, 1, new Grass())).setTerrain(tempTerrain, world).getTerrain(), tempTerrain);
	}

	@Test(expected = GameLogicException.class)
	public void clearElement() throws GameLogicException
	{
		Tile tempTile = new Tile(0, 1, tempTerrain);
		tempTile.setElement(tempElement, world);
		tempTile.clearElement(world);
		assertEquals("Tile has null Element after clear", tempTile.getElement(), null);
	}

	@Test(expected = GameLogicException.class)
	public void noElementFound() throws GameLogicException
	{
		Tile tempTile = new Tile(0, 1, tempTerrain);
		tempTile.getElement();
	}

	@Test(expected = GameLogicException.class)
	public void noTerrainFound() throws GameLogicException
	{
		Tile tempTile = new Tile(0, 1, tempTerrain);
		tempTile.setTerrain(null, world);
		tempTile.getTerrain();
	}

}
