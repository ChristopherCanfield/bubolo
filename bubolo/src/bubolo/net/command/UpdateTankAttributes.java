/**
 *
 */

package bubolo.net.command;

import java.util.UUID;

import bubolo.net.NetworkCommand;
import bubolo.world.Tank;
import bubolo.world.World;

/**
 * Updates a tank's attributes.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class UpdateTankAttributes extends NetworkCommand {
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final float speed;
	private final float x;
	private final float y;
	private final float rotation;
	private final float health;
	private final boolean drowned;
	private final UUID carriedPillboxId;

	/**
	 * Constructs an Update Tank Attributes network command.
	 *
	 * @param tank the tank to update.
	 */
	public UpdateTankAttributes(Tank tank) {
		this.id = tank.id();
		this.speed = tank.speed();
		this.x = tank.x();
		this.y = tank.y();
		this.rotation = tank.rotation();
		this.health = tank.hitPoints();
		this.drowned = tank.drowned();
		this.carriedPillboxId = tank.carriedPillboxId();
	}

	@Override
	protected void execute(World world) {
		Tank tank = (Tank) world.getEntity(id);
		tank.setNetAttributes(new NetTankAttributes(speed, health, carriedPillboxId));
		tank.setPosition(x, y);
		tank.setRotation(rotation);
		tank.setDrowning(drowned);
	}
}
