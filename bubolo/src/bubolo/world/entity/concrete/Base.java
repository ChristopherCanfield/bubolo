package bubolo.world.entity.concrete;

import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.ChangeOwner;
import bubolo.world.ActorEntity;
import bubolo.world.Damageable;
import bubolo.world.TerrainImprovement;
import bubolo.world.World;

/**
 * Bases allow Tanks to heal and recover their mines, and capturing them is the primary
 * goal of the game.
 *
 * @author BU CS673 - Clone Productions
 */
public class Base extends ActorEntity implements Damageable, TerrainImprovement
{
	/**
	 * Whether this Base is currently charging a Tank.
	 */
	private boolean isCharging = false;

	/**
	 * The number of hit points the base has.
	 */
	private float hitPoints = maxHitPoints;

	private static final int maxHitPoints = 100;

	private static final int hitPointsRechargeRate = 5;

	private int mineCount = MAX_MINE_COUNT;

	private static final int MAX_MINE_COUNT = 10;

	private static final int MINE_REPLENISH_RATE = 1;

	private int ammoCount = MAX_AMMO_COUNT;

	private static final int MAX_AMMO_COUNT = 100;

	private static final int AMMO_REPLENISH_RATE = 5;

	private static final int width = 26;
	private static final int height = 30;

	private static final float speedModifier = 0.75f;

	/**
	 * Constructs a new Base.
	 *
	 * @param args the entity's construction arguments.
	 */
	public Base(ConstructionArgs args)
	{
		super(args, width, height);
		updateBounds();
	}

	/**
	 * Checks whether or not this base is currently charging a tank.
	 *
	 * @return the current charging status of this base.
	 */
	public boolean isCharging()
	{
		return isCharging;
	}

	/**
	 * Sets the charging state of this base.
	 *
	 * @param charge
	 *            represents whether or not this base should be in a charging state. False
	 *            = not charging!
	 */
	public void setCharging(boolean charge)
	{
		isCharging = charge;
	}

	/**
	 * Returns the current health of the base
	 *
	 * @return current hit point count
	 */
	@Override
	public float hitPoints()
	{
		return hitPoints;
	}

	/**
	 * Method that returns the maximum number of hit points the entity can have.
	 * @return - Max Hit points for the entity
	 */
	@Override
	public int maxHitPoints()
	{
		return maxHitPoints;
	}

	/**
	 * Changes the hit point count after taking damage
	 *
	 * @param damagePoints
	 *            how much damage the base has taken
	 */
	@Override
	public void receiveDamage(float damagePoints, World world)
	{
		assert(damagePoints >= 0);

		if (hitPoints > 0) {
			hitPoints -= damagePoints;
			if (hitPoints < 0) { hitPoints = 0; }

			if (hitPoints <= 0 &&  isOwnedByLocalPlayer())
			{
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
	public void heal(float healPoints)
	{
		assert(healPoints >= 0);
		if (hitPoints + healPoints < maxHitPoints) {
			hitPoints += healPoints;
		} else {
			hitPoints = maxHitPoints;
		}
	}

	/**
	 * The current amount of ammo at the base
	 * @return the current amount of ammo
	 */
	public int getAmmoCount()
	{
		return ammoCount;
	}


	/**
	 * The maximum amount of ammo a base can have
	 * @return the maximum amount of ammo storage at a base
	 */
	public static int getMaxAmmoCount()
	{
		return MAX_AMMO_COUNT;
	}

	/**
	 * Replenishes the ammo for the base
	 */
	public void gatherAmmo()
	{
		if(ammoCount + AMMO_REPLENISH_RATE < MAX_AMMO_COUNT)
		{
			ammoCount += AMMO_REPLENISH_RATE;
		}
		else
		{
			ammoCount = MAX_AMMO_COUNT;
		}
	}

	/**
	 * Method that deducts ammo from supply to give to tank
	 * @return - amount of ammo capable of being supplied at request
	 */
	public int giveAmmo()
	{
		if(ammoCount - AMMO_REPLENISH_RATE < 0)
		{
			int ammoGiven = ammoCount;
			ammoCount = 0;
			return ammoGiven;
		}
		else
		{
			ammoCount -= AMMO_REPLENISH_RATE;
			return AMMO_REPLENISH_RATE;
		}
	}

	/**
	 * The current number of mines at a base
	 * @return the current number of mines
	 */
	public int getMineCount()
	{
		return mineCount;
	}

	/**
	 * The maximum number of mines a base can have
	 * @return the maximum storage for mines at a base
	 */
	public static int getMaxMineCount()
	{
		return MAX_MINE_COUNT;
	}

	/**
	 * Replenishes the mines for the base
	 */
	public void gatherMines()
	{
		if(mineCount + MINE_REPLENISH_RATE < MAX_MINE_COUNT)
		{
			mineCount += MINE_REPLENISH_RATE;
		}
		else
		{
			mineCount = MAX_MINE_COUNT;
		}
	}

	/**
	 * Method that deducts mines from supply to give to tank
	 * @return - amount of mines capable of being supplied at request
	 */
	public int giveMine()
	{
		if(mineCount - MINE_REPLENISH_RATE < 0)
		{
			int minesGiven = mineCount;
			mineCount = 0;
			return minesGiven;
		}
		else
		{
			mineCount -= MINE_REPLENISH_RATE;
			return MINE_REPLENISH_RATE;
		}
	}


	/**
	 * Donates hit points to an object to heal
	 * @return the amount of hit points to give
	 */
	public float giveHitPoints()
	{
		float hitPointsGiven;
		if(hitPoints - hitPointsRechargeRate < 0) {
			hitPointsGiven = hitPoints;
			hitPoints = 0;
			return hitPointsGiven;
		} else {
			hitPoints -= hitPointsRechargeRate;
			hitPointsGiven = hitPointsRechargeRate;
		}
		return hitPointsGiven;
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
