package bubolo.world;

import bubolo.Config;
import bubolo.Systems;
import bubolo.net.command.ActorEntityCaptured;
import bubolo.util.Nullable;
import bubolo.util.Time;

/**
 * Bases repair their owner, and refill the owner's ammo and mine stores.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Base extends ActorEntity implements Damageable, TerrainImprovement {
	/** Whether this base is currently refueling a Tank. */
	private boolean refuelingTank;
	/** Whether a friendly tank is on this repair bay. */
	private boolean isFriendlyTankOnThisRepairBay;

	private static final int maxHitPoints = 100;

	/** The base's health. Once this reaches zero, the base becomes capturable. */
	private float hitPoints = maxHitPoints;

	/** The amount of time that the base takes to heal from zero, in seconds. */
	private static final int baseHealTimeSeconds = 90;
	private static final float baseHealPerTick = maxHitPoints / (float) Time.secondsToTicks(baseHealTimeSeconds);

	/** The amount of time that the base is capturable after its health has been reduced to zero. */
	private static final int captureTimeSeconds = 10;
	private int captureTimerExpiredTimerId = -1;
	/** Whether the base can be captured by a tank driving over it. */
	private boolean capturable = false;

	private static final float maxRepairPoints = 100;
	private static final float maxAmmo = 100;
	private static final float maxMines = 10;

	private float repairPoints = maxRepairPoints;
	private float ammo = maxAmmo;
	private float mines = maxMines;

	/** The amount of time the base takes to refill its repair points, ammo, and mines (from zero), in seconds. */
	private static final int refillTimeTicks = Time.secondsToTicks(150);
	private static final float repairPointsRefilledPerTick = maxRepairPoints / refillTimeTicks;
	private static final float ammoRefilledPerTick = maxAmmo / refillTimeTicks;
	private static final float minesRefilledPerTick = maxMines / refillTimeTicks;

	/** The number of ticks between tank resupplying. */
	private static final int ticksBetweenTankResupplyEvent = Config.FPS; // 1 second per refuel.
	private boolean readyToResupplyTank = true;

	/** The amount of health a tank is refueled per resupply event. The time between resupply events is limited by ticksBetweenTankResupplyEvent. */
	private static final float resuplyEventRepairAmount = 4;
	private static final int resuplyEventAmmoAmount = 4;
	private static final int resuplyEventMinesAmount = 1;

	private static final int width = 32;
	private static final int height = 32;

	private static final TerrainTravelSpeed terrainTravelSpeed = TerrainTravelSpeed.VerySlow;

	/**
	 * Constructs a new Base.
	 *
	 * @param args the entity's construction arguments.
	 * @param world reference to the game world.
	 */
	protected Base(ConstructionArgs args, World world) {
		super(args, width, height);
		updateBounds();
		world.timer().scheduleTicks(ticksBetweenTankResupplyEvent, this::onReadyToRefuel);
	}

	@Override
	protected void onUpdate(World world) {
		refillSuppliesAndHealth();

		refuelingTank = false;
		isFriendlyTankOnThisRepairBay = false;

		/* @NOTE (cdc 2021-05-25): Switched to the index-based for loop, rather than for-each (my preference), b/c the
		 * 			iterator for the UnmodifiableList was creating a weirdly large amount of garbage according to the profiler. */
		var tanks = world.getTanks();
		for (int i = 0; i < tanks.size(); i++) {
			var tank = tanks.get(i);
			if (overlapsEntity(tank)) {
				if (refuelTank(tank)) {
					refuelingTank = true;
					isFriendlyTankOnThisRepairBay = true;
				} else if (hasOwner() && owner().equals(tank)) {
					isFriendlyTankOnThisRepairBay = true;
				} else {
					refuelingTank = false;
					processCapture(world, tank);
				}
			}
		}
	}

	private void refillSuppliesAndHealth() {
		// Don't refill supplies if the base is capturable.
		if (!capturable) {
			repairPoints += repairPointsRefilledPerTick;
			ammo += ammoRefilledPerTick;
			mines += minesRefilledPerTick;
			hitPoints += baseHealPerTick;

			clampSuppliesAndHealth();
		}
	}


	private void clampSuppliesAndHealth() {
		if (repairPoints > maxRepairPoints) { repairPoints = maxRepairPoints; }
		else if (repairPoints < 0) { repairPoints = 0; }

		if (ammo > maxAmmo) { ammo = maxAmmo; }
		else if (ammo < 0) { ammo = 0; }

		if (mines > maxMines) { mines = maxMines; }
		else if (mines < 0) { mines = 0; }

		if (hitPoints > maxHitPoints) { hitPoints = maxHitPoints; }
		else if (hitPoints < 0) { hitPoints = 0; }
	}

	private boolean refuelTank(Tank tank) {
		if (tank.isAlive() && tank.isAlliedWith(owner()) && !isTankRefueled(tank)) {
			if (readyToResupplyTank) {
				float refuelRepairPoints;
				if (tank.hitPoints() < tank.maxHitPoints()) {
					refuelRepairPoints = (repairPoints > resuplyEventRepairAmount) ? resuplyEventRepairAmount : repairPoints;
					repairPoints -= refuelRepairPoints;
				} else {
					refuelRepairPoints = 0;
				}

				int refuelAmmo;
				if (tank.ammo() < tank.maxAmmo()) {
					refuelAmmo = (int) ((ammo > resuplyEventAmmoAmount) ? resuplyEventAmmoAmount : ammo);
					ammo -= refuelAmmo;
				} else {
					refuelAmmo = 0;
				}

				int refuelMines;
				if (tank.mines() < tank.maxMines()) {
					refuelMines = (int) ((mines > resuplyEventMinesAmount) ? resuplyEventMinesAmount : mines);
					mines -= refuelMines;
				} else {
					refuelMines = 0;
				}

				tank.refuel(refuelRepairPoints, refuelAmmo, refuelMines);

				clampSuppliesAndHealth();
				readyToResupplyTank = false;
			}
			return true;
		}
		return false;
	}

	// Used by the timer.
	private void onReadyToRefuel(World world) {
		if (!isDisposed()) {
			readyToResupplyTank = true;
			world.timer().scheduleTicks(ticksBetweenTankResupplyEvent, this::onReadyToRefuel);
		}
	}

	private void processCapture(World world, Tank tank) {
		if ((hitPoints <= 0 || !hasOwner())
				&& (tank.isOwnedByLocalPlayer() && tank.isAlive())
				&& !this.isAlliedWithLocalPlayer()) {
			onCaptured(world, tank);

			Systems.network().send(new ActorEntityCaptured(this));
		}
	}

	@Override
	protected void onOwnerChanged(ActorEntity owner) {
		// Give the base a small amount of health when it is captured.
		heal(5);
	}

	private static boolean isTankRefueled(Tank tank) {
		return tank.hitPoints() >= tank.maxHitPoints()
				&& tank.ammo() >= tank.maxAmmo()
				&& tank.mines() >= tank.maxMines();
	}

	/**
	 * @return true if this base is currently refueling a tank.
	 */
	public boolean isRefueling() {
		return refuelingTank;
	}

	public boolean isFriendlyTankOnThisRepairBay() {
		return isFriendlyTankOnThisRepairBay;
	}

	public float ammo() {
		return ammo;
	}

	public float maxAmmo() {
		return maxAmmo;
	}

	public float mines() {
		return mines;
	}

	public float maxMines() {
		return maxMines;
	}

	public float repairPoints() {
		return repairPoints;
	}

	public float maxRepairPoints() {
		return maxRepairPoints;
	}

	/**
	 * Returns the current health of the base
	 *
	 * @return current hit point count
	 */
	@Override
	public float hitPoints() {
		return hitPoints;
	}

	/**
	 * Method that returns the maximum number of hit points the entity can have.
	 *
	 * @return - Max Hit points for the entity
	 */
	@Override
	public int maxHitPoints() {
		return maxHitPoints;
	}

	/**
	 * Changes the hit point count after taking damage
	 *
	 * @param damagePoints how much damage the base has taken
	 */
	@Override
	public void receiveDamage(World world, float damagePoints, @Nullable ActorEntity damageProvider) {
		assert (damagePoints >= 0);

		hitPoints -= damagePoints;
		if (hitPoints <= 0) {
			hitPoints = 0;

			// Reset the capture timer if it was already set.
			if (capturable) {
				assert(captureTimerExpiredTimerId != -1);
				world.timer().rescheduleSeconds(captureTimerExpiredTimerId, captureTimeSeconds);
			} else {
				captureTimerExpiredTimerId = world.timer().scheduleSeconds(captureTimeSeconds, this::onCaptureTimeExpired);
			}
			capturable = true;
		} else {
			Systems.messenger().notifyObjectUnderAttack(world, this, damageProvider);
		}
	}

	/**
	 * Heals the base by the specified amount.
	 *
	 * @param healPoints the amount that the base's health will be increased.
	 */
	private void heal(float healPoints) {
		assert (healPoints >= 0);
		hitPoints += healPoints;
		if (hitPoints > maxHitPoints) {
			hitPoints = maxHitPoints;
		}
	}

	/**
	 * When the capture timer has expired, this method makes the base uncapturable.
	 *
	 * @param world reference to the game world.
	 */
	private void onCaptureTimeExpired(World world) {
		captureTimerExpiredTimerId = -1;
		capturable = false;
	}

	/**
	 * Bases are always alive, even when their health is at zero.
	 */
	@Override
	public boolean isAlive() {
		return true;
	}


	@Override
	public boolean isValidBuildTarget() {
		return false;
	}

	@Override
	public boolean isSolid() {
		return (hitPoints > 0 && owner() != null && !isAlliedWithLocalPlayer());
	}

	@Override
	public float accelerationModifier() {
		return terrainTravelSpeed.accelerationModifier;
	}

	@Override
	public float maxSpeedModifier() {
		return terrainTravelSpeed.maxSpeedModifier;
	}

	@Override
	public String toString() {
		return super.toString() + " | isSolid: " + isSolid();
	}
}
