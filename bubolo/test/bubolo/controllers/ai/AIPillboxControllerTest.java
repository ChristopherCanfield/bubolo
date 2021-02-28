package bubolo.controllers.ai;

import static org.junit.Assert.*;

import org.junit.Test;

import bubolo.controllers.Controller;
import bubolo.controllers.ai.AiPillboxController;
import bubolo.graphics.LibGdxAppTester;
import bubolo.world.World;
import bubolo.world.entity.concrete.Pillbox;
import static org.mockito.Mockito.mock;


public class AIPillboxControllerTest
{
	@Test
	public void test()
	{
		LibGdxAppTester.createApp();
		Controller c = new AiPillboxController(mock(Pillbox.class));
		c.update(mock(World.class));
	}
}
