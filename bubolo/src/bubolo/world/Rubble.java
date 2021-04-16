package bubolo.world;

/**
 * Rubble is created when structures (like Walls) are destroyed.
 *
 * @author BU CS673 - Clone Productions
 */
public class Rubble extends StaticEntity implements TerrainImprovement {
	private static final TerrainTravelSpeed terrainTravelSpeed = TerrainTravelSpeed.VerySlow;
	private static final int width = 32;
	private static final int height = 32;

	/**
	 * Constructs a new Rubble.
	 *
	 * @param args the entity's construction arguments.
	 * @param world reference to the game world.
	 */
	protected Rubble(ConstructionArgs args, World world) {
		super(args, width, height);
	}

	@Override
	public float speedModifier() {
		return terrainTravelSpeed.speedModifier;
	}

	@Override
	public boolean isValidMinePlacementTarget() {
		return true;
	}
}
