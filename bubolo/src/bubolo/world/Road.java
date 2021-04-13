package bubolo.world;

import bubolo.util.TileUtil;

/**
 * Road is a Terrain type that offers tanks improved movement speed.
 *
 * @author BU CS673 - Clone Productions
 */
public class Road extends Terrain implements Adaptable {
	private byte tilingState = 0;

	/**
	 * An array containing the classes that result in a valid match when determining adaptive tiling state.
	 * TODO (cdc - 2021-04-05): This affects only the visualization, and probably should not be in this class.
	 */
	private Class<?>[] matchingTypes = new Class[] { Road.class };

	/**
	 * Modifier field used to reset an objects cap speed while traversing this terrain type.
	 */
	private static final float speedModifier = 1.25f;

	private static final int width = 32;
	private static final int height = 32;

	/**
	 * Constructs a new Road.
	 *
	 * @param args the entity's construction arguments.
	 * @param world reference to the game world.
	 */
	protected Road(ConstructionArgs args, World world) {
		super(args, width, height, speedModifier);
	}

	@Override
	public void updateTilingState(World w) {
		tilingState = TileUtil.getTilingState(this, w, matchingTypes);
	}

	@Override
	public byte getTilingState() {
		return tilingState;
	}
}
