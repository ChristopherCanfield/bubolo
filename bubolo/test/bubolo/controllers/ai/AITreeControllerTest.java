package bubolo.controllers.ai;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import bubolo.controllers.Controller;
import bubolo.graphics.Graphics;
import bubolo.world.GameWorld;


public class AITreeControllerTest
{
	@Test
	public void test()
	{
		Graphics graphics = mock(Graphics.class);
		Controller c = new AiTreeController();
		GameWorld world = new GameWorld(graphics, 100, 100);
		world.setSpriteLoading(false);
		c.update(world);
	}
}
