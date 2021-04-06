package bubolo.world;

/**
 * Game world objects
 *
 * @author Christopher D. Canfield
 */
public class Terrain extends StaticEntity {

	private final float speedModifier;
	private TerrainImprovement improvement;

	protected Terrain(ConstructionArgs args, int width, int height, float speedModifier) {
		super(args, width, height);

		this.speedModifier = speedModifier;
	}

	public float speedModifier() {
		return speedModifier;
	}

	public TerrainImprovement improvement() {
		return improvement;
	}

	public void setImprovement(TerrainImprovement improvement) {
		this.improvement = improvement;
	}

	/**
	 * Whether this terrain can be built on.
	 *
	 * @return true if this terrain can be built on.
	 */
	public boolean isValidBuildTarget() {
		return true;
	}
}
