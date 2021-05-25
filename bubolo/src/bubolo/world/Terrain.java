package bubolo.world;

/**
 * Game world objects
 *
 * @author Christopher D. Canfield
 */
public class Terrain extends StaticEntity {

	private final TerrainTravelSpeed terrainTravelSpeed;
	private TerrainImprovement improvement;

	protected Terrain(ConstructionArgs args, int width, int height, TerrainTravelSpeed terrainTravelSpeed) {
		super(args, width, height);

		this.terrainTravelSpeed = terrainTravelSpeed;
	}

	public float accelerationModifier() {
		return terrainTravelSpeed.accelerationModifier;
	}

	public float maxSpeedModifier() {
		return terrainTravelSpeed.maxSpeedModifier;
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

	/**
	 * @param terrain a terrain object.
	 * @return true if the terrain is a water type.
	 */
	public static boolean isWater(Terrain terrain) {
		return terrain instanceof Water || terrain instanceof DeepWater;
	}
}
