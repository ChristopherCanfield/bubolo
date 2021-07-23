package bubolo.net.command;

import java.util.UUID;
import java.util.logging.Logger;

import bubolo.Config;
import bubolo.controllers.ControllerFactory;
import bubolo.net.NetworkCommand;
import bubolo.util.GameLogicException;
import bubolo.util.Nullable;
import bubolo.world.ActorEntity;
import bubolo.world.Entity;
import bubolo.world.World;

/**
 * Creates an actor entity on remote computers.
 *
 * @author Christopher D. Canfield
 */
public class CreateActor extends NetworkCommand {
	private static final long serialVersionUID = 1L;

	private final Class<? extends ActorEntity> type;
	private final UUID id;

	private final float x;
	private final float y;
	private final float rotation;

	private final @Nullable UUID ownerId;
	private final @Nullable ControllerFactory factory;

	/**
	 * Constructs a CreateActor command object.
	 *
	 * @param type the actor's class.
	 * @param id the actor's unique id.
	 * @param x the actor's x position.
	 * @param y the actor's y position.
	 * @param rotation the actor's rotation.
	 * @param ownerId [optional] the id of the entity that owns the actor. May be null.
	 */
	public CreateActor(Class<? extends ActorEntity> type, UUID id, float x, float y, float rotation, @Nullable UUID ownerId) {
		this.type = type;
		this.id = id;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.ownerId = ownerId;
		this.factory = null;
	}

	/**
	 * Constructs a CreateActor command object.
	 *
	 * @param type the actor's class.
	 * @param id the actor's unique id.
	 * @param x the actor's x position.
	 * @param y the actor's y position.
	 * @param rotation the actor's rotation.
	 * @param ownerId [optional] the id of the entity that owns the actor. May be null.
	 * @param factory [optional] a factory that attaches a controller to the entity. May be null.
	 */
	public CreateActor(Class<? extends ActorEntity> type, UUID id, float x, float y, float rotation,
			@Nullable UUID ownerId, @Nullable ControllerFactory factory) {
		this.type = type;
		this.id = id;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.ownerId = ownerId;
		this.factory = factory;
	}

	@Override
	protected void execute(World world) {
		var entity = (ActorEntity) world.addEntity(type, new Entity.ConstructionArgs(id, x, y, rotation), factory);

		if (ownerId != null) {
			try {
				ActorEntity owner = (ActorEntity) world.getEntity(ownerId);
				entity.setOwner(owner);
			} catch (GameLogicException e) {
				Logger.getLogger(Config.AppProgramaticTitle)
						.severe("CreateActor net command: Setting the entity's owner failed. Entity type: " + type);
			}
		}
	}

	protected UUID getId() {
		return id;
	}
}
