/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.net.command;

import java.util.UUID;
import java.util.logging.Logger;

import bubolo.Config;
import bubolo.net.WorldOwner;
import bubolo.util.GameLogicException;
import bubolo.world.ActorEntity;
import bubolo.world.Entity;
import bubolo.world.World;

/**
 * Creates an actor entity on remote computers.
 *
 * @author BU CS673 - Clone Productions
 */
public class CreateActor extends CreateEntity
{
	private static final long serialVersionUID = 1L;

	private final UUID ownerId;

	/**
	 * Constructs a CreateActor command object.
	 *
	 * @param type
	 *            the actor's class.
	 * @param id
	 *            the actor's unique id.
	 * @param x
	 *            the actor's x position.
	 * @param y
	 *            the actor's y position.
	 * @param rotation
	 *            the actor's rotation.
	 * @param ownerId
	 *            the id of the entity that owns the actor.
	 */
	public CreateActor(Class<? extends Entity> type, UUID id, float x, float y, float rotation, UUID ownerId)
	{
		super(type, id, x, y, rotation);
		this.ownerId = ownerId;
	}

	@Override
	public void execute(WorldOwner worldOwner)
	{
		super.execute(worldOwner);

		try
		{
			World world = worldOwner.world();
			ActorEntity ownable = (ActorEntity) world.getEntity(getId());
			ActorEntity owner = (ActorEntity) world.getEntity(ownerId);
			ownable.setOwner(owner);
		}
		catch (GameLogicException e)
		{
			Logger.getLogger(Config.AppProgramaticTitle).severe("CreateOwnable net command: The entity was not created. ID: " + getId());
		}
	}
}
