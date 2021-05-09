package bubolo.world;

import bubolo.util.TileUtil;

/**
 * Tanks that enter deep water immediately sink.
 *
 * @author BU CS673 - Clone Productions
 */
public class DeepWater extends Terrain implements EdgeMatchable {
	private byte tilingState = 0;

	private final boolean[] cornerMatches = new boolean[4];

	private static final TerrainTravelSpeed terrainTravelSpeed = TerrainTravelSpeed.VerySlow;

	private static final int width = 32;
	private static final int height = 32;

	/**
	 * An array containing the classes that result in a valid match when determining adaptive tiling state.
	 * TODO (cdc - 2021-04-05): This affects only the visualization, and probably should not be in this class.
	 */
	private static final Class<?>[] matchingTypes = new Class[] { Water.class };

	protected DeepWater(ConstructionArgs args, World world) {
		super(args, width, height, terrainTravelSpeed.speedModifier);
	}

	@Override
	public boolean isValidBuildTarget() {
		return false;
	}

	/**
	 * Return an array of booleans representing whether the tiles along the corners of this object's tile contain a matching
	 * object for the adaptive tiling procedure.
	 *
	 * @return an array of booleans, where the elements represent whether a matching object was found to the top left, top right,
	 *         bottom left, and bottom right of this object, in order.
	 */
	public boolean[] getCornerMatches() {
		return cornerMatches;
	}

	@Override
	public void updateTilingState(World w) {
		tilingState = TileUtil.getTilingState(this, w, matchingTypes);
		TileUtil.getCornerMatches(cornerMatches, this, w, matchingTypes);
	}

	@Override
	public byte getTilingState() {
		return tilingState;
	}
}
