package bubolo.world;

import com.badlogic.gdx.math.Polygon;

/**
 * Interface for objects that can be made solid for the purposes of collision.
 *
 * @author Christopher D. Canfield
 */
public interface Collidable {

	/**
	 * @return true if the object is solid.
	 */
	boolean isSolid();

	/**
	 * @return The collidable object's bounding polygon.
	 */
	Polygon bounds();

	/**
	 * Updates the object's bounding polygon.
	 */
	void updateBounds();
}
