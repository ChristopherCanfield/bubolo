/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.net.command;

import java.util.UUID;
import java.util.logging.Logger;

import bubolo.Config;
import bubolo.net.NetworkCommand;
import bubolo.util.GameLogicException;
import bubolo.world.ActorEntity;
import bubolo.world.World;

/**
 * Changes the owner of an actor entity.
 *
 * @author BU CS673 - Clone Productions
 */
public class ChangeOwner extends NetworkCommand
{
	private static final long serialVersionUID = 1L;

	private final UUID id;

	private final UUID ownerId;

	/**
	 * Updates the status of an actor entity.
	 *
	 * @param ownable the actor entity entity to update.
	 */

	public ChangeOwner(ActorEntity ownable)
	{
		this.id = ownable.id();
		this.ownerId = ownable.owner().id();
	}

	@Override
	protected void execute(World world)
	{
		try {
			ActorEntity ownable = (ActorEntity) world.getEntity(id);
			ActorEntity owner = (ActorEntity) world.getEntity(ownerId);

			ownable.setOwner(owner);

		} catch (GameLogicException e) {
			Logger.getLogger(Config.AppProgramaticTitle).warning("UpdateOwnable net command: Unable to find ownable entity. ID: " + id);
		}
	}
}
