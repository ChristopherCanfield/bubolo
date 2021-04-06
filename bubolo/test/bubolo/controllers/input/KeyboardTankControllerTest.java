package bubolo.controllers.input;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import bubolo.controllers.Controller;
import bubolo.world.Tank;
import bubolo.world.World;


public class KeyboardTankControllerTest
{
	@Test
	public void test()
	{
		Controller c = new KeyboardTankController(mock(Tank.class));
		c.update(mock(World.class));
	}
}
