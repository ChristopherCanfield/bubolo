package bubolo.controllers.ai;

import com.badlogic.gdx.math.Intersector;

import bubolo.controllers.ActorEntityController;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.ChangeOwner;
import bubolo.world.World;
import bubolo.world.entity.concrete.Pillbox;
import bubolo.world.entity.concrete.Tank;

/**
 * A controller for pillboxes. This controller searches for a target, and fires when the pillbox
 * is ready.
 *
 * @author BU CS673 - Clone Productions
 */
public class AiPillboxController extends ActorEntityController<Pillbox>
{
	/**
	 * Constructs an AI Pillbox controller.
	 *
	 * @param pillbox the pillbox this controller will control.
	 */
	public AiPillboxController(Pillbox pillbox) {
		super(pillbox);
	}

	@Override
	public void update(World world)
	{
		// Only fire if cannon is ready.
		if (parent().isCannonReady()) {
			Tank target = getTarget(world);
			if (target != null) {
				fire(getTargetDirection(target), world);
			}
		}

		handleTankCapture(world);
	}

	/**
	 * Handles tank capturing of pillboxes.
	 *
	 * @param world the game world.
	 */
	private void handleTankCapture(World world) {
		var pillbox = parent();
		if (pillbox.hitPoints() <= 0) {
			for (Tank tank : world.getTanks()) {
				if (Intersector.overlapConvexPolygons(pillbox.captureBounds(), tank.bounds())) {
					pillbox.setOwner(tank);
					if(tank.isOwnedByLocalPlayer() && !pillbox.isOwnedByLocalPlayer()) {
						pillbox.setOwnedByLocalPlayer(true);
						sendNetUpdate(pillbox);
					}
				}
			}
		}
	}

	/**
	 * Finds a target for the pillbox.
	 *
	 * @param world reference to the game world
	 *
	 * @return target always the closest tank that is within range, or null if no tank is within range.
	 */
	private Tank getTarget(World world)
	{
		var pillbox = parent();

		Tank target = null;
		double targetDistance = Integer.MAX_VALUE;

		for (Tank tank : world.getTanks()) {
			// Don't attack the owner's tank, or hidden tanks.
			if (!tank.equals(pillbox.owner()) && !tank.isHidden()) {
				if (targetInRange(tank)) {
					double xdistance = Math.abs(pillbox.x() - tank.x());
					double ydistance = Math.abs(pillbox.y() - tank.y());
					double newTargetDistance = Math.sqrt((xdistance * xdistance) + (ydistance * ydistance));

					// Select the closest tank as the target.
					if (newTargetDistance < targetDistance) {
						target = tank;
						targetDistance = newTargetDistance;
					}
				}
			}
		}
		return target;
	}

	/**
	 * determine if the target tank is within range of the pillbox
	 *
	 * @param target
	 *            the tank the pillbox is targeting
	 * @return targetInRange returns true if the target is within range of this pillbox
	 */
	private boolean targetInRange(Tank target)
	{
		double xdistance = Math.abs(parent().x() - target.x());
		double ydistance = Math.abs(parent().y() - target.y());
		double distance = Math.sqrt((xdistance * xdistance) + (ydistance * ydistance));

		return (distance < parent().getRange());
	}

	/**
	 * returns the angle to the closest target for the pillbox
	 *
	 * @param target the Tank that the pillbox will target.
	 * @return the angle between this pillbox and the target.
	 */
	private float getTargetDirection(Tank target)
	{
		double xvector = target.x() - parent().x();
		double yvector = target.y() - parent().y();
		float direction = (float) Math.atan2(yvector, xvector);

		return direction;
	}

	/**
	 * tell the pillbox to aim and fire
	 *
	 * @param rotation
	 * @param world
	 */
	private void fire(float rotation, World world)
	{
		parent().aimCannon(rotation);
		parent().fireCannon(world);
	}

	private static void sendNetUpdate(Pillbox pillbox)
	{
		Network net = NetworkSystem.getInstance();
		net.send(new ChangeOwner(pillbox));
	}
}