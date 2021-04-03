package bubolo.world.entity.concrete;

import bubolo.util.TileUtil;
import bubolo.world.Adaptable;
import bubolo.world.Terrain;
import bubolo.world.World;

/**
 * Road is a Terrain type that offers tanks improved movement speed.
 *
 * @author BU CS673 - Clone Productions
 */
public class Road extends Terrain implements Adaptable
{
	private int tilingState = 0;

	/**
	 * Intended to be generic -- this is a list of all of the StationaryEntities classes that should
	 * result in a valid match when checking surrounding tiles to determine adaptive tiling state.
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
	 */
	public Road(ConstructionArgs args)
	{
		super(args, width, height, speedModifier);
	}

	@Override
	public void updateTilingState(World w)
	{
		tilingState = TileUtil.getTilingState(this, w, matchingTypes);
	}

	@Override
	public int getTilingState()
	{
		return tilingState;
	}
}
