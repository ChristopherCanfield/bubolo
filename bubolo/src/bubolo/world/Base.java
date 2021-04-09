package bubolo.world;

import bubolo.Config;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.ChangeOwner;

/**
 * Bases repair their owner, and refill the owner's ammo and mine stores.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Base extends ActorEntity implements Damageable, TerrainImprovement {
	/** Whether this base is currently refueling a Tank. */
	private boolean refuelingTank = false;

	private static final int maxHitPoints = 100;

	/** The base's health. Once this reaches zero, the base becomes capturable. */
	private float hitPoints = maxHitPoints;

	/** The amount of time that the base takes to heal from zero, in seconds. */
	private static final int baseHealTimeSeconds = 90;
	private static final float baseHealPerTick = maxHitPoints / baseHealTimeSeconds / (float) Config.FPS;

	/** The amount of time that the base is capturable after its health has been reduced to zero. */
	private static final int captureTimeSeconds = 10;
	private static final int captureTimeTicks = captureTimeSeconds * Config.FPS;
	private int captureTimeRemainingTicks = captureTimeTicks;

	private static final int maxRepairPoints = 100;
	private static final int maxAmmo = 100;
	private static final int maxMines = 10;

	private float repairPoints = maxRepairPoints;
	private float ammo = maxAmmo;
	private float mines = maxMines;

	/** The amount of time the base takes to refill its repair points, ammo, and mines (from zero), in seconds. */
	private static final int refillTimeSeconds = 300;
	private static final float repairPointsRefilledPerTick = maxRepairPoints / (float) refillTimeSeconds / Config.FPS;
	private static final float ammoRefilledPerTick = maxAmmo / (float) refillTimeSeconds / Config.FPS;
	private static final float minesRefilledPerTick = maxMines / (float) refillTimeSeconds / Config.FPS;

	/** The number of ticks between tank resupplying. */
	private static final int ticksBetweenTankResupplyEvent = Config.FPS; // 1 second per refuel.
	private int ticksUntilNextResupplyEvent = 0;

	/** The amount of health a tank is refueled per resupply event. The time between resupply events is limited by ticksBetweenTankResupplyEvent. */
	private static final float resuplyEventRepairAmount = 10;
	private static final int resuplyEventAmmoAmount = 10;
	private static final int resuplyEventMinesAmount = 1;

	private static final int width = 26;
	private static final int height = 30;

	private static final float speedModifier = 0.75f;

	/**
	 * Constructs a new Base.
	 *
	 * @param args the entity's construction arguments.
	 */
	protected Base(ConstructionArgs args) {
		super(args, width, height);
		updateBounds();
	}

	@Override
	protected void onUpdate(World world) {
		ticksUntilNextResupplyEvent--;
		refillSuppliesAndHealth();

		refuelingTank = false;
		for (Tank tank : world.getTanks()) {
			if (overlapsEntity(tank)) {
				if (refuelTank(tank)) {
					refuelingTank = true;
					break;
				} else {
					refuelingTank = false;
					processCapture(tank);
				}
			}
		}
	}

	private void refillSuppliesAndHealth() {
		if (captureTimeRemainingTicks == 0) {
			repairPoints += repairPointsRefilledPerTick;
			ammo += ammoRefilledPerTick;
			mines += minesRefilledPerTick;
			hitPoints += baseHealPerTick;

			clampSuppliesAndHealth();

		// Don't refill supplies if the base is capturable.
		} else {
			captureTimeRemainingTicks--;
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
		if (tank == owner() && !isTankRefueled(tank)) {
			if (ticksUntilNextResupplyEvent <= 0) {
				float refuelRepairPoints = (repairPoints > resuplyEventRepairAmount) ? repairPoints : resuplyEventRepairAmount;
				repairPoints -= refuelRepairPoints;

				int refuelAmmo = (int) ((ammo > resuplyEventAmmoAmount) ? ammo : resuplyEventAmmoAmount);
				ammo -= refuelAmmo;

				int refuelMines = (int) ((mines > resuplyEventMinesAmount) ? mines : resuplyEventMinesAmount);
				mines -= refuelMines;

				tank.refuel(repairPoints, refuelAmmo, refuelMines);

				clampSuppliesAndHealth();
				ticksUntilNextResupplyEvent = ticksBetweenTankResupplyEvent;
			}

			return true;
		}
		return false;
	}

	private void processCapture(Tank tank) {
		if (owner() == null || (hitPoints <= 0 && tank != owner() && tank.isOwnedByLocalPlayer())) {
			setOwnedByLocalPlayer(true);
			setOwner(tank);

			Network net = NetworkSystem.getInstance();
			net.send(new ChangeOwner(this));
		}
	}

	@Override
	protected void onOwnerChanged(ActorEntity owner) {
		// Give the base a small amount of health when it is captured.
		heal(5);
	}

	private static boolean isTankRefueled(Tank tank) {
		return tank.hitPoints() >= tank.maxHitPoints()
				&& tank.ammoCount() >= tank.maxAmmo()
				&& tank.mineCount() >= tank.maxMines();
	}

	/**
	 * @return true if this base is currently refueling a tank.
	 */
	public boolean isRefueling() {
		return refuelingTank;
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
	public void receiveDamage(float damagePoints, World world) {
		assert (damagePoints >= 0);

		if (hitPoints > 0) {
			hitPoints -= damagePoints;
			if (hitPoints < 0) {
				hitPoints = 0;
				captureTimeRemainingTicks = captureTimeTicks;
			}
		}
	}

	/**
	 * Heals the base by the specified amount.
	 *
	 * @param healPoints the amount that the base's health will be increased.
	 */
	@Override
	public void heal(float healPoints) {
		assert (healPoints >= 0);
		hitPoints += healPoints;
		if (hitPoints > maxHitPoints) {
			hitPoints = maxHitPoints;
		}
	}

	/**
	 * Bases are always alive, even when their health is at zero.
	 */
	@Override
	public boolean isAlive() {
		return true;
	}


	@Override
	public boolean isValidMinePlacementTarget() {
		return false;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public float speedModifier() {
		return speedModifier;
	}

	@Override
	public String toString() {
		return super.toString() + " | isSolid: " + isSolid();
	}
}
