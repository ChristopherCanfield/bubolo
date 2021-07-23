package bubolo.net.command;

import java.util.UUID;
import java.util.logging.Logger;

import bubolo.Config;
import bubolo.controllers.ControllerFactory;
import bubolo.net.NetworkCommand;
import bubolo.util.GameLogicException;
import bubolo.util.Nullable;
import bubolo.world.Entity;
import bubolo.world.World;

/**
 * Notifies the network that an entity has been created.
 *
 * @author Christopher D. Canfield
 */
public class CreateEntity extends NetworkCommand {
	private static final long serialVersionUID = 1L;

	private final Class<? extends Entity> type;
	private final UUID id;

	private final short x;
	private final short y;

	private final ControllerFactory factory;

	/**
	 * Constructs a CreateEntity object using Entity construction args. The x and y positions are truncated to unsigned shorts,
	 * and the rotation is set to zero.
	 *
	 * @param type the entity's class.
	 * @param constructionArgs the entity's construction arguments.
	 */
	public CreateEntity(Class<? extends Entity> type, Entity.ConstructionArgs constructionArgs) {
		this(type, constructionArgs.id(), (short) constructionArgs.x(), (short) constructionArgs.y());
	}

	/**
	 * Constructs a CreateEntity object.
	 *
	 * @param type the entity's class.
	 * @param id the entity's unique id.
	 * @param x the entity's x position.
	 * @param y the entity's y position.
	 */
	public CreateEntity(Class<? extends Entity> type, UUID id, short x, short y) {
		this(type, id, x, y, null);
	}

	/**
	 * Constructs a CreateEntity object with an attached controller factory.
	 *
	 * @param type the entity's class.
	 * @param id the entity's unique id.
	 * @param x the entity's x position.
	 * @param y the entity's y position.
	 * @param factory [optional] factory for adding custom controllers to this entity. Can be null.
	 */
	public CreateEntity(Class<? extends Entity> type, UUID id, short x, short y, @Nullable ControllerFactory factory) {
		this.type = type;
		this.id = id;
		this.x = x;
		this.y = y;
		this.factory = factory;
	}

	@Override
	protected void execute(World world) {
		try {
			var args = new Entity.ConstructionArgs(id, Short.toUnsignedInt(x), Short.toUnsignedInt(y), 0);
			world.addEntity(type, args, factory);
		} catch (GameLogicException e) {
			Logger.getLogger(Config.AppProgramaticTitle).severe("CreateEntity net command: Entity was not created. ID: " + id);
		}
	}

	/**
	 * Returns the entity's unique id.
	 *
	 * @return the entity's unique id.
	 */
	protected UUID getId() {
		return id;
	}
}
