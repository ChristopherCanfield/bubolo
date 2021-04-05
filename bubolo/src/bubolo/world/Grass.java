package bubolo.world;

/**
 * Movement speed on grass is normal.
 *
 * @author BU CS673 - Clone Productions
 */
public class Grass extends Terrain {
	/**
	 * Modifier field used to reset an objects cap speed while traversing this terrain type.
	 */
	private static final float speedModifier = 1.0f;

	private static final int width = 32;
	private static final int height = 32;

	protected Grass(ConstructionArgs args) {
		super(args, width, height, speedModifier);
	}
}
