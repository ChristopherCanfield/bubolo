package bubolo.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bubolo.util.Coords;
import bubolo.world.Entity.ConstructionArgs;

public class GameWorldTest
{
	private final ConstructionArgs args = new Entity.ConstructionArgs(UUID.randomUUID(), 0, 0, 0);

	private World world;

	@BeforeEach
	public void beforeEach() {
		world = new GameWorld(2, 2);
	}

	@Test
	public void addEntityBase()
	{
		assertNotNull(world.addEntity(Base.class, args));
		world.update();
	}

	@Test
	public void addEntityBullet()
	{
		assertNotNull(world.addEntity(Bullet.class, args));
		world.update();
	}

	@Test
	public void addEntityCrater()
	{
		assertNotNull(world.addEntity(Crater.class, args));
		world.update();
	}

	@Test
	public void addEntityDeepWater()
	{
		assertNotNull(world.addEntity(DeepWater.class, args));
		world.update();
	}

	@Test
	public void addEntityGrass()
	{
		assertNotNull(world.addEntity(Grass.class, args));
		world.update();
	}

	@Test
	public void addEntityMine()
	{
		assertNotNull(world.addEntity(Mine.class, args));
		world.update();
	}

	@Test
	public void addEntityPillbox()
	{
		assertNotNull(world.addEntity(Pillbox.class, args));
		world.update();
	}

	@Test
	public void addEntityRoad()
	{
		assertNotNull(world.addEntity(Road.class, args));
		world.update();
	}

	@Test
	public void addEntityRubble()
	{
		assertNotNull(world.addEntity(Rubble.class, args));
		world.update();
	}

	@Test
	public void addEntitySwamp()
	{
		assertNotNull(world.addEntity(Swamp.class, args));
		world.update();
	}

	@Test
	public void addEntityTank()
	{
		assertNotNull(world.addEntity(Tank.class, args));
		world.update();
	}

	@Test
	public void addEntityTree()
	{
		assertNotNull(world.addEntity(Tree.class, args));
		world.update();
	}

	@Test
	public void addEntityWall()
	{
		assertNotNull(world.addEntity(Wall.class, args));
		world.update();
	}

	@Test
	public void addEntityWater()
	{
		assertNotNull(world.addEntity(Water.class, args));
		world.update();
	}

	@Test
	public void getEntity()
	{
		var t = world.addEntity(Tank.class, args);

		assertEquals(t, world.getEntity(t.id()));
		world.update();
	}

	@Test
	public void getEntities()
	{
		var t = world.addEntity(Tank.class, args);
		world.update();

		assertEquals(1, world.getEntities().size());
		assertTrue(world.getEntities().contains(t));
	}

	@Test
	public void removeEntity()
	{
		var t = world.addEntity(Tank.class, args);

		world.update();
		assertTrue(world.getEntities().contains(t));
		t.dispose();

		world.update();
		assertFalse(world.getEntities().contains(t));
	}

	@Test
	public void getTileColumnsAndWidth()
	{
		World w = new GameWorld(10, 50);
		assertEquals(10, w.getTileColumns());
		assertEquals(10 * Coords.TileToWorldScale, w.getWidth());
	}

	@Test
	public void getTileRowsAndHeight()
	{
		World w = new GameWorld(10, 50);
		assertEquals(50, w.getTileRows());
		assertEquals(50 * Coords.TileToWorldScale, w.getHeight());
	}
}
