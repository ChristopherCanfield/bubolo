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
import bubolo.world.World;
import bubolo.world.entity.concrete.Bullet;

/**
 * Creates a bullet on remote computers.
 *
 * @author BU CS673 - Clone Productions
 */
public class CreateBullet extends CreateEntity
{
	private static final long serialVersionUID = 1L;

	private final UUID parentId;

	/**
	 * Constructs a CreateBullet object.
	 *
	 * @param id
	 *            the bullet's unique id.
	 * @param x
	 *            the bullet's x position.
	 * @param y
	 *            the bullet's y position.
	 * @param rotation
	 *            the bullet's rotation.
	 * @param parentId
	 *            the id of the entity that created the bullet.
	 */
	public CreateBullet(UUID id, float x, float y, float rotation, UUID parentId)
	{
		super(Bullet.class, id, x, y, rotation);
		this.parentId = parentId;
	}

	@Override
	public void execute(WorldOwner worldOwner)
	{
		super.execute(worldOwner);

		try
		{
			World world = worldOwner.world();
			Bullet bullet = (Bullet) world.getEntity(getId());
			ActorEntity parent = (ActorEntity) world.getEntity(parentId);
			bullet.setOwner(parent);
		}
		catch (GameLogicException e)
		{
			Logger.getLogger(Config.AppProgramaticTitle).severe("CreateBullet net command: The bullet was not created. ID: " + getId());
		}
	}
}
