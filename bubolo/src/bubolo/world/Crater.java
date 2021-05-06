package bubolo.world;

import java.util.UUID;

import bubolo.util.TileUtil;

/**
 * Craters are created when a mine explodes. They reduce Tank movement speed, and will flood if adjacent to water.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Crater extends StaticEntity implements TerrainImprovement, Adaptable {
	private byte tilingState = 0;

	/**
	 * An array containing the classes that result in a valid match when determining adaptive tiling state.
	 * TODO (cdc - 2021-04-05): This affects only the visualization, and probably should not be in this class.
	 */
	private Class<?>[] matchingTypes = new Class[] { Crater.class, Water.class };

	private static final TerrainTravelSpeed terrainTravelSpeed = TerrainTravelSpeed.VerySlow;

	private static final int width = 32;
	private static final int height = 32;

	protected Crater(ConstructionArgs args, World world) {
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

	@Override
	public void updateTilingState(World w) {
		tilingState = TileUtil.getTilingState(this, w, matchingTypes);
	}

	@Override
	public byte getTilingState() {
		return tilingState;
	}

	/**
	 * Disposes the crater, and replaces it and its underlying terrain with a water tile. If the crater is already
	 * disposed, this is a no-op.
	 *
	 * @param world reference to the game world.
	 */
	public void replaceWithWater(World world) {
		if (!isDisposed()) {
			dispose();

			var terrain = world.getTerrain(tileColumn(), tileRow());
			terrain.dispose();

			var args = new Entity.ConstructionArgs(UUID.randomUUID(), x(), y(), 0);
			world.addEntity(Water.class, args);
		}
	}
}