package bubolo.graphics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;

import bubolo.world.Entity.ConstructionArgs;
import bubolo.world.GameWorld;
import bubolo.world.Tank;
import bubolo.world.World;

public class TankCameraControllerTest
{
	@Test
	public void testSetCameraHasCamera()
	{
		World world = new GameWorld(100, 100);
		Tank tank = world.addEntity(Tank.class, new ConstructionArgs(UUID.randomUUID(), 0, 0, 0));
		world.update();

		Camera camera = new OrthographicCamera();
		CameraController controller = new TankCameraController(tank);
		assertFalse(controller.hasCamera());
		controller.setCamera(camera);
		assertTrue(controller.hasCamera());
	}
}
