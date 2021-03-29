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
import bubolo.world.Ownable;
import bubolo.world.Tile;
import bubolo.world.World;
import bubolo.world.entity.OldEntity;
import bubolo.world.entity.StationaryElement;
import bubolo.world.entity.Terrain;

/**
 * Generic entity creator for the network.
 *
 * @author BU CS673 - Clone Productions
 */
public class CreateEntity implements NetworkCommand
{
	private static final long serialVersionUID = 1L;

	private final Class<? extends OldEntity> type;
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
	public CreateEntity(Class<? extends OldEntity> type, UUID id, float x, float y, float rotation)
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
	public CreateEntity(Class<? extends OldEntity> type, UUID id, float x, float y, float rotation,
			ControllerFactory factory)
	{
		this.type = type;
		this.id = id;
		this.x = (int)x;
		this.y = (int)y;
		this.rotation = rotation;
		this.factory = factory;
	}

	@Override
	public void execute(World world)
	{
		try
		{
			OldEntity entity;
			if (factory == null) {
				entity = world.addEntity(type, id);
			} else {
				entity = world.addEntity(type, id, factory);
			}

			entity.setX(x).setY(y).setRotation(rotation);

			Tile tile = world.getTileFromWorldPosition(x, y);
			if (entity instanceof Terrain terrain) {
				tile.setTerrain(terrain, world);
			} else if (entity instanceof StationaryElement stationaryElement) {
				tile.setElement(stationaryElement, world);
			}

			if (entity instanceof Ownable)
			{
				((Ownable)entity).setLocalPlayer(false);
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
