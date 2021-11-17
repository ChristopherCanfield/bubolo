package bubolo.graphics;

import com.badlogic.gdx.graphics.Camera;

import bubolo.world.TankPositionObserver;

/**
 * Controller that moves the camera based on the tank's position.
 *
 * @author BU CS673 - Clone Productions
 */
class TankCameraController implements TankPositionObserver {
	private final Camera camera;

	private int worldWidthPixels;
	private int worldHeightPixels;

	private boolean cameraPositionChanged;

	/**
	 * Constructs a TankCameraController. Package-private because TankCameraControllers are internal to the Graphics system.
	 */
	TankCameraController(Camera camera) {
		this.camera = camera;
	}

	void setWorldSize(int worldWidthPixels, int worldHeightPixels) {
		this.worldWidthPixels = worldWidthPixels;
		this.worldHeightPixels = worldHeightPixels;
	}

	boolean cameraPositionChanged() {
		return cameraPositionChanged;
	}

	void resetCameraPositionChanged() {
		cameraPositionChanged = false;
	}

	@Override
	public void onTankPositionChanged(float newX, float newY) {
		float newCameraX = calculateCameraX(newX, camera.viewportWidth, worldWidthPixels);
		float newCameraY = calculateCameraY(newY, camera.viewportHeight, worldHeightPixels);

		// The libgdx camera's position is from the bottom left corner:
		// https://github.com/libgdx/libgdx/wiki/Orthographic-camera
		camera.position.set(newCameraX, newCameraY, 0.f);
		camera.update();

		cameraPositionChanged = true;
	}

	private static float calculateCameraX(float tankX, float viewportWidth, int worldWidthPixels) {
		float cameraX = tankX - viewportWidth / 2.f;
		if (cameraX < 0) {
			cameraX = 0;
		} else if (cameraX > worldWidthPixels - viewportWidth) {
			// Ensure that screen doesn't go negative if the world is smaller than the camera.
			float newCameraX = worldWidthPixels - viewportWidth;
			cameraX = (newCameraX >= 0) ? newCameraX : 0;
		}

		return Math.round(cameraX);
	}

	private static float calculateCameraY(float tankY, float viewportHeight, int worldHeight) {
		float cameraY = tankY - viewportHeight / 2.f;
		if (cameraY < 0) {
			cameraY = 0;
		} else if (cameraY > worldHeight - viewportHeight) {
			// Ensure that screen doesn't go negative if the world is smaller than the camera.
			float newCameraY = worldHeight - viewportHeight;
			cameraY = (newCameraY >= 0) ? newCameraY : 0;
		}

		return Math.round(cameraY);
	}
}
