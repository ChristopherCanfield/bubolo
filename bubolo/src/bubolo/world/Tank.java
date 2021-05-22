package bubolo.world;

import static com.badlogic.gdx.math.MathUtils.clamp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.Vector2;

import bubolo.audio.Audio;
import bubolo.audio.Sfx;
import bubolo.audio.SfxRateLimiter;
import bubolo.controllers.Controller;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.CreateBullet;
import bubolo.net.command.CreateEntity;
import bubolo.net.command.NetTankAttributes;
import bubolo.net.command.UpdateTankAttributes;
import bubolo.util.Coords;
import bubolo.util.Nullable;
import bubolo.world.Pillbox.BuildStatus;

/**
 * The tank, which may be controlled by a local player or networked player..
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Tank extends ActorEntity implements Damageable {
	private String playerName;

	// Max speed in world units per tick.
	private static final float maxSpeed = 3.25f;

	// The maximum speed after adjusting for the underlying terrain & terrain improvement.
	private float adjustedMaxSpeed = maxSpeed;

	// The tank's current speed.
	private float speed = 0;

	// The rate of acceleration, in world units per tick.
	private static final float accelerationRate = 0.01f;

	// The acceleration rate after adjusting for the underlying terrain and terrain improvement.
	private float adjustedAccelerationRate = accelerationRate;

	// The rate of deceleration, in world units per tick.
	private static final float decelerationRate = 0.05f;

	// The tank's rate of rotation per tick.
	private static final float rotationRate = 0.03f;

	// Whether the tank accelerated this tick.
	private boolean accelerated;

	// Whether the tank decelerated this tick.
	private boolean decelerated;

	// Whether the tank rotated this tick.
	private boolean rotated;

	// Whether the tank is hidden.
	private boolean hidden;

	// The reload speed of the tank's cannon, in milliseconds.
	private static final long cannonReloadSpeed = 500;

	// The time that the tank will respawn.
	private long nextRespawnTime;

	private static final long respawnTimeMillis = 2000L;

	// The last time that the cannon was fired.
	private long cannonReadyTime = 0;

	// Minimum amount of time between laying mines.
	private static final long mineLayingSpeedMillis = 1_000;

	// The next time a mine will be ready to be laid.
	private long mineReadyTime = 0;

	// Used for movement collision detection.
	private final Circle boundingCircle;
	private final float boundingCircleRadius = 6.0f;

	// Additional amount that the tank will bounce off a solid object.
	private static final float collisionBounce = 0.25f;

	// The previous x position. Used for collision handling.
	private float previousX = 0;
	// The previous y position. Used for collision handling.
	private float previousY = 0;

	private static final int maxHitPoints = 100;
	private float hitPoints = maxHitPoints;

	// Whether the tank's death was caused by drowning.
	private boolean drowned;

	private static final int maxAmmo = 100;
	private int ammoCount = maxAmmo;

	private static final int maxMines = 10;
	private int mineCount = maxMines;

	private static final int width = 20;
	private static final int height = 20;

	private final SfxRateLimiter sfxPlayer = new SfxRateLimiter(150);

	private final List<Controller> controllers = new ArrayList<>();

	private boolean controlledByLocalPlayer;

	// The pillbox that is being carried, built, or unbuilt (packed).
	private Pillbox carriedPillbox;
	private boolean unbuildPillbox;
	private boolean buildPillbox;
	private float pillboxBuildLocationX = -1;
	private float pillboxBuildLocationY = -1;

	/**
	 * Constructs a Tank.
	 *
	 * @param args the entity's construction arguments.
	 * @param world reference to the game world.
	 */
	protected Tank(ConstructionArgs args, World world) {
		super(args, width, height);

		boundingCircle = new Circle(x(), y(), boundingCircleRadius);
		updateBounds();
	}

	private void respawn(World world) {
		// Don't allow the tank to respawn until its respawn timer has expired.
		if (nextRespawnTime < System.currentTimeMillis() && isOwnedByLocalPlayer()) {
			// Loop until a suitable spawn point is found.
			boolean spawnFound = false;
			do {
				Spawn spawn = world.getRandomSpawn();
				setPosition(spawn.x(), spawn.y());
				// Ensure there are no tanks on top of, or adjacent to, the selected spawn location.
				spawnFound = world.getCollidablesWithinTileDistance(this, 4, true, Tank.class).isEmpty();
			} while (!spawnFound);

			hitPoints = maxHitPoints;
			drowned = false;
			ammoCount = maxAmmo;
			mineCount = maxMines;
			cannonReadyTime = 0;
			mineReadyTime = 0;

			speed = 0;
			adjustedMaxSpeed = maxSpeed;
			accelerated = false;
			decelerated = false;
			rotated = false;

			hidden = false;

			carriedPillbox = null;
			pillboxBuildLocationX = pillboxBuildLocationY = 0;

			notifyNetwork();
		}
	}

	/**
	 * For the network.
	 * @param drowning true if the tank is drowning.
	 */
	public void setDrowning(boolean drowning) {
		this.drowned = drowning;
	}

	@Override
	public void updateBounds() {
		super.updateBounds();
		boundingCircle.setPosition(x(), y());
	}

	@Override
	public void onUpdate(World world) {
		if (!isAlive() || checkForDrowned(world)) {
			respawn(world);
			return;
		}

		updateSpeedForTerrain(world);
		moveTank(world);
		performCollisionDetection(world);
		processPillboxBuilding(world);
		hidden = checkIfHidden(world);

		decelerated = false;
		accelerated = false;
		rotated = false;

		if (isOwnedByLocalPlayer()) {
			Audio.setListenerPosition(x(), y());
			int distanceToDeepWater = world.getTileDistanceToDeepWater(tileColumn(), tileRow(), 15) * Coords.TileToWorldScale;
			Audio.setListenerDistanceToDeepWater(distanceToDeepWater);
		}
	}

	public String playerName() {
		return playerName;
	}

	public void setPlayerName(String name) {
		this.playerName = name;
	}

	/**
	 * Returns the tile column at the center of the tank.
	 *
	 * @return the tile column at the center of the tank.
	 */
	@Override
	public int tileColumn() {
		return (int) (x() + width) / Coords.TileToWorldScale;
	}

	/**
	 * Returns the tile row at the center of the tank.
	 *
	 * @return the tile row at the center of the tank.
	 */
	@Override
	public int tileRow() {
		return (int) (y() + height) / Coords.TileToWorldScale;
	}

	public float centerX() {
		return x() + (width() / 2.0f);
	}

	public float centerY() {
		return y() + (height() / 2.0f);
	}

	/**
	 * Returns the tank's speed.
	 *
	 * @return the tank's speed.
	 */
	public float speed() {
		return speed;
	}

	/**
	 * Sets some of the tank's non-public attributes. For use with the network system.
	 *
	 * @param netTankAttributes a NetTankAttributes object that contains the tank's new attributes.
	 */
	public void setNetAttributes(NetTankAttributes netTankAttributes) {
		if (netTankAttributes.speed > speed) {
			accelerated = true;
		} else if (netTankAttributes.speed < speed) {
			decelerated = true;
		}

		this.speed = netTankAttributes.speed;
		this.hitPoints = netTankAttributes.hitPoints;
	}

	/**
	 * Accelerates the tank.
	 */
	public void accelerate() {
		if (speed < adjustedMaxSpeed && !accelerated) {
			speed += adjustedAccelerationRate;
			if (speed > adjustedMaxSpeed) {
				speed = adjustedMaxSpeed;
			}
			accelerated = true;
			cancelBuildingPillbox();
		}
		clampSpeed();

		notifyNetwork();
	}

	/**
	 * Decelerates the tank.
	 */
	public void decelerate() {
		if (speed > 0 && !decelerated) {
			speed -= decelerationRate;
			decelerated = true;
			cancelBuildingPillbox();
		}
		clampSpeed();

		notifyNetwork();
	}

	/**
	 * Ensures that the tank's speed remains between 0 and modifiedMaxSpeed.
	 */
	private void clampSpeed() {
		speed = clamp(speed, 0, adjustedMaxSpeed);
	}

	/**
	 * Rotates the tank clockwise.
	 */
	public void rotateRight() {
		if (!rotated) {
			rotated = true;
			setRotation(rotation() + rotationRate);
			cancelBuildingPillbox();
			notifyNetwork();
		}
	}

	/**
	 * Rotates the tank counter-clockwise.
	 */
	public void rotateLeft() {
		if (!rotated) {
			rotated = true;
			setRotation(rotation() - rotationRate);
			cancelBuildingPillbox();
			notifyNetwork();
		}
	}

	/**
	 * Returns true if the cannon is ready to fire.
	 *
	 * @return true if the cannon is ready to fire.
	 */
	public boolean isCannonReady() {
		return (System.currentTimeMillis() - cannonReadyTime > cannonReloadSpeed) && ammoCount > 0 && isAlive();
	}

	/**
	 * Fires the tank's cannon, if the cannon is ready.
	 *
	 * @param world  reference to the world.
	 *
	 * @return bullet reference to the new bullet or null if the tank cannot fire.
	 */
	public Bullet fireCannon(World world) {
		cancelBuildingPillbox();

		if (isCannonReady()) {
			cannonReadyTime = System.currentTimeMillis();

			float tankHalfWidth = width() / 2.0f;
			float tankHalfHeight = height() / 1.5f;
			float bulletX = x() + tankHalfWidth * (float) Math.cos(rotation());
			float bulletY = y() + tankHalfHeight * (float) Math.sin(rotation());

			var args = new Entity.ConstructionArgs(Entity.nextId(), bulletX, bulletY, rotation());
			Bullet bullet = world.addEntity(Bullet.class, args);
			bullet.setOwner(this);

			ammoCount--;

			Network net = NetworkSystem.getInstance();
			net.send(new CreateBullet(bullet.id(), bullet.x(), bullet.y(), bullet.rotation(), id()));

			return bullet;
		} else {
			return null;
		}
	}

	public boolean isCarryingPillbox() {
		return carriedPillbox != null && carriedPillbox.buildStatus() == BuildStatus.Carried;
	}

	/**
	 * @return the carried pillbox's ID, or null if no pillbox is being carried.
	 */
	public @Nullable UUID carriedPillboxId() {
		return isCarryingPillbox() ? carriedPillbox.id() : null;
	}

	/**
	 * Builds the carried pillbox, if the tank is carrying one, or unbuilds the nearest friendly pillbox, if one is within range.
	 * If the tank is not carrying a pillbox, and no friendly pillbox is within range, no action occurs.
	 *
	 * @param world reference to the game world.
	 */
	public void buildPillbox(World world) {
		if (isCarryingPillbox()) {
			if (!buildPillbox) {
				buildPillbox = findBuildLocationForPillbox(world);
			}
		} else {
			if (!unbuildPillbox) {
				unbuildPillbox = unbuildNearestFriendlyPillbox(world);
			}
		}
	}

	/**
	 * Cancels building or unbuilding a pillbox. If no pillbox is being built or unbuilt, no action occurs.
	 */
	public void cancelBuildingPillbox() {
		if (carriedPillbox != null) {
			if (unbuildPillbox) {
				carriedPillbox.cancelUnbuilding();
				carriedPillbox = null;
			} else if (buildPillbox) {
				if (carriedPillbox.buildStatus() == BuildStatus.Built) {
					// If the pillbox was built, stop carrying it.
					carriedPillbox = null;
				} else {
					carriedPillbox.cancelBuilding();
				}
			}
		}

		unbuildPillbox = false;
		buildPillbox = false;
		pillboxBuildLocationX = pillboxBuildLocationY = -1;
	}

	/**
	 * Starts unbuilding the nearest friendly pillbox, if one is within range.
	 *
	 * @param world reference to the game world.
	 * @return true if a friendly pillbox was found within range.
	 */
	private boolean unbuildNearestFriendlyPillbox(World world) {
		if (carriedPillbox == null) {
			var pillboxes = world.getCollidablesWithinTileDistance(this, 1, true, Pillbox.class);
			if (!pillboxes.isEmpty()) {
				float maxDistanceWorldUnits = Coords.TileToWorldScale;
				float targetLineX = (float) Math.cos(rotation()) * maxDistanceWorldUnits + centerX();
				float targetLineY = (float) Math.sin(rotation()) * maxDistanceWorldUnits + centerY();

				for (Collidable collidable : pillboxes) {
					Pillbox pillbox = (Pillbox) collidable;
					if (pillbox.isOwnedByLocalPlayer() &&
							Intersector.intersectSegmentPolygon(new Vector2(x(), y()), new Vector2(targetLineX, targetLineY), pillbox.bounds())) {

						carriedPillbox = (Pillbox) collidable;
						return true;
					}
				}
			}
			// No friendly pillbox found within range.
			return false;
		}
		// Already carrying a pillbox.
		return true;
	}

	/**
	 * Attempts to find a valid build location for the carried pillbox.
	 *
	 * @param world reference to the game world.
	 * @return true if a valid build location was found, or false if one was not found or no pillbox is being carried.
	 */
	private boolean findBuildLocationForPillbox(World world) {
		if (isCarryingPillbox()) {
			if (!hasPillboxBuildLocationTarget()) {
				float placementDistanceWorldUnits = Coords.TileToWorldScale;
				float targetX = (float) Math.cos(rotation()) * placementDistanceWorldUnits + centerX();
				float targetY = (float) Math.sin(rotation()) * placementDistanceWorldUnits + centerY();

				if (Pillbox.isValidBuildLocationWU(world, targetX, targetY)) {
					pillboxBuildLocationX = targetX;
					pillboxBuildLocationY = targetY;
					return true;
				} else {
					// No valid location found.
					return false;
				}
			}
			// Already has a pillbox placement target.
			return true;
		}
		// Isn't carrying a pillbox.
		return false;
	}

	private boolean hasPillboxBuildLocationTarget() {
		return pillboxBuildLocationX >= 0 && pillboxBuildLocationY >= 0;
	}

	/**
	 * Process building or unbuilding a pillbox. Called once per tick.
	 */
	private void processPillboxBuilding(World world) {
		if (carriedPillbox != null) {
			if (unbuildPillbox) {
				processPillboxPacking(world);
			} else if (buildPillbox) {
				processPillboxUnpacking(world);
			} else {
				carriedPillbox.setPosition(x(), y());
			}
		}
	}

	private void processPillboxPacking(World world) {
		// Pack the pillbox if it is being packed.
		if (carriedPillbox.buildStatus() != BuildStatus.Carried && carriedPillbox.builtPct() > 0) {
			carriedPillbox.unbuild(world);
		} else if (carriedPillbox.buildStatus() == BuildStatus.Carried) {
			unbuildPillbox = false;
		}
	}

	private void processPillboxUnpacking(World world) {
		if (hasPillboxBuildLocationTarget() && carriedPillbox.buildStatus() != BuildStatus.Built) {
			carriedPillbox.build(world, pillboxBuildLocationX, pillboxBuildLocationY);
		} else if (carriedPillbox.buildStatus() == BuildStatus.Built) {
			// If the pillbox was built, stop carrying it.
			carriedPillbox = null;
			buildPillbox = false;
		}
	}

	/**
	 * @return true if the Tank is hidden, false otherwise.
	 */
	public boolean isHidden() {
		return hidden || !isAlive();
	}

	private static final Intersector.MinimumTranslationVector minTranslationVector = new MinimumTranslationVector();

	private boolean checkIfHidden(World world) {
		// If this much of the tank is covered by one or more tree tiles, it counts as hidden.
		final float minTreeCoverage = width() * 0.7f;

		float treeCoverage = 0;
		List<Collidable> trees = world.getCollidablesWithinTileDistance(this, 1, false, Tree.class);
		for (var tree : trees) {
			if (tree.overlapsEntity(this, minTranslationVector)) {
				if (minTranslationVector.depth > minTreeCoverage) {
					return true;
				} else {
					treeCoverage += minTranslationVector.depth;
				}
			}
		}
		return treeCoverage >= minTreeCoverage;
	}

	/**
	 * Checks if the tank has drowned.
	 *
	 * @param world reference to the game world.
	 * @return true if the tank has drowned.
	 */
	private boolean checkForDrowned(World world) {
		var terrain = world.getTerrain(tileColumn(), tileRow());
		if (terrain instanceof DeepWater) {
			drowned = true;
			Audio.play(Sfx.TankDrowned, x(), y());
			onDeath(world);
			return true;
		}
		return false;
	}

	/**
	 * Updates the tank's max speed and acceleration for the underlying terrain.
	 *
	 * @param world reference to the game world.
	 */
	private void updateSpeedForTerrain(World world) {
		var terrainImprovement = world.getTerrainImprovement(tileColumn(), tileRow());
		if (terrainImprovement != null && terrainImprovement.speedModifier() > 0) {
			adjustedMaxSpeed = maxSpeed * terrainImprovement.speedModifier();
			adjustedAccelerationRate = accelerationRate * terrainImprovement.speedModifier();
		} else {
			var terrain = world.getTerrain(tileColumn(), tileRow());
			adjustedMaxSpeed = maxSpeed * terrain.speedModifier();
			adjustedAccelerationRate = accelerationRate * terrain.speedModifier();
		}
		clampSpeed();
	}

	/**
	 * Updates the Tank's world position according to its speed, acceleration/deceleration state, and collision information.
	 *
	 * @param world reference to the game world.
	 */
	private void moveTank(World world) {
		float amountMovedX = (float) Math.cos(rotation()) * speed;
		float amountMovedY = (float) Math.sin(rotation()) * speed;

		float newX = x() + amountMovedX;
		float newY = y() + amountMovedY;

		// Prevent the tank from exiting the game world.
		if (!world.containsPoint(newX, newY)) {
			return;
		}

		previousX = x();
		previousY = y();
		setPosition(newX, newY);
	}

	private void performCollisionDetection(World world) {
		int dirX = (x() - previousX) > 0 ? 1 : -1;
		int dirY = (y() - previousY) > 0 ? 1 : -1;

		// Search for collisions. If one is found, move the tank back to its previous position, plus an
		// offset defined by collisionBounce.
		var adjacentCollidables = world.getCollidablesWithinTileDistance(this, 1, true, null);
		for (var collider : adjacentCollidables) {
			if (Intersector.overlaps(boundingCircle, collider.bounds().getBoundingRectangle())) {
				float newX = previousX + (-dirX * collisionBounce);
				float newY = previousY + (-dirY * collisionBounce);
				setPosition(newX, newY);

				decelerate();
				break;
			}
		}

		// Check again for collisions. If one is found, move the tank back to its previous position, without the
		// collisionBounce offset. This prevents the tank from tunneling through solid objects, which could occur
		// if the tank is bounced into a solid object.
		for (var collider : adjacentCollidables) {
			if (Intersector.overlaps(boundingCircle, collider.bounds().getBoundingRectangle())) {
				setPosition(previousX, previousY);

				break;
			}
		}
	}

	/**
	 * Sends tank attribute information to the network.
	 */
	private void notifyNetwork() {
		if (isOwnedByLocalPlayer()) {
			Network net = NetworkSystem.getInstance();
			net.send(new UpdateTankAttributes(this));
		}
	}

	/**
	 * Returns the current health of the tank
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

	public boolean drowned() {
		return drowned;
	}

	/**
	 * Returns the current ammo count of the tank
	 *
	 * @return current ammo count
	 */
	public int ammoCount() {
		return ammoCount;
	}

	/**
	 * Returns the number of mines the tank currently contains
	 *
	 * @return the current mine count
	 */
	public int mineCount() {
		return mineCount;
	}

	/**
	 * Changes the hit point count after taking damage
	 *
	 * @param damagePoints how much damage the tank has taken
	 */
	@Override
	public void receiveDamage(float damagePoints, World world) {
		assert (damagePoints >= 0);

		if (!isDisposed() && hitPoints > 0) {
			hitPoints -= damagePoints;

			notifyNetwork();
			sfxPlayer.play(Sfx.TankHit, x(), y());

			if (hitPoints <= 0) {
				Audio.play(Sfx.TankExplosion, x(), y());
				onDeath(world);
			}
		}
	}

	/**
	 * Called when the tank dies.
	 */
	private void onDeath(World world) {
		hitPoints = 0;
		nextRespawnTime = System.currentTimeMillis() + respawnTimeMillis;
		if (isCarryingPillbox()) {
			carriedPillbox.dropFromTank(world);
			carriedPillbox = null;
		}
		notifyNetwork();
	}

	/**
	 * Heals the tank.
	 *
	 * @param healPoints the amount of health the tank will receive.
	 */
	private void heal(float healPoints) {
		if (hitPoints + Math.abs(healPoints) < maxHitPoints) {
			hitPoints += Math.abs(healPoints);
		} else {
			hitPoints = maxHitPoints;
		}
	}

	/**
	 * Refuels the tank.
	 *
	 * @param healPoints the amount to heal the tank. >= 0.
	 * @param ammo the amount of ammo to add to the tank's stores. >= 0.
	 * @param mines the number of mines to add to the tank's stores. >= 0.
	 */
	public void refuel(float healPoints, int ammo, int mines) {
		heal(healPoints);
		refuelAmmo(ammo);
		refuelMines(mines);
	}

	/**
	 * Supplies the tank with the specified amount of ammo.
	 *
	 * @param ammo the amount of ammo to increase the tank's ammo by.
	 */
	private void refuelAmmo(int ammo) {
		assert ammo >= 0;
		ammoCount += ammo;
		if (ammoCount > maxAmmo) {
			ammoCount = maxAmmo;
		}
	}

	/**
	 * Supplies the tank with the specified number of mines.
	 *
	 * @param mines the number of mines to add to the tank's stores.
	 */
	private void refuelMines(int mines) {
		assert mines >= 0;
		mineCount += mines;
		if (mineCount > maxMines) {
			mineCount = maxMines;
		}
	}

	/**
	 * Creates the mine in world and passes it back to the caller. Returns null if a mine can't be created.
	 *
	 * @param world reference to the game world.
	 * @return The mine, or null if a mine is unable to be placed.
	 */
	public Mine placeMine(World world) {
		if (canPlaceMineHere(world)) {
			mineReadyTime = System.currentTimeMillis() + mineLayingSpeedMillis;

			int tileX = Math.round(x() / Coords.TileToWorldScale);
			int tileY = Math.round(y() / Coords.TileToWorldScale);
			int mineX = tileX * Coords.TileToWorldScale;
			int mineY = tileY * Coords.TileToWorldScale;

			var args = new Entity.ConstructionArgs(Entity.nextId(), mineX, mineY, 0);
			Mine mine = world.addEntity(Mine.class, args);

			mineCount--;

			Network net = NetworkSystem.getInstance();
			net.send(new CreateEntity(Mine.class, mine.id(), mine.x(), mine.y(), mine.rotation()));

			return mine;
		}
		return null;
	}

	private boolean canPlaceMineHere(World world) {
		if (mineReadyTime < System.currentTimeMillis() && mineCount > 0) {
			int tileX = Math.round(x() / Coords.TileToWorldScale);
			int tileY = Math.round(y() / Coords.TileToWorldScale);

			Terrain terrain = world.getTerrain(tileX, tileY);
			if (terrain.isValidBuildTarget() && world.getMine(tileX, tileY) == null) {
				TerrainImprovement terrainImprovement = world.getTerrainImprovement(tileX, tileY);
				return (terrainImprovement == null || terrainImprovement.isValidBuildTarget());
			}
			return false;
		}
		return false;
	}

	/**
	 * @return the tank's max ammo count.
	 */
	public int maxAmmo() {
		return maxAmmo;
	}

	/**
	 * @return the tank's max mine count.
	 */
	public int maxMines() {
		return maxMines;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	/**
	 * Attaches a controller to this tank. Tanks can have multiple attached controllers.
	 *
	 * @param c the controller to add.
	 */
	@Override
	public void addController(Controller c) {
		controllers.add(c);
	}

	@Override
	protected void updateControllers(World world) {
		controllers.forEach(controller -> controller.update(world));
	}

	public void setControlledByLocalPlayer(boolean controlledByLocal) {
		this.controlledByLocalPlayer = controlledByLocal;
	}

	@Override
	public boolean isOwnedByLocalPlayer() {
		return controlledByLocalPlayer;
	}
}
