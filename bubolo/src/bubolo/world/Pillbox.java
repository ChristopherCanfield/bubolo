package bubolo.world;

import java.util.UUID;

import com.badlogic.gdx.math.Polygon;

import bubolo.Config;
import bubolo.audio.Sfx;
import bubolo.audio.SfxRateLimiter;

/**
 * Pillboxes are stationary defensive structures that shoot at enemy tanks, and can be captured. Captured pillboxes do not shoot
 * at their owner.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Pillbox extends ActorEntity implements Damageable, TerrainImprovement {
	/** When the cannon will be ready to fire. */
	private long cannonReadyTime = 0;

	/** Time required to reload cannon. */
	private static final long cannonReloadSpeed = 500;

	/** The direction the pillbox will fire. */
	private float cannonRotation = 0;

	/** Max range to locate a target. The pillbox will not fire unless there is a tank within this range. */
	private double range = 300;

	/** The pillbox's maximum health. */
	private static final int maxHitPoints = 100;

	/** The pillbox's health. */
	private float hitPoints = maxHitPoints;

	/** The amount of time that the pillbox is capturable after its health has been reduced to zero. */
	private static final int captureTimeSeconds = 10;
	private static final int captureTimeTicks = captureTimeSeconds * Config.FPS;
	private int captureTimeRemainingTicks = captureTimeTicks;

	// 0.5f / FPS = heals ~0.5 health per second.
	private static final float hpPerTick = 0.5f / Config.FPS;

	private static final int width = 30;
	private static final int height = 30;

	private static final int captureBoundsExtraWidth = 12;
	private static final int captureWidth = width + captureBoundsExtraWidth;
	private static final int captureBoundsExtraHeight = 12;
	private static final int captureHeight = height + captureBoundsExtraHeight;

	// Gives the appearance of capturing the pillbox by touching it.
	private final BoundingBox captureBounds;

	private final SfxRateLimiter sfxPlayer = new SfxRateLimiter(150);

	/**
	 * Constructs a new Pillbox.
	 *
	 * @param args the entity's construction arguments.
	 */
	protected Pillbox(ConstructionArgs args) {
		super(args, width, height);
		captureBounds = new BoundingBox(x() - (captureBoundsExtraWidth / 2),
				y() - (captureBoundsExtraHeight / 2),
				captureWidth,
				captureHeight);
	}

	/**
	 * @return The area within which a tank can capture a pillbox.
	 */
	public Polygon captureBounds() {
		return captureBounds.bounds();
	}

	@Override
	public void updateBounds() {
		super.updateBounds();
		captureBounds.updateBounds(x() - (captureBoundsExtraWidth / 2), y() - (captureBoundsExtraHeight / 2), captureWidth, captureHeight);
	}

	@Override
	protected void onUpdate(World world) {
		if (captureTimeRemainingTicks <= 0) {
			heal(hpPerTick);
		} else {
			captureTimeRemainingTicks--;
		}
	}

	@Override
	protected void onOwnerChanged(ActorEntity newOwner) {
		// If the Pillbox gained a new owner, set its health to a small positive value,
		// so another player
		// can't instantly grab it without needing to reduce its health.
		if (newOwner != null) {
			hitPoints = 5;
		}
	}

	/**
	 * Whether the pillbox is ready to fire. Pillboxes with no health won't fire.
	 *
	 * @return true if the cannon is ready to fire.
	 */
	public boolean isCannonReady() {
		return System.currentTimeMillis() > cannonReadyTime && hitPoints() > 0;
	}

	/**
	 * Aim the Cannon
	 *
	 * @param rotation direction to aim the cannon
	 */
	public void aimCannon(float rotation) {
		cannonRotation = rotation;
	}

	/**
	 * get cannon rotation
	 *
	 * @return cannonRotation the direction the pillbox is set to fire
	 */
	public float getCannonRotation() {
		return cannonRotation;
	}

	/**
	 * Fire the pillbox
	 *
	 * @param world reference to world.
	 */
	public void fireCannon(World world) {
		cannonReadyTime = System.currentTimeMillis() + cannonReloadSpeed;

		var args = new Entity.ConstructionArgs(UUID.randomUUID(), x(), y(), getCannonRotation());
		Bullet bullet = world.addEntity(Bullet.class, args);
		bullet.setOwner(this);
	}

	/**
	 * returns the range of this pillbox
	 *
	 * @return range distance at which the pillbox will attempt to fire at an enemy
	 */
	public double getRange() {
		return this.range;
	}

	/**
	 * sets the static range of this pillbox
	 *
	 * @param range distance at which the pillbox will attempt to fire at an enemy
	 */
	public void setRange(double range) {
		this.range = range;
	}

	/**
	 * Returns the current health of the pillbox
	 *
	 * @return current hit point count
	 */
	@Override
	public float hitPoints() {
		return Math.max(0, hitPoints);
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

	@Override
	public boolean isAlive() {
		return hitPoints > 0;
	}

	/**
	 * Changes the hit point count after taking damage
	 *
	 * @param damagePoints how much damage the pillbox has taken
	 */
	@Override
	public void receiveDamage(float damagePoints, World world) {
		assert damagePoints >= 0;

		sfxPlayer.play(Sfx.PILLBOX_HIT);
		hitPoints -= damagePoints;

		if (hitPoints < 0) {
			// Give the player a few seconds to claim the damaged pillbox.
			captureTimeRemainingTicks = captureTimeTicks;
			hitPoints = 0;
		}
	}

	/**
	 * Increments the pillbox's health by a given amount
	 *
	 * @param healPoints - how many points the pillbox is given
	 */
	@Override
	public void heal(float healPoints) {
		assert healPoints >= 0;

		hitPoints += healPoints;
		if (hitPoints > maxHitPoints) {
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
}
