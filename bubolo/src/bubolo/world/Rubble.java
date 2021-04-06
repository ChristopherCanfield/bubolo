package bubolo.world;

/**
 * Rubble is created when structures (like Walls) are destroyed.
 *
 * @author BU CS673 - Clone Productions
 */
public class Rubble extends StaticEntity implements TerrainImprovement {
	private static final float speedModifier = 0.6f;
	private static final int width = 32;
	private static final int height = 32;

	/**
	 * Constructs a new Rubble.
	 *
	 * @param args the entity's construction arguments.
	 */
	protected Rubble(ConstructionArgs args) {
		super(args, width, height);
	}

	@Override
	public float speedModifier() {
		return speedModifier;
	}

	@Override
	public boolean isValidMinePlacementTarget() {
		return true;
	}
}
