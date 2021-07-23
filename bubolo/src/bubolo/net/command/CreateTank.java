package bubolo.net.command;

import java.util.logging.Logger;

import bubolo.Config;
import bubolo.controllers.ControllerFactory;
import bubolo.controllers.net.NetworkTankController;
import bubolo.graphics.TeamColor;
import bubolo.world.ActorEntity;
import bubolo.world.Tank;
import bubolo.world.World;

/**
 * Creates a network-controlled tank on connected computers.
 *
 * @author Christopher D. Canfield
 */
public class CreateTank extends CreateActor {
	private static final long serialVersionUID = 1L;

	private final String playerName;
	private final TeamColor color;

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
		assert tank.teamColor() != null;

		this.playerName = tank.playerName();
		this.color = tank.teamColor();
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
