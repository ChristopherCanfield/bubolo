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
import bubolo.world.Ownable;
import bubolo.world.World;
import bubolo.world.entity.Entity;

/**
 * Moves an entity in the world.
 *
 * @author BU CS673 - Clone Productions
 */
public class UpdateOwnable implements NetworkCommand
{
	private static final long serialVersionUID = 1L;

	private final UUID id;

	private final boolean isOwned;
	private final UUID ownerUID;

	/**
	 * Update the status of an ownable entity
	 * @param ownable
	 * 		the ownable entity to update
	 */

	public UpdateOwnable(Ownable ownable)
	{
		this.id = ownable.getId();
		this.isOwned = ownable.isOwned();
		this.ownerUID = ownable.getOwnerId();
	}

	@Override
	public void execute(World world)
	{
		try
		{
			Entity entity = world.getEntity(id);
			Ownable ownable = (Ownable)entity;
			ownable.setOwned(this.isOwned);
			ownable.setOwnerId(this.ownerUID);
		}
		catch (GameLogicException e)
		{
			Logger.getLogger(Config.AppProgramaticTitle).warning("UpdateOwnable net command: Unable to find ownable entity. ID: " + id);
		}
	}
}
