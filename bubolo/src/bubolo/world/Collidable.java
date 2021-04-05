package bubolo.world;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

import bubolo.util.Nullable;

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
	 * @param collidable a collidable object to check against.
	 * @return true if this collidable object overlaps the passed in collidable.
	 */
	default boolean overlapsEntity(Collidable collidable) {
		return overlapsEntity(collidable, null);
	}

	/**
	 * Tests whether this collidable object overlaps another.
	 *
	 * @param collidable a collidable object to check against.
	 * @param collisionVector [optional] if present, this will be populated with the minimum magnitude vector required to push the
	 * entities apart.
	 * @return true if this collidable object overlaps the passed in collidable.
	 */
	default boolean overlapsEntity(Collidable collidable, @Nullable Intersector.MinimumTranslationVector collisionVector) {
		return Intersector.overlapConvexPolygons(bounds(), collidable.bounds(), collisionVector);
	}
}
