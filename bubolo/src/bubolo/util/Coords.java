package bubolo.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

/**
 * Utility methods for working with coordinates.
 *
 * @author BU CS673 - Clone Productions
 */
public abstract class Coords
{
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
	 * @param camera
	 *            the game's camera.
	 * @param worldCoordinates
	 *            the coordinates to convert.
	 * @return the converted coordinates.
	 */
	public static Vector2 worldToCamera(Camera camera, Vector2 worldCoordinates)
	{
		return new Vector2(worldCoordinates.x - camera.position.x,
				worldCoordinates.y - camera.position.y);
	}

	/**
	 * Converts camera coordinates to world coordinates.
	 *
	 * @param camera
	 *            the game's camera.
	 * @param cameraCoordinates
	 *            the coordinates to convert.
	 * @return the converted coordinates.
	 */
	public static Vector2 cameraToWorld(Camera camera, Vector2 cameraCoordinates)
	{
		return new Vector2(cameraCoordinates.x + camera.position.x,
				cameraCoordinates.y + camera.position.y);
	}
}
