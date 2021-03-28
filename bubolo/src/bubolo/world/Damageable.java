package bubolo.world;

/**
 * Interface for Entities that can take damage.
 *
 * @author BU CS673 - Clone Productions
 */
public interface Damageable
{
	public int getHeight();
	public int getWidth();

	public float getX();
	public float getY();

	/**
	 * Returns the current health of the tank
	 *
	 * @return current hit point count
	 */
	public float getHitPoints();

	/**
	 * Method that returns the maximum number of hit points the entity can have.
	 * @return - Max Hit points for the entity
	 */
	public int getMaxHitPoints();

	/**
	 * Changes the hit point count after taking damage
	 *
	 * @param damagePoints
	 *            how much damage the tank has taken
	 */
	public void takeHit(float damagePoints);

	/**
	 * Increments the tanks health by a given amount
	 *
	 * @param healPoints
	 *            - how many points the tank is given
	 */
	public void heal(float healPoints);

	/**
	 * Whether the entity is alive or not.
	 *
	 * @return true if the entity is alive.
	 */
	public boolean isAlive();
}
