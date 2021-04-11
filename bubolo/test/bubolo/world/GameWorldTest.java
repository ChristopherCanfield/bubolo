package bubolo.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class GameWorldTest
{
	@Test
	public void gameWorldBadHeight()
	{
		try
		{
			World w = new GameWorld(100, -1000);
			w.update();
			fail("GameWorld did not fail on invalid input");
		}
		catch (Exception e)
		{
		}
	}

	@Test
	public void testAddEntityBase()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(Base.class));
	}

	@Test
	public void testAddEntityBullet()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(Bullet.class));
	}

	@Test
	public void testAddEntityCrater()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(Crater.class));
	}

	@Test
	public void testAddEntityDeepWater()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(DeepWater.class));
	}

	@Test
	public void testAddEntityGrass()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(Grass.class));
	}

	@Test
	public void testAddEntityMine()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(Mine.class));
	}

	@Test
	public void testAddEntityPillbox()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(Pillbox.class));
	}

	@Test
	public void testAddEntityRoad()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(Road.class));
	}

	@Test
	public void testAddEntityRubble()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(Rubble.class));
	}

	@Test
	public void testAddEntitySwamp()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(Swamp.class));
	}

	@Test
	public void testAddEntityTank()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(Tank.class));
	}

	@Test
	public void testAddEntityTree()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(Tree.class));
	}

	@Test
	public void testAddEntityWall()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(Wall.class));
	}

	@Test
	public void testAddEntityWater()
	{
		GameWorld world = new GameWorld(1, 2);
		assertNotNull(world.addEntity(Water.class));
	}

	@Test
	public void testGetEntity()
	{
		GameWorld world = new GameWorld(1, 2);
		OldEntity t = world.addEntity(Tank.class);

		assertEquals(t, world.getEntity(t.getId()));
	}

	@Test
	public void testGetEntities()
	{
		GameWorld world = new GameWorld(1, 2);
		OldEntity t = world.addEntity(Tank.class);
		world.update();

		assertEquals(1, world.getEntities().size());
		assertTrue(world.getEntities().contains(t));
	}

	@Test
	public void testRemoveEntity_Entity()
	{
		GameWorld world = new GameWorld(1, 2);
		OldEntity t = world.addEntity(Tank.class);

		world.update();
		assertTrue(world.getEntities().contains(t));

		world.removeEntity(t);
		world.update();
		assertFalse(world.getEntities().contains(t));
	}

	@Test
	public void testRemoveEntity_UUID()
	{
		GameWorld world = new GameWorld(1, 2);
		OldEntity t = world.addEntity(Tank.class);

		world.update();
		assertTrue(world.getEntities().contains(t));

		world.removeEntity(t.getId());
		world.update();
		assertFalse(world.getEntities().contains(t));
	}

	@Test
	public void testGetMapWidth()
	{
		World w = new GameWorld(10, 50);
		assertEquals(10, w.getWidth());
	}

	@Test
	public void testGetMapHeight()
	{
		World w = new GameWorld(10, 50);
		assertEquals(50, w.getHeight());
	}
}
