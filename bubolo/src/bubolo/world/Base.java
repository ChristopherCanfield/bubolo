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
	private boolean refueling = false;

	private static final int maxHitPoints = 100;
	
	/** The base's health. Once this reaches zero, the base becomes capturable. */
	private float hitPoints = maxHitPoints;
	
	/** The amount of time that the base takes to heal from zero, in seconds. */
	private static final int baseHealTimeSeconds = 90;
	private static final float baseHealPerTick = maxHitPoints / baseHealTimeSeconds / (float) Config.FPS;
	/** The amount of time that the base is capturable after its health has been reduced to zero. */
	private static final int captureTimeSeconds = 10;
	
	private static final int maxRepairPoints = 100;
	private static final int maxAmmo = 100;
	private static final int maxMines = 10;
	
	private float repairPoints = maxRepairPoints;
	private float ammo = maxAmmo;
	private float mines = maxMines;

	/** The amount of time the base takes to refill its repair points, ammo, and mines (from zero), in seconds. */
	private static final int refillTimeSeconds = 300;
	private static final float repairPointsRefilledPerTick = maxRepairPoints / refillTimeSeconds / (float) Config.FPS;
	private static final float ammoRefilledPerTick = maxAmmo / refillTimeSeconds / (float) Config.FPS;
	private static final float minesRefilledPerTick = maxMines / refillTimeSeconds / (float) Config.FPS;

	/** The number of ticks between tank refueling. */
	private static final int ticksBetweenTankRefuelEvent = Config.FPS; // 1 second per refuel.
	
	/** The amount of health a tank is refueled per refueling event. The time between refuel events is limited by ticksBetweenTankRefuelEvent. */ 
	private static final float healthTankRefuelAmount = 10; 
	private static final int ammoTankRefuelAmount = 10;
	private static final int mineTankRefuelAmount = 1;
	
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
	public boolean isValidMinePlacementTarget() {
		return false;
	}

	/**
	 * Checks whether or not this base is currently charging a tank.
	 *
	 * @return the current charging status of this base.
	 */
	public boolean isCharging() {
		return isRefueling;
	}

	/**
	 * Sets the charging state of this base.
	 *
	 * @param charge sets whether this base is charging.
	 */
	public void setCharging(boolean charge) {
		isRefueling = charge;
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
			}

			if (hitPoints <= 0 && isOwnedByLocalPlayer()) {
				setOwnedByLocalPlayer(false);
				setOwner(null);

				Network net = NetworkSystem.getInstance();
				net.send(new ChangeOwner(this));
			}
		}
	}

	/**
	 * Increments the base's health by a given amount
	 *
	 * @param healPoints - how many points the base is given
	 */
	@Override
	public void heal(float healPoints) {
		assert (healPoints >= 0);
		if (hitPoints + healPoints < maxHitPoints) {
			hitPoints += healPoints;
		} else {
			hitPoints = maxHitPoints;
		}
	}

	/**
	 * The current amount of ammo at the base
	 *
	 * @return the current amount of ammo
	 */
	public int getAmmoCount() {
		return ammoCount;
	}

	/**
	 * The maximum amount of ammo a base can have
	 *
	 * @return the maximum amount of ammo storage at a base
	 */
	public static int getMaxAmmoCount() {
		return MAX_AMMO_COUNT;
	}

	/**
	 * Replenishes the ammo for the base
	 */
	public void gatherAmmo() {
		if (ammoCount + AMMO_REPLENISH_RATE < MAX_AMMO_COUNT) {
			ammoCount += AMMO_REPLENISH_RATE;
		} else {
			ammoCount = MAX_AMMO_COUNT;
		}
	}

	/**
	 * Method that deducts ammo from supply to give to tank
	 *
	 * @return - amount of ammo capable of being supplied at request
	 */
	public int giveAmmo() {
		if (ammoCount - AMMO_REPLENISH_RATE < 0) {
			int ammoGiven = ammoCount;
			ammoCount = 0;
			return ammoGiven;
		} else {
			ammoCount -= AMMO_REPLENISH_RATE;
			return AMMO_REPLENISH_RATE;
		}
	}

	/**
	 * The current number of mines at a base
	 *
	 * @return the current number of mines
	 */
	public int getMineCount() {
		return mineCount;
	}

	/**
	 * The maximum number of mines a base can have
	 *
	 * @return the maximum storage for mines at a base
	 */
	public static int getMaxMineCount() {
		return MAX_MINE_COUNT;
	}

	/**
	 * Replenishes the mines for the base
	 */
	public void gatherMines() {
		if (mineCount + MINE_REPLENISH_RATE < MAX_MINE_COUNT) {
			mineCount += MINE_REPLENISH_RATE;
		} else {
			mineCount = MAX_MINE_COUNT;
		}
	}

	/**
	 * Method that deducts mines from supply to give to tank
	 *
	 * @return - amount of mines capable of being supplied at request
	 */
	public int giveMine() {
		if (mineCount - MINE_REPLENISH_RATE < 0) {
			int minesGiven = mineCount;
			mineCount = 0;
			return minesGiven;
		} else {
			mineCount -= MINE_REPLENISH_RATE;
			return MINE_REPLENISH_RATE;
		}
	}

	/**
	 * Repairs the specified tank.
	 */
	public void repair(Tank tank) {
		if ()
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
