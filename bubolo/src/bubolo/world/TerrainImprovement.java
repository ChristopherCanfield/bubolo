package bubolo.world;

import java.util.UUID;

import bubolo.util.Units;

/**
 * An object that can be built on top of terrain. Terrain improvements are mutually exclusive; that is, only
 * one type of terrain improvement may exist on a terrain at a time.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public interface TerrainImprovement {

	/**
	 * The terrain improvement's impact on tank movement speed, if any. If non-zero, this overrides the underlying
	 * terrain's movement speed impacts. If this terrain does not impact movement speed, leave it at the default value
	 * of zero.
	 *
	 * @return the terrain improvement's impact on tank movement speed, or zero if this does not affect tank movement speed.
	 */
	default float speedModifier() {
		return 0;
	}

	/**
	 * Specifies whether buildable objects can be built on top of (if the buildable object is not a terrain improvement),
	 * or replace (if the buildable object is a terrain improvement), this terrain improvement.
	 *
	 * @return true if buildable objects can be built in a tile that has this terrain improvement.
	 */
	boolean isValidBuildTarget();

	UUID id();

	/**
	 * @return The object's x position in world units.
	 */
	float x();
	/**
	 * @return The object's y position in world units.
	 */
	float y();

	/**
	 * @return the world column that the object is in.
	 */
	default int tileColumn() {
		return (int) x() / Units.TileToWorldScale;
	}

	/**
	 * @return the world row that the object is in.
	 */
	default int tileRow() {
		return (int) y() / Units.TileToWorldScale;
	}

	void dispose();
}
