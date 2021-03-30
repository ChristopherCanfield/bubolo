package bubolo.world.entity.concrete;

import bubolo.world.Adaptable;
import bubolo.world.Terrain;
import bubolo.world.World;

/**
 * Deep water is intended to act as a barrier to prevent Tanks from leaving the map.
 *
 * @author BU CS673 - Clone Productions
 */
public class DeepWater extends Terrain implements Adaptable
{
	private int tilingState = 0;

	private boolean[] cornerMatches = new boolean[4];

	/**
	 * Modifier field used to reset an objects cap speed while traversing this terrain type.
	 */
	private static final float speedModifier = 0.2f;

	private static final int width = 32;
	private static final int height = 32;

	/**
	 * Intended to be generic -- this is a list of all of the StationaryEntities classes that should
	 * result in a valid match when checking surrounding tiles to determine adaptive tiling state.
	 */
	private Class<?>[] matchingTypes = new Class[] { Water.class };

	public DeepWater(ConstructionArgs args)
	{
		super(args, width, height, speedModifier);
	}

	/**
	 * Return an array of booleans representing whether the tiles along the corners of this object's
	 * tile contain a matching object for the adaptive tiling procedure.
	 *
	 * @return an array of booleans, where the elements represent whether a matching object was
	 *         found to the top left, top right, bottom left, and bottom right of this obect, in
	 *         order.
	 */
	public boolean[] getCornerMatches()
	{
		return cornerMatches;
	}

	@Override
	public void updateTilingState(World w)
	{
//		if (getTile() != null)
//		{
//			tilingState = TileUtil.getTilingState(getTile(), w, matchingTypes);
//			cornerMatches = TileUtil.getCornerMatches(getTile(), w, matchingTypes);
//		}
//		else
//		{
//			tilingState = 0;
//		}
	}

	@Override
	public int getTilingState()
	{
		return tilingState;
	}
}
