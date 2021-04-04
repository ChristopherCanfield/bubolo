/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.net.command;

import java.util.logging.Logger;

import bubolo.Config;
import bubolo.controllers.ControllerFactory;
import bubolo.controllers.net.NetworkTankController;
import bubolo.world.ActorEntity;
import bubolo.world.World;
import bubolo.world.entity.concrete.Tank;

/**
 * Creates a network-controlled tank on connected computers.
 *
 * @author BU CS673 - Clone Productions
 */
public class CreateTank extends CreateEntity
{
	private static final long serialVersionUID = 1L;

	private final String playerName;

	/**
	 * @param tank
	 *            reference to the tank that should be created on network players' computers.
	 */
	public CreateTank(Tank tank)
	{
		super(Tank.class, tank.id(), tank.x(), tank.y(), tank.rotation(),
				new ControllerFactory() {
					private static final long serialVersionUID = 1L;

					@Override
					public void create(ActorEntity entity)
					{
						entity.setController(new NetworkTankController());
					}
				});

		assert tank.getPlayerName() != null;
		this.playerName = tank.getPlayerName();
	}

	@Override
	public void execute(World world)
	{
		super.execute(world);

		try
		{
			Tank tank = (Tank) world.getEntity(getId());
			tank.setPlayerName(playerName);
			tank.setOwnedByLocalPlayer(false);
		}
		catch (Exception e)
		{
			Logger.getLogger(Config.AppProgramaticTitle).severe("CreateTank net command: The tank was not created. ID: " + getId());
		}
	}
}
