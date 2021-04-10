package bubolo.graphics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;

import bubolo.mock.MockTank;
import bubolo.mock.MockWorld;
import bubolo.world.Tank;

public class TankCameraControllerTest
{
	@Test
	public void testSetCamera()
	{
		Camera camera = new OrthographicCamera();
		CameraController controller = new TankCameraController(new MockTank());
		controller.setCamera(camera);
	}

	@Test
	public void testHasCameraFalse()
	{
		CameraController controller = new TankCameraController(new MockTank());
		assertFalse(controller.hasCamera());
	}

	@Test
	public void testHasCameraTrue()
	{
		Camera camera = new OrthographicCamera();
		CameraController controller = new TankCameraController(new MockTank());
		controller.setCamera(camera);
		assertTrue(controller.hasCamera());
	}

	@Test
	public void testUpdate()
	{
		Camera camera = new OrthographicCamera();
		CameraController controller = new TankCameraController(new MockTank());
		controller.setCamera(camera);
		controller.update(new MockWorld());
	}

	@Test
	public void testUpdateLessThanZero()
	{
		Camera camera = new OrthographicCamera(20, 30);
		Tank tank = new MockTank();
		tank.setPosition(-100, -120);
		CameraController controller = new TankCameraController(tank);
		controller.setCamera(camera);
		controller.update(new MockWorld());
	}

	@Test
	public void testUpdateGreaterThanWorldWidth()
	{
		Camera camera = new OrthographicCamera(20, 30);
		Tank tank = new MockTank();
		tank.setPosition(200, 200);
		CameraController controller = new TankCameraController(tank);
		controller.setCamera(camera);
		controller.update(new MockWorld());
	}
}
