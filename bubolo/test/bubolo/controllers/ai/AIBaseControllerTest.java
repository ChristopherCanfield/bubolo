package bubolo.controllers.ai;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import bubolo.controllers.Controller;
import bubolo.world.Base;
import bubolo.world.World;


public class AIBaseControllerTest
{
	@Test
	public void test()
	{
		Controller c = new AiBaseController(mock(Base.class));
		c.update(mock(World.class));
	}
}
