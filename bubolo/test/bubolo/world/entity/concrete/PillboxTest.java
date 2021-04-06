package bubolo.world.entity.concrete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import bubolo.graphics.LibGdxAppTester;
import bubolo.mock.MockBulletCreator;
import bubolo.world.Pillbox;
import bubolo.world.entity.EntityTestCase;

public class PillboxTest
{
	static Pillbox pillbox;

	/**
	 * Constructs a Pillbox object and sets the default parameters.
	 */
	@BeforeClass
	public static void setup()
	{
		pillbox = new Pillbox();
		EntityTestCase.setTestParams(pillbox);
	}

	@Test
	public void isLocalPlayer()
	{
		pillbox.setLocalPlayer(true);
		assertEquals("Pillbox local player ownership set correctly.", true, pillbox.isLocalPlayer());
	}

	@Test
	public void isOwned()
	{
		pillbox.setOwnerId(UUID.randomUUID());
		assertTrue("Pillbox ownership state set correctly.", pillbox.isOwned());
	}
	@Test
	public void aimCannon()
	{
		float direction = 60;
		pillbox.aimCannon(direction);
		assertEquals("Pillbox aimed correctly", true, pillbox.getCannonRotation() == direction);
	}

	@Test
	public void getCannonRotation()
	{
		float direction = 60;
		pillbox.aimCannon(direction);
		assertEquals("Pillbox aimed correctly", true, pillbox.getCannonRotation() == direction);

	}
	@Test
	public void isCannonReady()
	{
		LibGdxAppTester.createApp();
		Pillbox p = new Pillbox();
		assertTrue(p.isCannonReady());
		p.fireCannon(new MockBulletCreator());
		assertFalse(p.isCannonReady());

	}
	@Test
	public void fireCannon()
	{
		LibGdxAppTester.createApp();
		pillbox.fireCannon(new MockBulletCreator());

	}
	@Test
	public void getRange()
	{
		double Range = 30.0;
		pillbox.setRange(Range);
		assertEquals("Pillbox range set correctly", true, pillbox.getRange() == Range);
	}
	@Test
	public void setRange()
	{
		double Range = 30.0;
		pillbox.setRange(Range);
		assertEquals("Pillbox range set correctly", true, pillbox.getRange() == Range);
	}
	@Test
	public void  getHitPoints()
	{
		assertEquals(100, pillbox.hitPoints(), 0);
	}

	@Test
	public void getMaxHitPoints()
	{
		assertEquals(100, pillbox.maxHitPoints(), 0);
	}

	@Test
	public void healDamageTest()
	{
		pillbox.takeHit(1);
		assertEquals(99, pillbox.hitPoints(), 0);
		pillbox.heal(1);
		assertEquals(100, pillbox.hitPoints(), 0);
	}

	@Test
	public void setOwner()
	{
		pillbox.setOwnerId(pillbox.getId());
		assertEquals(pillbox.getId(), pillbox.getOwnerId());
	}
	@Test
	public void getOwner()
	{
		pillbox.setOwnerId(pillbox.getId());
		assertEquals(pillbox.getId(), pillbox.getOwnerId());
	}
}
