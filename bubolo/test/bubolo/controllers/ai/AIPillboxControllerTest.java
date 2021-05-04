package bubolo.controllers.ai;

import java.util.UUID;

import org.junit.Test;

import bubolo.world.Entity;
import bubolo.world.GameWorld;
import bubolo.world.Pillbox;
import bubolo.world.World;


public class AIPillboxControllerTest
{
	@Test
	public void updateTest()
	{
		World world = new GameWorld(10, 10);
		var pillbox = world.addEntity(Pillbox.class, new Entity.ConstructionArgs(UUID.randomUUID(), 0, 0, 0));
		var controller = new AiPillboxController(pillbox);
		pillbox.addController(controller);
		controller.update(world);
	}
}
