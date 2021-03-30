package bubolo.world;

/**
 * An object that can be built on top of terrain. Terrain improvements are mutually exclusive; that is, only
 * one type of terrain improvement may exist on a terrain at a time.
 *
 * @author Christopher D. Canfield
 */
public interface TerrainImprovement {

	/**
	 * The terrain improvement's impact on tank movement speed, if any. If non-zero, this overrides the underlying
	 * terrain's movement speed impacts. If this terrain does not impact movement speed, leave it at the default value
	 * of zero.
	 *
	 * @return the terrain improvement's impact on tank movement speed, or zero if this does not affect tank movement speed.
	 */
	default public float speedModifier() {
		return 0;
	}
}
