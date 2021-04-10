package bubolo.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.Test;

import bubolo.world.Entity.ConstructionArgs;
import bubolo.world.GameWorld;
import bubolo.world.MineExplosion;
import bubolo.world.Tank;
import bubolo.world.World;

public class ControllersTest
{

	@Test
	public void testGetInstance()
	{
		assertNotNull(Controllers.getInstance());
	}

	@Test
	public void testCreateEntityControllerFactory()
	{
		World world = new GameWorld(10, 10);
		Controllers controllerSystem = Controllers.getInstance();
		// Pass mine explosion, since it does not have any controllers normally.
		var mineExplosion = world.addEntity(MineExplosion.class, new ConstructionArgs(UUID.randomUUID(), 0, 0, 0));
		assertFalse(controllerSystem.createController(mineExplosion, null));
	}

	@Test
	public void testCreateTankControllerFactory()
	{
		World world = new GameWorld(10, 10);
		Controllers controllerSystem = Controllers.getInstance();
		Tank tank = world.addEntity(Tank.class, new ConstructionArgs(UUID.randomUUID(), 0, 0, 0));
		assertTrue(controllerSystem.createController(tank, null));
	}

}
