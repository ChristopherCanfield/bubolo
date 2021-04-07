package bubolo.world;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;

import bubolo.audio.Audio;
import bubolo.audio.Sfx;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.MoveTank;
import bubolo.net.command.NetTankSpeed;
import bubolo.util.Coords;

/**
 * The tank, which may be controlled by a local player or networked player..
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Tank extends ActorEntity implements Damageable {
	private String playerName;

	// Max speed in world units per tick.
	private static final float maxSpeed = 4.f;

	// The maximum speed after tanking account the underlying terrain & terrainImprovement.
	private float modifiedMaxSpeed = maxSpeed;

	// The tank's current speed.
	private float speed = 0;

	// The rate of acceleration, in pixels per tick.
	private static final float accelerationRate = 0.01f;

	// The rate of deceleration, in pixels per tick.
	private static final float decelerationRate = 0.035f;

	// The tank's rate of rotation per tick.
	private static final float rotationRate = 0.03f;

	// Specifies whether the tank accelerated this tick.
	private boolean accelerated;

	// Specifies whether the tank decelerated this tick.
	private boolean decelerated;

	// Specifies whether the tank is hidden in trees
	private boolean hidden;

	// The reload speed of the tank's cannon, in milliseconds.
	private static final long cannonReloadSpeed = 500;

	// The time that the tank will respawn.
	private long nextRespawnTime;

	private static final long respawnTimeMillis = 1000L;

	// The last time that the cannon was fired.
	private long cannonReadyTime = 0;

	// Minimum amount of time between laying mines.
	private static final long mineLayingSpeedMillis = 500;

	// The next time a mine will be ready to be laid.
	private long mineReadyTime = 0;

	// Used for movement collision detection.
	private final Circle boundingCircle;
	private final float boundingCircleRadius = 6.0f;

	// The additional amount that the tank will bounce off a solid object.
	private static final float collisionBounce = 0.25f;

	// The previous x position. Used for collision handling.
	private float previousX = 0;
	// The previous y position. Used for collision handling.
	private float previousY = 0;

	private static final int maxHitPoints = 100;
	private float hitPoints = maxHitPoints;

	private static final int maxAmmo = 100;
	private int ammoCount = maxAmmo;

	private static final int maxMines = 10;
	private int mineCount = maxMines;

	private static final Random randomGenerator = new Random();

	private static final int width = 20;
	private static final int height = 20;

	/**
	 * Constructs a Tank.
	 *
	 * @param args the entity's construction arguments.
	 */
	protected Tank(ConstructionArgs args) {
		super(args, width, height);

		boundingCircle = new Circle(x(), y(), boundingCircleRadius);
		updateBounds();
	}

	private void respawn(World world) {
		// Don't allow the tank to respawn until its respawn timer has expired.
		if (nextRespawnTime > System.currentTimeMillis()) {
			var spawns = world.getSpawns();
			if (spawns.size() > 0) {
				Spawn spawn = spawns.get(randomGenerator.nextInt(spawns.size()));
				setPosition(spawn.x(), spawn.y());

				Network net = NetworkSystem.getInstance();
				net.send(new MoveTank(this));
			}

			hitPoints = maxHitPoints;
			ammoCount = maxAmmo;
			mineCount = maxMines;
			cannonReadyTime = 0;
			mineReadyTime = 0;

			speed = 0;
			modifiedMaxSpeed = maxSpeed;
			accelerated = false;
			decelerated = false;
			hidden = false;
		}
	}

	@Override
	public void updateBounds() {
		super.updateBounds();
		boundingCircle.setPosition(x(), y());
	}

	@Override
	public void onUpdate(World world) {
		if (!isAlive()) {
			respawn(world);
			return;
		}

		updateSpeedForTerrain(world);
		moveTank(world);
		performCollisionDetection(world);
		hidden = checkIfHidden(world);

		decelerated = false;
		accelerated = false;
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

	/**
	 * Returns the tank's speed.
	 *
	 * @return the tank's speed.
	 */
	public float speed() {
		return speed;
	}

	/**
	 * Sets the tank's speed. For use with the network system.
	 *
	 * @param netTankSpeed a NetTankSpeed object that contains the tank's new speed.
	 */
	public void setSpeed(NetTankSpeed netTankSpeed) {
		if (netTankSpeed.getSpeed() > speed) {
			accelerated = true;
		}

		this.speed = netTankSpeed.getSpeed();
	}

	/**
	 * Accelerates the tank.
	 */
	public void accelerate() {
		if (speed < modifiedMaxSpeed && !accelerated) {
			speed += accelerationRate;
			if (speed > modifiedMaxSpeed) {
				speed = modifiedMaxSpeed;
			}
			accelerated = true;
		}
		clampSpeed();
	}

	/**
	 * Decelerates the tank.
	 */
	public void decelerate() {
		if (speed > 0 && !decelerated) {
			speed -= decelerationRate;
			decelerated = true;
		}
		clampSpeed();
	}

	/**
	 * Ensures that the tank's speed remains between 0 and modifiedMaxSpeed.
	 */
	private void clampSpeed() {
		if (speed > modifiedMaxSpeed) {
			speed = modifiedMaxSpeed;
		} else if (speed < 0) {
			speed = 0;
		}
	}

	/**
	 * Rotates the tank clockwise.
	 */
	public void rotateRight() {
		setRotation(rotation() + rotationRate);
	}

	/**
	 * Rotates the tank counter-clockwise.
	 */
	public void rotateLeft() {
		setRotation(rotation() - rotationRate);
	}

	/**
	 * Returns true if the cannon is ready to fire.
	 *
	 * @return true if the cannon is ready to fire.
	 */
	public boolean isCannonReady() {
		return (System.currentTimeMillis() - cannonReadyTime > cannonReloadSpeed);
	}

	/**
	 * Fires the tank's cannon, which adds a bullet to the world and initiates a cannon reload.
	 *
	 * @param world  reference to the world.
	 * @param startX the bullet's start x position.
	 * @param startY the bullet's start y position.
	 *
	 * @return bullet reference to the new bullet or null if the tank cannot fire.
	 */
	public Bullet fireCannon(World world, float startX, float startY) {
		if ((ammoCount > 0) && (cannonReadyTime - System.currentTimeMillis() < 0)) {
			cannonReadyTime = System.currentTimeMillis();

			var args = new Entity.ConstructionArgs(UUID.randomUUID(), startX, startY, rotation());
			Bullet bullet = world.addEntity(Bullet.class, args);
			bullet.setOwner(this);

			ammoCount--;

			return bullet;
		}

		else {
			return null;
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
		List<Collidable> trees = world.getNearbyCollidables(this, false, 1, Tree.class);
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
	 * Updates the tank's speed for the underlying terrain.
	 *
	 * @param world reference to the game world.
	 */
	private void updateSpeedForTerrain(World world) {
		var terrainImprovement = world.getTerrainImprovement(tileColumn(), tileRow());
		if (terrainImprovement != null && terrainImprovement.speedModifier() > 0) {
			modifiedMaxSpeed = maxSpeed * terrainImprovement.speedModifier();
		} else {
			var terrain = world.getTerrain(tileColumn(), tileRow());
			modifiedMaxSpeed = maxSpeed * terrain.speedModifier();
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
		if (world.containsPoint(newX, newY)) {
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
		var adjacentCollidables = world.getNearbyCollidables(this, true, 1, null);
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

		if (hitPoints > 0) {
			hitPoints -= damagePoints;
			Audio.play(Sfx.TANK_HIT);

			if (hitPoints <= 0) {
				onDeath();
			}
		}
	}

	/**
	 * Called when the tank dies.
	 */
	private void onDeath() {
		Audio.play(Sfx.TANK_EXPLOSION);
		nextRespawnTime = System.currentTimeMillis() + respawnTimeMillis;
	}

	/**
	 * Increments the tanks health by a given amount
	 *
	 * @param healPoints - how many points the tank is given
	 */
	@Override
	public void heal(float healPoints) {
		if (hitPoints + Math.abs(healPoints) < maxHitPoints) {
			hitPoints += Math.abs(healPoints);
		} else {
			hitPoints = maxHitPoints;
		}
	}

	/**
	 * Supplies the tank with the specified amount of ammo.
	 *
	 * @param ammo the amount of ammo to increase the tank's ammo by.
	 */
	public void collectAmmo(int ammo) {
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
	public void collectMines(int mines) {
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

			float mineX = x();
			float mineY = y();

			var args = new Entity.ConstructionArgs(UUID.randomUUID(), mineX, mineY, 0);
			Mine mine = world.addEntity(Mine.class, args);

			mineCount--;
			return mine;
		}
		return null;
	}

	private boolean canPlaceMineHere(World world) {
		if (mineReadyTime < System.currentTimeMillis() && mineCount > 0) {
			Terrain terrain = world.getTerrain(tileColumn(), tileRow());
			if (terrain.isValidBuildTarget() && world.getMine(tileColumn(), tileRow()) == null) {
				TerrainImprovement terrainImprovement = world.getTerrainImprovement(tileColumn(), tileRow());
				return (terrainImprovement == null || terrainImprovement.isValidMinePlacementTarget());
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
}
