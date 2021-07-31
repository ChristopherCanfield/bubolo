package bubolo.net;

import bubolo.world.World;

/**
 * Interface for network commands that interact with the game world. The {@code execute} method will be called exactly once when
 * it reaches the target player, so any processing, such as the creation of new Entities, should be performed in that method.
 * {@code NetworkGameCommands} are executed on the game thread.
 *
 * @author Christopher D. Canfield
 */
public interface NetworkGameCommand extends NetworkCommand {

	/**
	 * Called when this NetworkCommand reaches another player. Perform any processing, such as creating new Entities, in this
	 * method. References on one machine will not be valid on another, so instead of using references directly, you must get a
	 * reference to an entity by using its UUID:
	 * <p>
	 * {@code Entity entity = World.getEntity(id)}
	 * </p>
	 * Because of this, many {@code execute} implementations will require the target object's ID.
	 * <p>
	 * This method is called on the main game thread.
	 * </p>
	 *
	 * @param world reference to the game world.
	 */
	void execute(World world);
}
