package bubolo.world.entity.concrete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import bubolo.graphics.LibGdxAppTester;
import bubolo.mock.MockBulletCreator;
import bubolo.mock.MockMineCreator;
import bubolo.world.GameWorld;
import bubolo.world.Grass;
import bubolo.world.Mine;
import bubolo.world.Tank;
import bubolo.world.Tile;
import bubolo.world.World;
import bubolo.world.entity.EntityTestCase;

public class TankTest
{
	private Tank tank;
	private World world;
	private Grass grass;

	/**
	 * Constructs a Tank object and sets the default parameters.
	 */
	@Before
	public void setup()
	{
		world = new GameWorld(32, 32);
		tank = (Tank) world.addEntity(Tank.class).setTransform(16, 16, 0);
		grass = new Grass();
		Tile[][] grassTile = new Tile[1][1];
		grassTile[0][0] = new Tile(0,0,grass);
		world.setTiles(grassTile);
		EntityTestCase.setTestParams(tank);
	}

	@Test
	public void isHidden()
	{
		assertEquals(false, tank.isHidden());
	}

	@Test
	public void getSpeed()
	{
		assertEquals(0, (int) tank.getSpeed());
	}

	@Test
	public void fireCannon()
	{
		LibGdxAppTester.createApp();
		tank.fireCannon(new MockBulletCreator(), 50, 40);
	}

	@Test
	public void accelerate()
	{
		float speed = tank.getSpeed();
		tank.accelerate();
		assertEquals(speed + 0.01f, tank.getSpeed(), 0.001f);
	}

	@Test
	public void decelerate()
	{
		assertEquals(0.f, tank.getSpeed(), 0.001f);

		tank.accelerate();
		float speed = (tank.getSpeed() - 0.02f < 0.f) ? 0.f : tank.getSpeed();
		tank.decelerate();
		assertEquals(speed, tank.getSpeed(), 0.001f);
	}

	@Test
	public void isCannonReady()
	{
		LibGdxAppTester.createApp();
		assertTrue(tank.isCannonReady());
		tank.fireCannon(new MockBulletCreator(), 50, 40);
		assertFalse(tank.isCannonReady());
	}

	@Test
	public void rotateLeft()
	{
		tank.setRotation((float) Math.PI / 2);
		float rotation = tank.getRotation();
		tank.rotateLeft();
		assertEquals((rotation - 0.05f), tank.getRotation(), 0.0001f);
	}

	@Test
	public void rotateRight()
	{
		tank.setRotation((float) Math.PI / 2);
		float rotation = tank.getRotation();
		tank.rotateRight();
		assertEquals(rotation + 0.05f, tank.getRotation(), 0.0001f);
	}

	@Test
	public void  getHitPoints()
	{
		assertEquals(100, tank.hitPoints(), 0);
	}

	@Test
	public void getMaxHitPoints()
	{
		assertEquals(100, tank.maxHitPoints(), 0);
	}

	@Test
	public void getAmmoCount()
	{
		assertEquals(100, tank.ammoCount(), 0);
	}

	@Test
	public void getMineCount()
	{
		assertEquals(10, tank.mineCount(), 0);
	}

	@Test
	public void isAlive()
	{
		assertTrue(tank.isAlive());
		tank.takeHit(200);
		assertFalse(tank.isAlive());
	}

	@Test
	public void takeHit()
	{
		tank.takeHit(20);
		assertEquals(80, tank.hitPoints(), 0);
	}

	@Test
	public void heal()
	{
		tank.takeHit(20);
		tank.heal(5);
		assertEquals(85, tank.hitPoints(), 0);
	}

	@Test
	public void gatherMine()
	{
		tank.collectMines(1);
		assertEquals(Tank.maxMine, tank.mineCount(), 0);
	}

	@Test
	public void gatherAmmo()
	{
		tank.collectAmmo(10);
		assertEquals(100, tank.ammoCount(), 0);
	}

	@Test
	public void dropMine()
	{
		MockMineCreator m = new MockMineCreator();
		Mine mine = tank.dropMine(m, 16, 16);
		assertNotNull(mine);
		mine = tank.dropMine(m, 16, 16);
		assertNull(mine);
	}

	@Test
	public void setOwner()
	{
		tank.setOwnerId(tank.getId());
		assertEquals(tank.getId(), tank.getOwnerId());
	}
	@Test
	public void getOwner()
	{
		tank.setOwnerId(tank.getId());
		assertEquals(tank.getId(), tank.getOwnerId());
	}

	@Test
	public void getMax()
	{
		assertEquals(100, tank.maxHitPoints(), 0);
		assertEquals(100, tank.maxAmmo(), 0);
		assertEquals(10, tank.maxMines(), 0);
	}
}
