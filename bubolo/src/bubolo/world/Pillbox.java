package bubolo.world;

import com.badlogic.gdx.math.Polygon;

import bubolo.Config;
import bubolo.audio.Sfx;
import bubolo.audio.SfxRateLimiter;
import bubolo.util.Time;

/**
 * Pillboxes are stationary defensive structures that shoot at enemy tanks, and can be captured. Captured pillboxes do not shoot
 * at their owner.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Pillbox extends ActorEntity implements Damageable, TerrainImprovement {
	/** Time required to reload cannon. */
	private static final int cannonReloadSpeedTicks = Time.secondsToTicks(0.5f);
	private boolean cannonReloaded = true;

	/** The direction the pillbox will fire. */
	private float cannonRotation = 0;

	/** Max range to locate a target. The pillbox will not fire unless there is a tank within this range. */
	private float range = 300;

	/** The pillbox's maximum health. */
	private static final int maxHitPoints = 100;

	/** The pillbox's health. */
	private float hitPoints = maxHitPoints;

	/** The amount of time that the pillbox is capturable after its health has been reduced to zero. */
	private static final int captureTimeTicks = Time.secondsToTicks(10);
	private boolean capturable = false;
	private int capturableTimerId = -1;

	private boolean beingMoved;

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
	 * @param world reference to the game world.
	 */
	protected Pillbox(ConstructionArgs args, World world) {
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
		if (!isBeingMoved()) {
			if (!capturable) {
				heal(hpPerTick);
			}
		} else {
			// @TODO (cdc 2021-05-16): Offset this behind the tank. Needs to change based on the direction of the tank.
			setPosition(owner().x(), owner().y());
		}
	}

	@Override
	protected void onOwnerChanged(ActorEntity newOwner) {
		// If the Pillbox gained a new owner, set its health to a small positive value,
		// so another player can't instantly grab it without needing to reduce its health.
		if (newOwner != null) {
			hitPoints = 5;
		}
	}

	public boolean isBeingMoved() {
		return beingMoved;
	}

	public void setIsBeingMoved(boolean beingMoved) {
		assert hasOwner();
		this.beingMoved = beingMoved;
	}

	/**
	 * Whether the pillbox is ready to fire. Pillboxes with no health won't fire.
	 *
	 * @return true if the cannon is ready to fire.
	 */
	public boolean isCannonReady() {
		return cannonReloaded && hitPoints() > 0;
	}

	/**
	 * Aims the cannon.
	 *
	 * @param rotation direction to aim the cannon
	 */
	public void aimCannon(float rotation) {
		cannonRotation = rotation;
	}

	/**
	 * Fires the cannon in its current direction.
	 *
	 * @param world reference to world.
	 */
	public void fireCannon(World world) {
		cannonReloaded = false;
		world.timer().scheduleTicks(cannonReloadSpeedTicks, w -> cannonReloaded = true);

		var args = new Entity.ConstructionArgs(Entity.nextId(), x(), y(), cannonRotation);
		Bullet bullet = world.addEntity(Bullet.class, args);
		bullet.setOwner(this);
	}

	/**
	 * @return the distance at which the pillbox will attempt to fire at an enemy
	 */
	public float range() {
		return range;
	}

	/**
	 * @return the pillbox's current health.
	 */
	@Override
	public float hitPoints() {
		return Math.max(0, hitPoints);
	}

	/**
	 * @return the pillbox's maximum hit points.
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
	 * Damages the pillbox, which results in its hit points being decreased.
	 *
	 * @param damagePoints how much damage the pillbox has taken. Must be >= 0.
	 */
	@Override
	public void receiveDamage(float damagePoints, World world) {
		assert damagePoints >= 0;

		sfxPlayer.play(Sfx.PillboxHit, x(), y());
		hitPoints -= damagePoints;

		if (hitPoints < 0) {
			// Give the player a few seconds to claim the damaged pillbox.
			if (capturable) {
				world.timer().rescheduleTicks(capturableTimerId, captureTimeTicks);
			} else {
				capturableTimerId = world.timer().scheduleTicks(captureTimeTicks, this::onCapturableTimerExpired);
			}

			capturable = true;
			hitPoints = 0;
		}
	}

	private void onCapturableTimerExpired(World world) {
		capturable = false;
		capturableTimerId = -1;
	}

	/**
	 * Restores the pillbox's health by the specified amount.
	 *
	 * @param healPoints the number of hit points the pillbox will gain. Must be >= 0.
	 */
	private void heal(float healPoints) {
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
