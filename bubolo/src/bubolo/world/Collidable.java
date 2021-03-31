package bubolo.world;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

/**
 * Interface for objects that can be used in collisions.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public interface Collidable {

	/**
	 * @return true if the object is solid. Two solid objects can't pass through each other.
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

	/**
	 * Whether this collidable object overlaps another.
	 *
	 * @param collidable a different collidable object.
	 * @return true if this collidable object overlaps the passed in collidable.
	 */
	default boolean overlapsEntity(Collidable collidable) {
		updateBounds();
		collidable.updateBounds();
		return Intersector.overlapConvexPolygons(bounds(), collidable.bounds());
	}
}
