package bubolo.controllers.ai;

import bubolo.controllers.Controller;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.UpdateOwnable;
import bubolo.util.TileUtil;
import bubolo.world.World;
import bubolo.world.entity.Entity;
import bubolo.world.entity.concrete.Pillbox;
import bubolo.world.entity.concrete.Tank;

/**
 * A controller for pillboxes. This controller automatically finds a target within range
 * of the pillbox and fires based on the set reload speed.
 *
 * @author BU CS673 - Clone Productions
 */
public class AiPillboxController implements Controller
{
	private final Pillbox pillbox;

	/**
	 * constructs an AI Pillbox controller
	 *
	 * @param pillbox
	 *            the pillbox this controller will correspond to.
	 */
	public AiPillboxController(Pillbox pillbox)
	{
		this.pillbox = pillbox;
	}

	@Override
	public void update(World world)
	{
		// Only fire if cannon is ready and the pillbox has more than 0 health.
		if (pillbox.isCannonReady() && pillbox.getHitPoints() > 0) {
			Tank target = getTarget(world);
			if (target != null) {
				fire(getTargetDirection(target), world);
			}
		}

		if(!pillbox.isOwned() && pillbox.getHitPoints() <= 0)
		{
			for(Entity entity : TileUtil.getLocalCollisions(pillbox, world))
			{
				if (entity instanceof Tank)
				{
					Tank tank = (Tank)entity;
					pillbox.setOwnerId(tank.getId());
					if(tank.isLocalPlayer() && !pillbox.isLocalPlayer())
					{
						pillbox.setLocalPlayer(true);
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
		Tank target = null;
		double targetDistance = Integer.MAX_VALUE;

		for (Tank tank : world.getTanks()) {
			// Don't attack the owner's tank, or hidden tanks.
			if(!tank.getId().equals(pillbox.getOwnerId()) && !tank.isHidden()) {
				if (targetInRange(tank)) {
					double xdistance = Math.abs(pillbox.getX() - tank.getX());
					double ydistance = Math.abs(pillbox.getY() - tank.getY());
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
	private boolean targetInRange(Entity target)
	{
		double xdistance = Math.abs(pillbox.getX() - target.getX());
		double ydistance = Math.abs(pillbox.getY() - target.getY());
		double distance = Math.sqrt((xdistance * xdistance) + (ydistance * ydistance));

		return (distance < pillbox.getRange());
	}

	/**
	 * returns the angle to the closest target for the pillbox
	 *
	 * @param Target
	 *            the Tank for the pillbox to target
	 * @return the angle toward the closest tank. returns -1 if no tanks in range
	 */
	private float getTargetDirection(Entity target)
	{
		double xvector = 0;
		double yvector = 0;
		float direction = -1;

		xvector = target.getX() - pillbox.getX();
		yvector = target.getY() - pillbox.getY();
		direction = (float) Math.atan2(yvector, xvector);

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
		pillbox.aimCannon(rotation);
		pillbox.fireCannon(world);
	}
	private static void sendNetUpdate(Pillbox pillbox)
	{
		Network net = NetworkSystem.getInstance();
		net.send(new UpdateOwnable(pillbox));
	}
}