package bubolo.world;

import com.badlogic.gdx.math.Polygon;

import bubolo.audio.Audio;
import bubolo.audio.Sfx;
import bubolo.util.GameLogicException;

/**
 * Trees hide tanks that drive over them, and can spread over time.
 *
 * @author BU CS673 - Clone Productions
 */
public class Tree extends StaticEntity implements TerrainImprovement, Collidable, Damageable {
	/**
	 * The health of the tree
	 */
	private float hitPoints = maxHitPoints;

	/**
	 * The maximum amount of hit points of the tree
	 */
	public static final int maxHitPoints = 1;

	private static final TerrainTravelSpeed terrainTravelSpeed = TerrainTravelSpeed.Slow;

	private static final int width = 32;
	private static final int height = 32;

	private final BoundingBox boundingBox;

	protected Tree(ConstructionArgs args, World world) {
		super(args, width, height);

		boundingBox = new BoundingBox(this);
	}

	@Override
	public float speedModifier() {
		return terrainTravelSpeed.speedModifier;
	}

	/**
	 * Returns the current health of the tree
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
	 * @param damagePoints how much damage the tree has taken
	 */
	@Override
	public void receiveDamage(float damagePoints, World world) {
		assert (damagePoints >= 0);
		hitPoints -= damagePoints;

		if (hitPoints <= 0) {
			dispose();
		}
	}

	/**
	 * Not implemented for trees.
	 */
	@Override
	public void heal(float healPoints) {
		throw new GameLogicException("Trees cannot be healed.");
	}

	@Override
	protected void onDispose() {
		Audio.play(Sfx.TreeHit, x(), y());
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public boolean isValidMinePlacementTarget() {
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
