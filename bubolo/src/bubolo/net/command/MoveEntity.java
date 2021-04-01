/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.net.command;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import bubolo.Config;
import bubolo.net.NetworkCommand;
import bubolo.net.WorldOwner;
import bubolo.util.GameLogicException;
import bubolo.world.ActorEntity;

/**
 * Moves an entity in the world.
 *
 * @author BU CS673 - Clone Productions
 */
public class MoveEntity implements NetworkCommand
{
	private static final long serialVersionUID = 1L;

	private final UUID id;

	private final float x;
	private final float y;

	private final float rotation;

	/**
	 * Constructs a MoveEntity object.
	 *
	 * @param entity
	 *            the entity to move.
	 */
	public MoveEntity(ActorEntity entity)
	{
		this.id = entity.id();
		this.x = entity.x();
		this.y = entity.y();
		this.rotation = entity.rotation();
	}

	@Override
	public void execute(WorldOwner worldOwner)
	{
		try
		{
			ActorEntity entity = (ActorEntity) worldOwner.world().getEntity(id);
			entity.setX(x).setY(y).setRotation(rotation);
		}
		catch (GameLogicException e)
		{
			Logger.getLogger(Config.AppProgramaticTitle).log(Level.WARNING, "MoveEntity net command: Unable to find entity " + id);
		}
	}
}
