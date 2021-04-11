package bubolo.world;

/**
 * Tank spawn location.
 *
 * @author BU CS673 - Clone Productions
 */
public class Spawn extends StaticEntity {
	private static final int width = 32;
	private static final int height = 32;

	/**
	 * Constructs a new Spawn.
	 *
	 * @param args the entity's construction arguments.
	 * @param world reference to the game world.
	 */
	protected Spawn(ConstructionArgs args, World world) {
		super(args, width, height);
	}
}
