package bubolo.world;

import com.badlogic.gdx.math.Polygon;

import bubolo.Config;
import bubolo.audio.Sfx;
import bubolo.audio.SfxRateLimiter;
import bubolo.util.Coords;
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

	/** The percentage the pillbox is built, which determines if it can be moved or not. */
	private float builtPct = 1;
	private static final float buildTimeSeconds = 3;
	private static final float buildPctPerTick = 1.0f / Time.secondsToTicks(buildTimeSeconds);

	public enum BuildStatus {
		/** The pillbox is built. A pillbox in the built state can enter the Packing state. */
		Built,
		/** The pillbox is being packed for carriage on a tank. A pillbox in the Packing state can enter the Built or Packing states. */
		Packing,
		/** The pillbox is being carried on a tank. A pillbox in the Carried state can enter any other state. */
		Carried,
		/** The pillbox is being unpacked. A pillbox in the Unpacking state can enter the Built or Carried states. */
		Unpacking
	}

	// Pillboxes start built.
	private BuildStatus buildStatus = BuildStatus.Built;

	private boolean solid = true;

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
		assert !(buildStatus == BuildStatus.Built && builtPct < 1);

		if (buildStatus == BuildStatus.Built) {
			assert isSolid();

			if (!capturable) {
				heal(hpPerTick);
			}
		} else {
			if (buildStatus == BuildStatus.Carried) {
				assert !isSolid();
				setPosition(owner().x(), owner().y());
			}
		}
	}

	@Override
	protected void onOwnerChanged(ActorEntity newOwner) {
		// If the Pillbox gained a new owner, set its health to a small positive value,
		// so another player can't instantly grab it without needing to reduce its health.
		if (newOwner != null) {
			hitPoints = 5;
			setBuildStatus(BuildStatus.Built);
		}
	}

	public BuildStatus buildStatus() {
		return buildStatus;
	}

	/**
	 * Shorthand for {@code buildStatus() == BuildStatus.Carried}
	 *
	 * @return true if the pillbox is being carried.
	 */
	public boolean isBeingCarried() {
		return buildStatus == BuildStatus.Carried;
	}

	/**
	 * Decreases the built percentage. Used to pack placed pillboxes onto a tank, so they can be carried and relocated.
	 *
	 * @precondition the pillbox must have an owner for it to be packed.
	 */
	public void packForCarrying() {
		assert hasOwner();

		System.out.println("packForCarrying");
		builtPct -= buildPctPerTick;
		setBuildStatus(BuildStatus.Packing);

		if (builtPct <= 0) {
			setBuildStatus(BuildStatus.Carried);
		}
	}

	/**
	 * Increase the built percentage. Used to unpack carried pillboxes so they can be placed.
	 *
	 * @param targetX a valid x target build location for this pillbox.
	 * @param targetY a valid y target build location for this pillbox.
	 */
	public void unpackForPlacement(float targetX, float targetY) {
		System.out.println("unpackForPlacement");

		builtPct += buildPctPerTick;
		setBuildStatus(BuildStatus.Unpacking);
		setPosition(targetX, targetY);

		if (builtPct >= 1) {
			setBuildStatus(BuildStatus.Built);
		}
	}

	/**
	 * @return the percent packed this pillbox is, from 0 to 1.
	 */
	public float builtPct() {
		return builtPct;
	}

	/**
	 * Sets the built percent to 100%.
	 */
	public void cancelUnbuilding() {
		setBuildStatus(BuildStatus.Built);
	}

	/**
	 * Sets the built percent to 0%.
	 */
	public void cancelBuilding() {
		setBuildStatus(BuildStatus.Carried);
	}

	private void setBuildStatus(BuildStatus status) {
		buildStatus = status;
		System.out.println("New pillbox build status: " + status);

		switch (status) {
		case Built:
			builtPct = 1;
			solid = true;
			break;
		case Carried:
			builtPct = 0;
			solid = false;
			break;
		case Packing:
			solid = true;
			break;
		case Unpacking:
			solid = true;
			break;
		}
	}

	/**
	 * Specifies whether the target location, in world units, is a valid location to place this pillbox.
	 *
	 * @param world reference to the game world.
	 * @param targetX the x position to place this pillbox, in world units.
	 * @param targetY the y position to place this pillbox, in world units.
	 * @return true if the specified target location is a valid placement location for this pillbox.
	 */
	public boolean isValidBuildLocation(World world, float targetX, float targetY) {
		int tileX = Math.round(targetX / Coords.TileToWorldScale);
		int tileY = Math.round(targetY / Coords.TileToWorldScale);
		if (world.isValidTile(tileX, tileY) && world.getTerrain(tileX, tileY).isValidBuildTarget()) {
			var terrainImprovement = world.getTerrainImprovement(tileX, tileY);
			return (terrainImprovement == null || terrainImprovement.isValidBuildTarget());
		}

		return false;
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
		return solid;
	}

	@Override
	public boolean isValidBuildTarget() {
		return false;
	}
}
