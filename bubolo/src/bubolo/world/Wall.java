package bubolo.world;

import java.util.UUID;

import com.badlogic.gdx.math.Polygon;

import bubolo.audio.Sfx;
import bubolo.audio.SfxRateLimiter;
import bubolo.util.TileUtil;

/**
 * Walls block tanks. They are replaced with Rubble when destroyed.
 *
 * @author BU CS673 - Clone Productions
 */
public class Wall extends StaticEntity implements TerrainImprovement, Collidable, Adaptable, Damageable {
	private int tilingState = 0;

	private static final int maxHitPoints = 100;

	/**
	 * The wall's health.
	 */
	private float hitPoints = maxHitPoints;

	/**
	 * An array containing the classes that result in a valid match when determining adaptive tiling state.
	 * TODO (cdc - 2021-04-05): This affects only the visualization, and probably should not be in this class.
	 */
	private Class<?>[] matchingTypes = new Class[] { Wall.class };

	private static final int width = 30;
	private static final int height = 30;

	private final BoundingBox boundingBox;

	private final SfxRateLimiter sfxPlayer = new SfxRateLimiter(150);

	protected Wall(ConstructionArgs args, World world) {
		super(args, width, height);

		boundingBox = new BoundingBox(this);
	}

	@Override
	public void updateTilingState(World w) {
		tilingState = TileUtil.getTilingState(this, w, matchingTypes);
	}

	@Override
	public int getTilingState() {
		return tilingState;
	}

	/**
	 * Returns the current health of the wall
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
	 * @param damagePoints how much damage the wall has taken
	 */
	@Override
	public void receiveDamage(float damagePoints, World world) {
		assert (damagePoints >= 0);
		hitPoints -= damagePoints;
		sfxPlayer.play(Sfx.WALL_HIT);

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
	public void heal(float healPoints) {
		assert (healPoints >= 0);
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
	public boolean isValidMinePlacementTarget() {
		return false;
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
