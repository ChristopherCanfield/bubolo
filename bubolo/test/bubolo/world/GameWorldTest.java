package bubolo.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import bubolo.controllers.ControllerFactory;
import bubolo.controllers.ai.AiTreeController;
import bubolo.world.entity.OldEntity;
import bubolo.world.entity.concrete.Base;
import bubolo.world.entity.concrete.Bullet;
import bubolo.world.entity.concrete.Crater;
import bubolo.world.entity.concrete.DeepWater;
import bubolo.world.entity.concrete.Grass;
import bubolo.world.entity.concrete.Mine;
import bubolo.world.entity.concrete.Pillbox;
import bubolo.world.entity.concrete.Road;
import bubolo.world.entity.concrete.Rubble;
import bubolo.world.entity.concrete.Spawn;
import bubolo.world.entity.concrete.Swamp;
import bubolo.world.entity.concrete.Tank;
import bubolo.world.entity.concrete.Tree;
import bubolo.world.entity.concrete.Wall;
import bubolo.world.entity.concrete.Water;

public class GameWorldTest
{
	@Test
	public void gameWorldBadWidth()
	{
		try
		{
			World w = new GameWorld(-100, 1000);
			w.update();
			fail("GameWorld did not fail on invalid input");
		}
		catch (Exception e)
		{
		}
	}

	@Test
	public void getTanks()
	{
		GameWorld w= new GameWorld(500, 500);
		Tank t = w.addEntity(Tank.class);
		var l = w.getTanks();
		assertEquals("List does not contain the target tank!", true, l.contains(t));
		w.removeEntity(t);
		l = w.getTanks();
		assertEquals("List contains the target tank after it was removed!", false, l.contains(t));
	}

	@Test
	public void getSpawns()
	{
		GameWorld w= new GameWorld(500, 500);
		Spawn s = w.addEntity(Spawn.class);
		var l = w.getSpawns();
		assertEquals("List does not contain the target Spawn!", true, l.contains(s));
		w.removeEntity(s);
		l = w.getSpawns();
		assertEquals("List contains the target tank after it was removed!", false, l.contains(s));
	}

	@Test
	public void getEffects()
	{
		GameWorld w= new GameWorld(500, 500);
		Bullet b = w.addEntity(Bullet.class);
		var l = w.getEffects();
		assertEquals("List does not contain the target tank!", true, l.contains(b));
		w.removeEntity(b);
		l = w.getEffects();
		assertEquals("List contains the target tank after it was removed!", false, l.contains(b));
	}

	@Test
	public void getActors()
	{
		GameWorld w= new GameWorld(500, 500);
		Tank t = w.addEntity(Tank.class);
		var l = w.getActors();
		assertEquals("List does not contain the target tank!", true, l.contains(t));
		w.removeEntity(t);
		l = w.getActors();
		assertEquals("List contains the target tank after it was removed!", false, l.contains(t));
	}

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
	@Test
	public void testGetMapTiles()
	{
		GameWorld w = new GameWorld(1, 1);
		Tile[][] tiles = new Tile[1][1];
		tiles[0][0] = new Tile(0, 0);
		w.setTiles(tiles);
		assertSame(tiles, w.getTiles());
	}
	@Test
	public void testSetMapTiles()
	{
		World w = new GameWorld(10, 50);
		assertEquals(50, w.getHeight());
	}

	@Test
	public void testSetMapHeight()
	{
		World w = new GameWorld(0, 0);
		w.setHeight(40);
		assertEquals(40, w.getHeight());
	}

	@Test
	public void testSetMapWidth()
	{
		World w = new GameWorld(0, 0);
		w.setWidth(75);
		assertEquals(75, w.getWidth());
	}

	@Test
	public void testAddEntity()
	{
		World w = new GameWorld(0,0);
		OldEntity e = new Grass();
		ControllerFactory c;
		c = null;
		w.addEntity(e.getClass(), e.getId());
		w.addEntity(e.getClass(), c);
	}

	@Test
	public void testTileFunctions()
	{
		World w = new GameWorld(0,0);
		Tile[][] mapTiles = new Tile[1][1];
		mapTiles[0][0] = new Tile(0, 0, w.addEntity(Grass.class));
		w.setTiles(mapTiles);
		assertEquals(Grass.class, w.getTiles()[0][0].getTerrain().getClass());
	}

	@Test
	public void addRemoveController()
	{
		World w = new GameWorld(0, 0);
		w.addController(AiTreeController.class);
		assertEquals(1, w.getControllerCount());

		w.removeController(AiTreeController.class);
		assertEquals(0, w.getControllerCount());
	}
}
