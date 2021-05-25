package bubolo.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

/**
 * Utility methods for working with coordinates.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public abstract class Coords {
	/**
	 * The number of world units per tile.
	 */
	public static final int TileToWorldScale = 32;

	public static int worldUnitToTile(float worldUnit) {
		return (int) worldUnit / TileToWorldScale;
	}

	/**
	 * Converts world coordinates to camera coordinates.
	 *
	 * @param camera the game's camera.
	 * @param worldCoordinates the coordinates to convert.
	 * @return the converted coordinates.
	 */
	public static Vector2 worldToCamera(Camera camera, Vector2 worldCoordinates) {
		return worldToCamera(camera, worldCoordinates.x, worldCoordinates.y);
	}

	/**
	 * Converts world coordinates to camera coordinates.
	 *
	 * @param camera the game's camera.
	 * @param worldX the world x coordinate to convert.
	 * @param worldY the world y coordinate to convert.
	 * @return the converted coordinates.
	 */
	public static Vector2 worldToCamera(Camera camera, float worldX, float worldY) {
		return new Vector2(worldX - camera.position.x, worldY - camera.position.y);
	}

	/**
	 * Converts world coordinates to camera coordinates. This overload fills a passed-in instantiated Vector2, rather
	 * than returning a new Vector2.
	 *
	 * @param camera the game's camera.
	 * @param worldX the world x coordinate to convert.
	 * @param worldY the world y coordinate to convert.
	 * @param cameraCoordinatesOut an instantiated Vector2 that will be filled with the camera coordinates.
	 * @return reference to cameraCoordinatesOut.
	 */
	public static Vector2 worldToCamera(Camera camera, float worldX, float worldY, Vector2 cameraCoordinatesOut) {
		return cameraCoordinatesOut.set(worldX - camera.position.x, worldY - camera.position.y);
	}

	/**
	 * Converts camera coordinates to world coordinates.
	 *
	 * @param camera the game's camera.
	 * @param cameraCoordinates the coordinates to convert.
	 * @return the converted coordinates.
	 */
	public static Vector2 cameraToWorld(Camera camera, Vector2 cameraCoordinates) {
		return new Vector2(cameraCoordinates.x + camera.position.x, cameraCoordinates.y + camera.position.y);
	}
}
