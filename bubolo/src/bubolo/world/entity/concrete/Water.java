package bubolo.world.entity.concrete;

import bubolo.util.TileUtil;
import bubolo.world.Adaptable;
import bubolo.world.Terrain;
import bubolo.world.World;

/**
 * Water terrain can be crossed by a Tank, but can deal damage over time.
 *
 * @author BU CS673 - Clone Productions
 */
public class Water extends Terrain implements Adaptable
{
	private int tilingState = 0;

	private final boolean[] cornerMatches = new boolean[4];

	/**
	 * Intended to be generic -- this is a list of all of the StationaryEntities classes that should
	 * result in a valid match when checking surrounding tiles to determine adaptive tiling state.
	 */
	private static final Class<?>[] matchingTypes = new Class[] { Water.class, DeepWater.class };

	/**
	 * Modifier field used to reset an objects cap speed while traversing this terrain type.
	 */
	private static final float speedModifier = 0.3f;

	private static final int width = 32;
	private static final int height = 32;

	public Water(ConstructionArgs args)
	{
		super(args, width, height, speedModifier);
	}

	@Override
	public boolean isBuildable() {
		return false;
	}

	@Override
	public void updateTilingState(World w)
	{
		tilingState = TileUtil.getTilingState(this, w, matchingTypes);
		TileUtil.getCornerMatches(cornerMatches, this, w, matchingTypes);
	}

	/**
	 * Return an array of booleans representing whether the tiles along the corners of this Water's
	 * tile contain a matching object for the adaptive tiling procedure.
	 *
	 * @return an array of booleans, where the elements represent whether a matching object was
	 *         found to the top left, top right, bottom left, and bottom right of this object, in
	 *         order.
	 */
	public boolean[] getCornerMatches()
	{
		return cornerMatches;
	}

	@Override
	public int getTilingState()
	{
		return tilingState;
	}
}
