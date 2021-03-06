package bubolo.controllers.ai;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import bubolo.controllers.Controller;
import bubolo.world.World;
import bubolo.world.entity.concrete.Base;


public class AIBaseControllerTest
{
	@Test
	public void test()
	{
		Controller c = new AiBaseController(mock(Base.class));
		c.update(mock(World.class));
	}
}
