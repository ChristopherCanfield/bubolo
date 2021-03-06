package bubolo.controllers.ai;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import bubolo.controllers.Controller;
import bubolo.world.World;
import bubolo.world.entity.concrete.Pillbox;


public class AIPillboxControllerTest
{
	@Test
	public void test()
	{
		Controller c = new AiPillboxController(mock(Pillbox.class));
		c.update(mock(World.class));
	}
}
