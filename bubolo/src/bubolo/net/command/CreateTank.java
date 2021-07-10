/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.net.command;

import java.util.logging.Logger;

import com.badlogic.gdx.graphics.Color;

import bubolo.Config;
import bubolo.controllers.ControllerFactory;
import bubolo.controllers.net.NetworkTankController;
import bubolo.world.ActorEntity;
import bubolo.world.Tank;
import bubolo.world.World;

/**
 * Creates a network-controlled tank on connected computers.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class CreateTank extends CreateActor {
	private static final long serialVersionUID = 1L;

	private final String playerName;
	private final Color color;

	/**
	 * @param tank reference to the tank that should be created on network players' computers.
	 */
	public CreateTank(Tank tank) {
		super(Tank.class, tank.id(), tank.x(), tank.y(), tank.rotation(), null, new ControllerFactory() {
			private static final long serialVersionUID = 1L;

			@Override
			public void create(ActorEntity entity) {
				entity.addController(new NetworkTankController());
			}
		});

		assert tank.playerName() != null;
		assert tank.color() != null;

		this.playerName = tank.playerName();
		this.color = tank.color();
	}

	@Override
	public void execute(World world) {
		super.execute(world);

		try {
			Tank tank = (Tank) world.getEntity(getId());
			tank.initialize(playerName, color, false);
		} catch (Exception e) {
			Logger.getLogger(Config.AppProgramaticTitle)
					.severe("CreateTank net command: The tank was not created. ID: " + getId());
		}
	}
}
