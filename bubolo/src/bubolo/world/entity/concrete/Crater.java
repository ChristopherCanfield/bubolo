package bubolo.world.entity.concrete;

import bubolo.world.Adaptable;
import bubolo.world.StaticEntity;
import bubolo.world.TerrainImprovement;
import bubolo.world.World;

/**
 * Craters are created when another Terrain type is blown up using a Mine. They reduce Tank movement
 * speed and may be flooded upon contact with Water.
 *
 * @author BU CS673 - Clone Productions
 */
public class Crater extends StaticEntity implements TerrainImprovement, Adaptable
{
	private int tilingState = 0;

	/**
	 * Intended to be generic -- this is a list of all of the StationaryEntities classes that should
	 * result in a valid match when checking surrounding tiles to determine adaptive tiling state.
	 */
	private Class<?>[] matchingTypes = new Class[] { Crater.class, Water.class };

	private static final float speedModifier = 1.25f;

	private static final int width = 32;
	private static final int height = 32;

	public Crater(ConstructionArgs args)
	{
		super(args, width, height);
	}

	@Override
	public float speedModifier() {
		return speedModifier;
	}

	@Override
	public void updateTilingState(World w)
	{
		//var tile = getTile();
		//tilingState = (tile != null) ? TileUtil.getTilingState(tile, w, matchingTypes) : 0;
	}

	@Override
	public int getTilingState() {
		return tilingState;
	}
}
