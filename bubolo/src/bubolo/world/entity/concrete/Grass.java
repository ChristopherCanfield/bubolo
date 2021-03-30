package bubolo.world.entity.concrete;

import bubolo.world.Terrain;

/**
 * Grass is the standard Terrain of B.U.B.O.L.O., and offers no special movement effects.
 *
 * @author BU CS673 - Clone Productions
 */
public class Grass extends Terrain
{
	/**
	 * Modifier field used to reset an objects cap speed while traversing this terrain type.
	 */
	private static final float speedModifier = 1.0f;

	private static final int width = 32;
	private static final int height = 32;

	public Grass(ConstructionArgs args)
	{
		super(args, width, height, speedModifier);
	}
}
