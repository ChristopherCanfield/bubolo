package bubolo.world.entity.concrete;

import bubolo.world.Terrain;

/**
 * Swamp Terrain can be traversed by Tanks, but will reduce movement speed.
 *
 * @author BU CS673 - Clone Productions
 */
public class Swamp extends Terrain
{
	/**
	 * Modifier field used to reset an objects cap speed while traversing this terrain type.
	 */
	private static final float speedModifier = 0.7f;

	private static final int width = 32;
	private static final int height = 32;

	public Swamp(ConstructionArgs args)
	{
		super(args, width, height, speedModifier);
	}
}
