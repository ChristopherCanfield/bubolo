package bubolo.world.entity.concrete;

import bubolo.audio.Audio;
import bubolo.audio.Sfx;
import bubolo.world.Damageable;
import bubolo.world.StaticEntity;
import bubolo.world.TerrainImprovement;

/**
 * Trees are StationaryElements that can spread over time, and hide Tanks that drive over them.
 *
 * @author BU CS673 - Clone Productions
 */
public class Tree extends StaticEntity implements TerrainImprovement, Damageable
{
	/**
	 * The health of the tree
	 */
	private float hitPoints = maxHitPoints;

	/**
	 * The maximum amount of hit points of the tree
	 */
	public static final int maxHitPoints = 1;

	private static final float speedModifier = 1.25f;

	private static final int width = 32;
	private static final int height = 32;

	public Tree(ConstructionArgs args) {
		super(args, width, height);
	}

//	@Override
//	public void update(World world) {
//		if(hitPoints <= 0) {
//			getTile().clearElement(world);
//			world.removeEntity(this);
//		}
//	}

	@Override
	public float speedModifier() {
		return speedModifier;
	}

	/**
	 * Returns the current health of the tree
	 *
	 * @return current hit point count
	 */
	@Override
	public float getHitPoints()
	{
		return hitPoints;
	}

	/**
	 * Method that returns the maximum number of hit points the entity can have.
	 * @return - Max Hit points for the entity
	 */
	@Override
	public int getMaxHitPoints()
	{
		return maxHitPoints;
	}

	@Override
	public boolean isAlive() {
		return hitPoints > 0;
	}

	/**
	 * Changes the hit point count after taking damage
	 *
	 * @param damagePoints
	 *            how much damage the tree has taken
	 */
	@Override
	public void takeHit(float damagePoints)
	{
		assert(damagePoints >= 0);
		hitPoints -= damagePoints;
	}

	/**
	 * Increments the pillbox's health by a given amount
	 *
	 * @param healPoints - how many points the tree is given
	 */
	@Override
	public void heal(float healPoints)
	{
		if (hitPoints + Math.abs(healPoints) < maxHitPoints)
		{
			hitPoints += Math.abs(healPoints);
		}
		else
		{
			hitPoints = maxHitPoints;
		}
	}

	@Override
	protected void onDispose()
	{
		Audio.play(Sfx.TREE_HIT);
	}
}
