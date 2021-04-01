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
import bubolo.net.WorldOwner;
import bubolo.util.GameLogicException;
import bubolo.world.Entity;
import bubolo.world.Ownable;

/**
 * Moves an entity in the world.
 *
 * @author BU CS673 - Clone Productions
 */
public class UpdateOwnable implements NetworkCommand
{
	private static final long serialVersionUID = 1L;

	private final UUID id;

	private final UUID ownerId;

	/**
	 * Update the status of an ownable entity
	 * @param ownable
	 * 		the ownable entity to update
	 */

	public UpdateOwnable(Ownable ownable)
	{
		this.id = ownable.getId();
		this.ownerId = ownable.getOwnerId();
	}

	@Override
	public void execute(WorldOwner worldOwner)
	{
		try
		{
			Entity entity = worldOwner.world().getEntity(id);
			Ownable ownable = (Ownable)entity;
			ownable.setOwnerId(this.ownerId);
		}
		catch (GameLogicException e)
		{
			Logger.getLogger(Config.AppProgramaticTitle).warning("UpdateOwnable net command: Unable to find ownable entity. ID: " + id);
		}
	}
}
