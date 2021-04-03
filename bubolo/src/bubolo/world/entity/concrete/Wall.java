package bubolo.world.entity.concrete;

import java.util.UUID;

import com.badlogic.gdx.math.Polygon;

import bubolo.audio.Audio;
import bubolo.audio.Sfx;
import bubolo.util.TileUtil;
import bubolo.world.Adaptable;
import bubolo.world.BoundingBox;
import bubolo.world.Collidable;
import bubolo.world.Damageable;
import bubolo.world.Entity;
import bubolo.world.StaticEntity;
import bubolo.world.TerrainImprovement;
import bubolo.world.World;

/**
 * Walls are intended to impede Tank movement, and create Rubble Terrain when destroyed.
 *
 * @author BU CS673 - Clone Productions
 */
public class Wall extends StaticEntity implements TerrainImprovement, Collidable, Adaptable, Damageable
{
	private int tilingState = 0;

	private static final int maxHitPoints = 100;

	/**
	 * The wall's health.
	 */
	private float hitPoints = maxHitPoints;

	/**
	 * Intended to be generic -- this is a list of all of the StationaryEntities classes that should
	 * result in a valid match when checking surrounding tiles to determine adaptive tiling state.
	 */
	private Class<?>[] matchingTypes = new Class[] { Wall.class };

	private static final int width = 30;
	private static final int height = 30;

	private BoundingBox boundingBox = new BoundingBox();

	public Wall(ConstructionArgs args)
	{
		super(args, width, height);
	}

	@Override
	public void updateTilingState(World w)
	{
		tilingState = TileUtil.getTilingState(this, w, matchingTypes);
	}

	@Override
	public int getTilingState()
	{
		return tilingState;
	}

	/**
	 * Returns the current health of the wall
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
	 *            how much damage the wall has taken
	 */
	@Override
	public void receiveDamage(float damagePoints, World world)
	{
		assert(damagePoints >= 0);
		hitPoints -= damagePoints;
		Audio.play(Sfx.WALL_HIT);

		if (hitPoints <= 0) {
			// When the wall is destroyed, replace it with rubble.
			var args = new Entity.ConstructionArgs(UUID.randomUUID(), x(), y(), 0);
			world.addEntity(Rubble.class, args);

			dispose();
		}
	}

	/**
	 * Increments the pillbox's health by a given amount
	 *
	 * @param healPoints - how many points the wall is given
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

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public Polygon bounds() {
		return boundingBox.bounds();
	}

	@Override
	public void updateBounds() {
		boundingBox.updateBounds(this);
	}
}
