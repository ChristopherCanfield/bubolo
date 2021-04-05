package bubolo.controllers;

import static org.junit.Assert.*;

import org.junit.Test;

import bubolo.world.Grass;
import bubolo.world.Tank;
import bubolo.world.Tree;

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
		Controllers controllerSystem = Controllers.getInstance();
		// Pass Grass entity, since it does not have any controllers normally.
		Grass grass = new Grass();
		controllerSystem.createController(grass, null);
		assertEquals(0, grass.getControllerCount());
	}

	@Test
	public void testCreateTankControllerFactory()
	{
		Controllers controllerSystem = Controllers.getInstance();
		Tank tank = new Tank();
		controllerSystem.createController(tank, null);
		assertEquals(1, tank.getControllerCount());
	}

}
