/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.net.command;

import java.util.UUID;
import java.util.logging.Logger;

import bubolo.Config;
import bubolo.controllers.ControllerFactory;
import bubolo.net.NetworkCommand;
import bubolo.util.GameLogicException;
import bubolo.world.ActorEntity;
import bubolo.world.Entity;
import bubolo.world.World;

/**
 * Generic entity creator for the network.
 *
 * @author BU CS673 - Clone Productions
 */
public class CreateEntity extends NetworkCommand
{
	private static final long serialVersionUID = 1L;

	private final Class<? extends Entity> type;
	private final UUID id;

	private final int x;
	private final int y;

	private final float rotation;

	private final ControllerFactory factory;

	/**
	 * Constructs a CreateEntity object.
	 *
	 * @param type
	 *            the entity's class.
	 * @param id
	 *            the entity's unique id.
	 * @param x
	 *            the entity's x position.
	 * @param y
	 *            the entity's y position.
	 * @param rotation
	 *            the entity's rotation.
	 */
	public CreateEntity(Class<? extends Entity> type, UUID id, float x, float y, float rotation)
	{
		this.type = type;
		this.id = id;
		this.x = (int)x;
		this.y = (int)y;
		this.rotation = rotation;
		this.factory = null;
	}

	/**
	 * Constructs a CreateEntity object.
	 *
	 * @param type
	 *            the entity's class.
	 * @param id
	 *            the entity's unique id.
	 * @param x
	 *            the entity's x position.
	 * @param y
	 *            the entity's y position.
	 * @param rotation
	 *            the entity's rotation.
	 * @param factory
	 *            factory for adding custom controllers to this entity.
	 */
	public CreateEntity(Class<? extends Entity> type, UUID id, float x, float y, float rotation, ControllerFactory factory)
	{
		this.type = type;
		this.id = id;
		this.x = (int)x;
		this.y = (int)y;
		this.rotation = rotation;
		this.factory = factory;
	}

	@Override
	protected void execute(World world)
	{
		try
		{
			var args = new Entity.ConstructionArgs(id, x, y, rotation);

			Entity entity;
			entity = world.addEntity(type, args, factory);

			if (entity instanceof ActorEntity actor)
			{
				actor.setOwnedByLocalPlayer(false);
			}
		}
		catch (GameLogicException e)
		{
			Logger.getLogger(Config.AppProgramaticTitle).severe("CreateEntity net command: Entity was not created. ID: " + id);
		}
	}

	/**
	 * Returns the entity's unique id.
	 * @return the entity's unique id.
	 */
	protected UUID getId()
	{
		return id;
	}
}
