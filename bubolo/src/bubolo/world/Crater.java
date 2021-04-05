package bubolo.world;

import bubolo.util.TileUtil;

/**
 * Craters are created when another Terrain type is blown up using a Mine. They reduce Tank movement speed and may be flooded upon
 * contact with Water.
 *
 * @author BU CS673 - Clone Productions
 */
public class Crater extends StaticEntity implements TerrainImprovement, Adaptable {
	private int tilingState = 0;

	/**
	 * An array containing the classes that result in a valid match when determining adaptive tiling state. TODO (cdc -
	 * 2021-04-05): This affects only the visualization, and probably should not be in this class.
	 */
	private Class<?>[] matchingTypes = new Class[] { Crater.class, Water.class };

	private static final float speedModifier = 0.45f;

	private static final int width = 32;
	private static final int height = 32;

	protected Crater(ConstructionArgs args) {
		super(args, width, height);
	}

	@Override
	public float speedModifier() {
		return speedModifier;
	}

	@Override
	public void updateTilingState(World w) {
		tilingState = TileUtil.getTilingState(this, w, matchingTypes);
	}

	@Override
	public int getTilingState() {
		return tilingState;
	}
}
