package bubolo.world.entity.concrete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import bubolo.world.GameWorld;
import bubolo.world.Grass;
import bubolo.world.Tile;
import bubolo.world.Tree;
import bubolo.world.World;
import bubolo.world.entity.EntityTestCase;

public class TreeTest
{
	private static Tree tree;
	private static World world;
	private static Grass grass;
	/**
	 * Constructs a Tree object and sets the default parameters.
	 */
	@BeforeClass
	public static void setup()
	{
		world = new GameWorld(10, 10);
		tree = world.addEntity(Tree.class);
		grass = world.addEntity(Grass.class);
		Tile[][] treeTile = new Tile[1][1];
		treeTile[0][0] = new Tile(0,0,grass);
		treeTile[0][0].setElement(tree, world);
		EntityTestCase.setTestParams(tree);
	}

	@Test
	public void Tree()
	{
		assertTrue(true);
	}
	@Test
	public void  getHitPoints()
	{
		assertEquals(1, tree.hitPoints(), 0);
	}

	@Test
	public void getMaxHitPoints()
	{
		assertEquals(1, tree.maxHitPoints(), 0);
	}

	@Test
	public void healDamageTest()
	{
		tree.takeHit(1);
		assertEquals(0, tree.hitPoints(), 0);
		tree.heal(1);
		assertEquals(1, tree.hitPoints(), 0);
	}

	@Test
	public void onDispose()
	{
		tree.onDispose();
	}
}
