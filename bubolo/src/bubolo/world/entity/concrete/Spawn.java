package bubolo.world.entity.concrete;

import bubolo.world.StaticEntity;

/**
 * spawn is an entity that marks a tile for tanks to re spawn at after dying
 *
 * @author BU CS673 - Clone Productions
 */
public class Spawn extends StaticEntity
{
	private static final int width = 32;
	private static final int height = 32;

	/**
	 * Construct a new Spawn with a random UUID.
	 */
	public Spawn(ConstructionArgs args)
	{
		super(args, width, height);
	}
}
