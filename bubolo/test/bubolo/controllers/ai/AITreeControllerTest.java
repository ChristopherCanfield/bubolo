package bubolo.controllers.ai;

import org.junit.Test;

import bubolo.controllers.Controller;
import bubolo.world.GameWorld;


public class AITreeControllerTest
{
	@Test
	public void test()
	{
		Controller c = new AiTreeController();
		GameWorld world = new GameWorld(100, 100);
		c.update(world);
	}
}
