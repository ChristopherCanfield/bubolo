package bubolo.world;

import static bubolo.util.Units.worldUnitToTile;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

import bubolo.Config;
import bubolo.Systems;
import bubolo.audio.Sfx;
import bubolo.audio.SfxRateLimiter;
import bubolo.net.Network;
import bubolo.net.NetworkCommand;
import bubolo.net.NetworkSystem;
import bubolo.net.command.MovePillboxOffTileMap;
import bubolo.net.command.MovePillboxOntoTileMap;
import bubolo.net.command.UpdatePillboxAttributes;
import bubolo.util.Time;
import bubolo.util.Units;

/**
 * Pillboxes are stationary defensive structures that shoot at enemy tanks, and can be captured. Captured pillboxes do not shoot
 * at their owner.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Pillbox extends ActorEntity implements Damageable, TerrainImprovement {
	/** Time required to reload cannon. */
	private static final float cannonReloadSpeedSeconds = 0.5f;
	private boolean cannonReloaded = true;

	/** Whether the pillbox has a target. */
	private boolean hasTarget;

	/** The direction the pillbox will fire. */
	private float cannonRotation = 0;

	/** Max range to locate a target. The pillbox will not fire unless there is a tank within this range. */
	private float range = 300;

	/** The pillbox's maximum health. */
	private static final int maxHitPoints = 100;

	/** The pillbox's health. */
	private float hitPoints = maxHitPoints;

	// 0.5f / FPS = heals ~0.5 health per second.
	private static final float maxHealthPerTick = 0.5f / Config.FPS;
	private float healthPerTick = maxHealthPerTick;

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
		/** The pillbox is being packed for carriage on a tank. A pillbox in the Unbuilding state can enter the Built or Carried states. */
		Unbuilding,
		/** The pillbox is being carried on a tank. A pillbox in the Carried state can enter any other state. */
		Carried,
		/** The pillbox is being unpacked. A pillbox in the Building state can enter the Built or Carried states. */
		Building
	}

	// Pillboxes start built.
	private BuildStatus buildStatus = BuildStatus.Built;

	private boolean solid = true;

	private static final int width = 29;
	private static final int height = 29;

	private static final int captureBoundsExtraWidth = 14;
	private static final int captureWidth = width + captureBoundsExtraWidth;
	private static final int captureBoundsExtraHeight = 14;
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
				heal(healthPerTick);
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
	 * @param world reference to the game world.
	 * @precondition the pillbox must have an owner for it to be packed.
	 */
	public void unbuild(World world) {
		assert hasOwner();

		builtPct -= buildPctPerTick;
		setBuildStatus(BuildStatus.Unbuilding);

		if (builtPct <= 0 && isOwnedByLocalPlayer()) {
			setBuildStatus(BuildStatus.Carried);
			world.movePillboxOffTileMap(this);
			notifyNetwork(new MovePillboxOffTileMap(this));
		}
	}

	/**
	 * Increase the built percentage. Used to unpack carried pillboxes so they can be placed.
	 *
	 * @param world reference to the game world.
	 * @param targetX a valid x target build location for this pillbox.
	 * @param targetY a valid y target build location for this pillbox.
	 */
	public void build(World world, float targetX, float targetY) {
		builtPct += buildPctPerTick;
		setBuildStatus(BuildStatus.Building);
		int column = worldUnitToTile(targetX);
		int row = worldUnitToTile(targetY);
		setPosition(column * Units.TileToWorldScale, row * Units.TileToWorldScale);

		if (builtPct >= 1 && isOwnedByLocalPlayer()) {
			setBuildStatus(BuildStatus.Built);
			world.movePillboxOntoTileMap(this, tileColumn(), tileRow());
			notifyNetwork(new MovePillboxOntoTileMap(this, tileColumn(), tileRow()));
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

	/**
	 * Drops the pillbox from a tank. The pillbox is built in the nearest valid location, and becomes neutral. This is used
	 * when a tank dies while carrying a pillbox.
	 *
	 * @param world reference to the game world.
	 */
	public void dropFromTank(World world) {
		if (isOwnedByLocalPlayer()) {
			var terrain = world.getNearestBuildableTerrain(owner().x(), owner().y());
			setBuildStatus(BuildStatus.Built);
			world.movePillboxOntoTileMap(this, terrain.tileColumn(), terrain.tileRow());

			notifyNetwork(new MovePillboxOntoTileMap(this, terrain.tileColumn(), terrain.tileRow()));

			setOwner(null);
			notifyNetwork(true);
		}
	}

	private void setBuildStatus(BuildStatus status) {
		buildStatus = status;

		switch (status) {
		case Built:
			builtPct = 1;
			solid = true;
			break;
		case Carried:
			builtPct = 0;
			solid = false;
			break;
		case Unbuilding:
			solid = true;
			break;
		case Building:
			solid = true;
			break;
		}

		notifyNetwork();
	}

	/**
	 * Updates attributes that should only be directly set by the network system.
	 *
	 * @param solid whether the pillbox is solid or not.
	 * @param buildStatus the pillbox's new build status.
	 * @param builtPct the pillbox's new percent built.
	 */
	public void setNetPillboxAttributes(boolean solid, BuildStatus buildStatus, float builtPct) {
		this.solid = solid;
		this.buildStatus = buildStatus;
		this.builtPct = builtPct;
	}

	public enum BuildLocationValidity {
		Buildable,
		InvalidNotBuildable,
		InvalidTankInTheWay
	}

	/**
	 * Specifies whether the target location, in world units, is a valid location to place this pillbox.
	 *
	 * @param world reference to the game world.
	 * @param targetX the x position to place this pillbox, in world units.
	 * @param targetY the y position to place this pillbox, in world units.
	 * @return true if the specified target location is a valid placement location for this pillbox.
	 */
	public static BuildLocationValidity isValidBuildLocationWU(World world, float targetX, float targetY) {
		int tileX = worldUnitToTile(targetX);
		int tileY = worldUnitToTile(targetY);
		return isValidBuildTile(world, tileX, tileY);
	}

	// Used by the isValidBuildTile method.
	private static final BoundingBox targetLocationBoundingBox = new BoundingBox(0, 0, Units.TileToWorldScale, Units.TileToWorldScale);

	/**
	 * Specifies whether the target location, in tiles, is a valid location to place this pillbox.
	 *
	 * @param world reference to the game world.
	 * @param tileX the tile column to place this pillbox.
	 * @param tileY the tile row to place this pillbox.
	 * @return true if the specified target location is a valid placement location for this pillbox.
	 */
	public static BuildLocationValidity isValidBuildTile(World world, int tileX, int tileY) {
		if (world.isValidTile(tileX, tileY) && world.getTerrain(tileX, tileY).isValidBuildTarget()) {
			var terrainImprovement = world.getTerrainImprovement(tileX, tileY);
			if (terrainImprovement == null || terrainImprovement.isValidBuildTarget()) {
				float targetX = Units.TileToWorldScale * tileX;
				float targetY = Units.TileToWorldScale * tileY;
				targetLocationBoundingBox.updateBounds(targetX, targetY, Units.TileToWorldScale, Units.TileToWorldScale);
				for (Tank tank : world.getTanks()) {
					if (Intersector.overlapConvexPolygons(tank.bounds(), targetLocationBoundingBox.bounds())) {
						return BuildLocationValidity.InvalidTankInTheWay;
					}
				}
				return BuildLocationValidity.Buildable;
			}
		}

		return BuildLocationValidity.InvalidNotBuildable;
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

	public float cannonRotation() {
		return cannonRotation;
	}

	/**
	 * Fires the cannon in its current direction.
	 *
	 * @param world reference to world.
	 */
	public void fireCannon(World world) {
		cannonReloaded = false;
		healthPerTick = damageState().healthPerTickWhileFiring;
		world.timer().scheduleSeconds(damageState().reloadSeconds, w -> {
			cannonReloaded = true;
			healthPerTick = maxHealthPerTick;
		});

		var args = new Entity.ConstructionArgs(Entity.nextId(), x(), y(), cannonRotation);
		Bullet bullet = world.addEntity(Bullet.class, args);
		bullet.setOwner(this);
	}

	/**
	 * @return true if the pillbox has a target.
	 */
	public boolean hasTarget() {
		return hasTarget;
	}

	/**
	 * @param value true if the pillbox's targeting system has identified a target, or false otherwise.
	 */
	public void setHasTarget(boolean value) {
		this.hasTarget = value;
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

	public enum DamageState {
		Undamaged(cannonReloadSpeedSeconds, maxHealthPerTick),
		LightlyDamaged(cannonReloadSpeedSeconds * 0.75f, maxHealthPerTick * 0.66f),
		ModeratelyDamaged(cannonReloadSpeedSeconds * 0.5f, maxHealthPerTick * 0.33f),
		SeverelyDamaged(cannonReloadSpeedSeconds * 0.25f, 0),
		OutOfService(0, maxHealthPerTick);

		/** The pillbox's bullet reload time, in seconds. */
		final float reloadSeconds;
		/** The amount of health the pillbox recovers while firing. When not firing, the pillbox recovers health at full speed. */
		final float healthPerTickWhileFiring;

		private DamageState(float reloadSeconds, float healthPerTick) {
			this.reloadSeconds = reloadSeconds;
			this.healthPerTickWhileFiring = healthPerTick;
		}
	}

	public DamageState damageState() {
		var damagePercent = hitPoints() / maxHitPoints();
		if (damagePercent >= 0.9F) {
			return DamageState.Undamaged;
		} else if (damagePercent > 0.60f && damagePercent < 0.85f) {
			return DamageState.LightlyDamaged;
		} else if (damagePercent > 0.30f && damagePercent <= 0.60f) {
			return DamageState.ModeratelyDamaged;
		} else if (damagePercent > 0 && damagePercent <= 0.30f) {
			return DamageState.SeverelyDamaged;
		} else {
			return DamageState.OutOfService;
		}
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
				Systems.audio().play(Sfx.PillboxPowerOff, x(), y());
			}

			capturable = true;
			hitPoints = 0;
		}
	}

	private void onCapturableTimerExpired(World world) {
		capturable = false;
		capturableTimerId = -1;
		Systems.audio().play(Sfx.PillboxPowerOn, x(), y());
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

	/**
	 * Sends a command to the network, followed by an UpdatePillboxAttributes command. If this pillbox is not owned by
	 * the local player, this is a no-op.
	 *
	 * @param additionalNetworkCommand the network commands to send before sending the UpdatePillboxAttributes command.
	 */
	private void notifyNetwork(NetworkCommand additionalNetworkCommand) {
		if (isOwnedByLocalPlayer()) {
			assert !(additionalNetworkCommand instanceof UpdatePillboxAttributes) :
					"Use notifyNetwork(), rather than notifyNetwork(additionalNetworkCommand), to send UpdatePillboxAttributes commands.";

			Network net = NetworkSystem.getInstance();
			net.send(additionalNetworkCommand);

			notifyNetwork();
		}
	}

	/**
	 * Sends pillbox attribute information to the network, if this pillbox is owned by the local player.
	 */
	private void notifyNetwork() {
		notifyNetwork(false);
	}

	/**
	 * Sends pillbox attribute information to the network.
	 *
	 * @param overrideOwnedByLocalPlayerCheck if true, the network command is sent regardless of whether this pillbox is owned
	 * by the local player.
	 */
	private void notifyNetwork(boolean overrideOwnedByLocalPlayerCheck) {
		if (isOwnedByLocalPlayer() || overrideOwnedByLocalPlayerCheck) {
			Network net = NetworkSystem.getInstance();
			net.send(new UpdatePillboxAttributes(this));
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
