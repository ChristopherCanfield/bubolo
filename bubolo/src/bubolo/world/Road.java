package bubolo.world;

import bubolo.util.TileUtil;

/**
 * Roads increase tank movement speed.
 *
 * @author BU CS673 - Clone Productions
 */
public class Road extends Terrain implements EdgeMatchable {
	private byte tilingState = 0;

	/**
	 * An array containing the classes that result in a valid match when determining adaptive tiling state.
	 * TODO (cdc - 2021-04-05): This affects only the visualization, and probably should not be in this class.
	 */
	private static final Class<?>[] matchingTypes = new Class[] { Road.class };

	private static final TerrainTravelSpeed terrainTravelSpeed = TerrainTravelSpeed.Fast;

	private static final int width = 32;
	private static final int height = 32;

	/**
	 * Constructs a new Road.
	 *
	 * @param args the entity's construction arguments.
	 * @param world reference to the game world.
	 */
	protected Road(ConstructionArgs args, World world) {
		super(args, width, height, terrainTravelSpeed);
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
