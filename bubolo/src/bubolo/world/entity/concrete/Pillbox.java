package bubolo.world.entity.concrete;

import java.util.UUID;

import com.badlogic.gdx.math.Polygon;

import bubolo.Config;
import bubolo.audio.Audio;
import bubolo.audio.Sfx;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.ChangeOwner;
import bubolo.world.ActorEntity;
import bubolo.world.BoundingBox;
import bubolo.world.Damageable;
import bubolo.world.Entity;
import bubolo.world.TerrainImprovement;
import bubolo.world.World;

/**
 * Pillboxes are stationary defensive structures that can be placed by a Tank.
 * They shoot at an enemy Tank until destroyed, at which point they can be
 * retrieved and used again.
 *
 * @author BU CS673 - Clone Productions
 */
public class Pillbox extends ActorEntity implements Damageable, TerrainImprovement {
	/* When the cannon will be ready to fire. */
	private long cannonReadyTime = 0;

	/* Time required to reload cannon. */
	private static final long cannonReloadSpeed = 500;

	/* The direction the pillbox will fire. */
	private float cannonRotation = 0;

	/* Max range to locate a target. Pillbox will not fire unless there is a tank
	 * within this range. */
	private double range = 300;

	/* The pillbox's health. */
	private float hitPoints = MAX_HIT_POINTS;

	/* The pillbox's maximum health. */
	public static final int MAX_HIT_POINTS = 100;

	// 0.5f / FPS = heals ~0.5 health per second.
	private static final float hpPerTick = 0.5f / Config.FPS;

	private static final int width = 27;
	private static final int height = 27;

	// Gives the appearance of capturing the pillbox by touching it.
	private static final int captureWidth = width + 10;
	private static final int captureHeight = height + 10;

	private final BoundingBox captureBounds = new BoundingBox();

	/**
	 * Constructs a new Pillbox.
	 *
	 * @param args the entity's construction arguments.
	 */
	public Pillbox(ConstructionArgs args) {
		super(args, width, height);
		updateBounds();
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
		captureBounds.updateBounds(this, captureWidth, captureHeight);
	}

	@Override
	protected void onUpdate(World world) {
		heal(hpPerTick);
	}

	@Override
	protected void onOwnerChanged(ActorEntity newOwner) {
		// If the Pillbox gained a new owner, set its health to a small positive value,
		// so another player
		// can't instantly grab it without needing to reduce its health.
		if (newOwner != null) {
			hitPoints = 10;
		}
	}

	/**
	 * Whether the pillbox is ready to fire. Pillboxes with no health won't fire.
	 *
	 * @return true if the cannon is ready to fire.
	 */
	public boolean isCannonReady() {
		return System.currentTimeMillis() > cannonReadyTime && getHitPoints() > 0;
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
	public float getHitPoints() {
		return hitPoints;
	}

	/**
	 * Method that returns the maximum number of hit points the entity can have.
	 *
	 * @return - Max Hit points for the entity
	 */
	@Override
	public int getMaxHitPoints() {
		return MAX_HIT_POINTS;
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
	public void takeHit(float damagePoints, World world) {
		assert damagePoints >= 0;

		Audio.play(Sfx.PILLBOX_HIT);
		hitPoints -= damagePoints;
		if (hitPoints < 0) {
			// Give the player a few seconds to claim the damaged pillbox.
			hitPoints = -10;
		}

		if (hitPoints <= 0) {
			if (isOwnedByLocalPlayer() && owner() != null) {
				setOwnedByLocalPlayer(false);
				setOwner(null);

				Network net = NetworkSystem.getInstance();
				net.send(new ChangeOwner(this));
			}
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
		if (hitPoints > MAX_HIT_POINTS) {
			hitPoints = MAX_HIT_POINTS;
		}
	}

	@Override
	public boolean isSolid() {
		return true;
	}
}
