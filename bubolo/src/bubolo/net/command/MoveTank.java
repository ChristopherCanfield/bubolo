/**
 *
 */

package bubolo.net.command;

import java.util.UUID;

import bubolo.net.NetworkCommand;
import bubolo.world.World;
import bubolo.world.entity.concrete.Tank;

/**
 * Moves a tank and updates its speed.
 *
 * @author BU CS673 - Clone Productions
 */
public class MoveTank extends NetworkCommand
{
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final float speed;
	private final float x;
	private final float y;
	private final float rotation;

	/**
	 * Constructs a Move Tank command.
	 * @param tank the tank to move.
	 */
	public MoveTank(Tank tank)
	{
		this.id = tank.id();
		this.speed = tank.speed();
		this.x = tank.x();
		this.y = tank.y();
		this.rotation = tank.rotation();
	}

	@Override
	protected void execute(World world)
	{
		Tank tank = (Tank) world.getEntity(id);
		tank.setSpeed(new NetTankSpeed(speed));
		tank.setX(x);
		tank.setY(y);
		tank.setRotation(rotation);
	}
}
