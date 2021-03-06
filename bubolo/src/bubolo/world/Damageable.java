package bubolo.world;

/**
 * Interface for Entities that can take damage.
 *
 * @author BU CS673 - Clone Productions
 */
public interface Damageable
{
	public int height();
	public int width();

	public float x();
	public float y();

	/**
	 * Returns the current health of the entity.
	 *
	 * @return the entity's hit points.
	 */
	public float hitPoints();

	/**
	 * Returns the entity's maximum number of hit points.
	 *
	 * @return the entity's max hit points.
	 */
	public int maxHitPoints();

	/**
	 * Gives damage to the entity.
	 *
	 * @param damage the amount of damage done. Must be <= 0.
	 * @param world reference to the game world.
	 */
	public void receiveDamage(float damage, World world);

	/**
	 * Whether the entity is alive or not.
	 *
	 * @return true if the entity is alive.
	 */
	default public boolean isAlive() {
		return hitPoints() > 0;
	}
}
