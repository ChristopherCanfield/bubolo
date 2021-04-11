package bubolo.graphics;

import static com.google.common.base.Preconditions.checkNotNull;

import com.badlogic.gdx.graphics.Camera;

import bubolo.world.Tank;
import bubolo.world.World;

/**
 * Controller that moves the camera based on the tank's position.
 *
 * @author BU CS673 - Clone Productions
 */
class TankCameraController implements CameraController
{
	private Tank tank;
	private Camera camera;

	/**
	 * Constructs a TankCameraController. Package-private because TankCameraControllers are internal
	 * to the Graphics system.
	 *
	 * @param tank the tank that the camera will follow.
	 */
	TankCameraController(Tank tank)
	{
		this.tank = checkNotNull(tank);
	}

	@Override
	public void setCamera(Camera camera)
	{
		this.camera = checkNotNull(camera);
	}

	@Override
	public boolean hasCamera()
	{
		return (camera != null);
	}

	@Override
	public void update(World world)
	{
		if (camera == null)
		{
			throw new IllegalStateException("No camera has been set for this TankCameraController.");
		}

		float tankX = calculateCameraX(camera, tank, world);
		float tankY = calculateCameraY(camera, tank, world);

		// The libgdx camera's position is from the bottom left corner:
		// https://github.com/libgdx/libgdx/wiki/Orthographic-camera
		camera.position.set(Math.round(tankX), Math.round(tankY), 0.f);
		camera.update();
	}

	private static float calculateCameraX(Camera camera, Tank tank, World world)
	{
		float tankX = tank.x();

		float cameraX = tankX - camera.viewportWidth / 2.f;
		if (cameraX < 0)
		{
			cameraX = 0;
		}
		else if (cameraX > world.getWidth() - camera.viewportWidth)
		{
			// Ensure that screen doesn't go negative if the world is smaller than the camera.
			float newCameraX = world.getWidth() - camera.viewportWidth;
			cameraX = (newCameraX >= 0) ? newCameraX : 0;
		}

		return cameraX;
	}

	private static float calculateCameraY(Camera camera, Tank tank, World world)
	{
		float tankY = tank.y();

		float cameraY = tankY - camera.viewportHeight / 2.f;
		if (cameraY < 0)
		{
			cameraY = 0;
		}
		else if (cameraY > world.getHeight() - camera.viewportHeight)
		{
			// Ensure that screen doesn't go negative if the world is smaller than the camera.
			float newCameraY = world.getHeight() - camera.viewportHeight;
			cameraY = (newCameraY >= 0) ? newCameraY : 0;
		}

		return cameraY;
	}
}
