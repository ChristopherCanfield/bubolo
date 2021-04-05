package bubolo.world.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import bubolo.world.Tank;

public class ActorTest
{
	static Actor act;

	/**
	 * Creates a Tank object and sets the starting parameters.
	 */
	@BeforeClass
	public static void setup()
	{
		act = new Tank();
		EntityTestCase.setTestParams(act);
	}

	@Test
	public void isLocalPlayer()
	{
		act.setLocalPlayer(true);
		assertTrue("Actor local player ownership set correctly.", act.isLocalPlayer());
	}

	@Test
	public void isOwned()
	{
		act.setOwnerId(UUID.randomUUID());
		assertTrue("Actor ownership state set correctly.", act.isOwned());
	}

	@Test
	public void constructId()
	{
		Actor act2 = new Tank(EntityTestCase.TARGET_UUID);
		assertEquals("Actor UUID set correctly.", EntityTestCase.TARGET_UUID, act2.getId());
	}
}
