package bubolo.controllers.ai;

import com.badlogic.gdx.math.Intersector;

import bubolo.Systems;
import bubolo.audio.Sfx;
import bubolo.controllers.ActorEntityController;
import bubolo.net.command.ActorEntityCaptured;
import bubolo.util.Units;
import bubolo.world.Pillbox;
import bubolo.world.Pillbox.BuildStatus;
import bubolo.world.Tank;
import bubolo.world.Wall;
import bubolo.world.World;

/**
 * A controller for pillboxes. This controller handles pillbox target acquisition and pillbox capturing.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class AiPillboxController extends ActorEntityController<Pillbox> {
	private static float delayBeforeFiringAtTarget = 0.35f;

	private boolean firingDelayExpired;
	private boolean targetLost;

	/**
	 * Constructs an AI Pillbox controller.
	 *
	 * @param pillbox the pillbox this controller will control.
	 */
	public AiPillboxController(Pillbox pillbox) {
		super(pillbox);
	}

	@Override
	public void update(World world) {
		var pillbox = parent();

		// Don't process updates if the pillbox is being moved.
		if (pillbox.buildStatus() == BuildStatus.Built) {
			// Only fire if cannon is ready.
			if (pillbox.isCannonReady()) {
				Tank target = getTarget(world);
				if (target != null) {
					if (!pillbox.hasTarget()) {
						Systems.audio().play(Sfx.PillboxTargetFound, pillbox.x(), pillbox.y());
						pillbox.setHasTarget(true);
						firingDelayExpired = false;
						targetLost = false;

						// Allow the pillbox to start firing after a brief delay.
						world.timer().scheduleSeconds(delayBeforeFiringAtTarget, w -> {
							firingDelayExpired = true;
						});
					} else if (firingDelayExpired) {
						fire(getAngleToTarget(target), world);
					}
				} else if (pillbox.hasTarget() && !targetLost) {
					targetLost = true;
					// Schedule the target lost actions.
					world.timer().scheduleSeconds(1.5f, w -> {
						if (pillbox.hasTarget()) {
							if (pillbox.hitPoints() > 0) {
								Systems.audio().play(Sfx.PillboxTargetLost, pillbox.x(), pillbox.y());
							}
							pillbox.setHasTarget(false);
						}
					});
				}
			}

			handleTankCapture(world);
		}
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
				if (pillbox.owner() != tank && tank.isOwnedByLocalPlayer() && tank.isAlive()) {
					if (Intersector.overlapConvexPolygons(pillbox.captureBounds(), tank.bounds())) {
						pillbox.onCaptured(world, tank);
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
	 * @return target the closest enemy tank that is within range, or null if no tank is within range.
	 */
	private Tank getTarget(World world) {
		double maxRange = parent().range();
		float x = parent().x();
		float y = parent().y();
		var owner = parent().owner();

		Tank target = null;
		double targetDistance = Integer.MAX_VALUE;

		/*
		 * @NOTE (cdc 2021-05-25): Switched to the index-based for loop, rather than for-each (my preference), b/c the iterator
		 * for the UnmodifiableList was creating a weirdly large amount of garbage according to the profiler.
		 */
		var tanks = world.getTanks();
		for (int i = 0; i < tanks.size(); i++) {
			var tank = tanks.get(i);
			// Don't attack the owner's tank, or hidden tanks.
			if (!tank.isAlliedWith(owner) && !tank.isHidden()) {
				double newTargetDistance = distance(x, y, tank.x(), tank.y());
				if (newTargetDistance < maxRange && newTargetDistance < targetDistance) {
					target = tank;
					targetDistance = newTargetDistance;
				}
			}
		}
		return target;
	}

	private static double distance(float pillboxX, float pillboxY, float targetX, float targetY) {
		double xdistance = Math.abs(pillboxX - targetX);
		double ydistance = Math.abs(pillboxY - targetY);
		return Math.sqrt((xdistance * xdistance) + (ydistance * ydistance));
	}


	/**
	 * returns the angle to the closest target for the pillbox
	 *
	 * @param target the Tank that the pillbox will target.
	 * @return the angle between this pillbox and the target.
	 */
	private float getAngleToTarget(Tank target) {
		double xvector = target.x() - parent().x();
		double yvector = target.y() - parent().y();
		float radians = (float) Math.atan2(yvector, xvector);

		return radians;
	}

	/**
	 * Aims and fires the pillbox's cannon.
	 *
	 * @param rotation the pillbox cannon's rotation.
	 * @param world reference to the game world.
	 */
	private void fire(float rotation, World world) {
		var pillbox = parent();
		pillbox.aimCannon(rotation);
		// Don't fire a bullet if it will hit a wall that is touching the pillbox.
		// @TODO (cdc 2021-06-03): If a pillbox becomes surrounded by walls, it won't be able to fire.
		if (!willBulletHitAdjacentWall(pillbox, world)) {
			pillbox.fireCannon(world);
		}
	}

	/**
	 * Returns true if a pillbox will hit an adjacent wall if it fires. Not 100% accurate, but good enough (cdc 2021-06-03).
	 *
	 * @param pillbox the pillbox that this AI controls.
	 * @param world reference to the game world.
	 * @return whether the pillbox will hit an adjacent wall if it fires.
	 */
	private static boolean willBulletHitAdjacentWall(Pillbox pillbox, World world) {
		var oneTileDistX = (float) Math.cos(pillbox.cannonRotation()) * Units.TileToWorldScale + pillbox.centerX();
		var oneTileDistY = (float) Math.sin(pillbox.cannonRotation()) * Units.TileToWorldScale + pillbox.centerY();
		int oneTileColumn = Units.worldUnitToTile(oneTileDistX);
		int oneTileRow = Units.worldUnitToTile(oneTileDistY);

		return (world.getTerrainImprovement(oneTileColumn, oneTileRow) instanceof Wall);
	}

	private static void sendNetUpdate(Pillbox pillbox) {
		Systems.network().send(new ActorEntityCaptured(pillbox));
	}
}
