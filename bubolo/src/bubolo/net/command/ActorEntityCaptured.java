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
 * @author Christopher D. Canfield
 */
public class ActorEntityCaptured extends NetworkCommand {
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final UUID ownerId;

	/**
	 * Changes the owner of an actor.
	 *
	 * @param ownable the actor entity entity to update.
	 */
	public ActorEntityCaptured(ActorEntity ownable) {
		this.id = ownable.id();
		this.ownerId = (ownable.owner() != null) ? ownable.owner().id() : null;
	}

	@Override
	protected void execute(World world) {
		try {
			ActorEntity ownable = (ActorEntity) world.getEntity(id);
			ActorEntity newOwner = (ActorEntity) world.getEntityOrNull(ownerId);
			if (newOwner == null) {
				ownable.setOwner(newOwner);
			} else if (!newOwner.equals(ownable.owner())) {
				ownable.onCaptured(world, newOwner);
			}
		} catch (GameLogicException e) {
			Logger.getLogger(Config.AppProgramaticTitle)
					.warning("UpdateOwnable net command: Unable to find ownable entity. ID: " + id);
		}
	}
}
