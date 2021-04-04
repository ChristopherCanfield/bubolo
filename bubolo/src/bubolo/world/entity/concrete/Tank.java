package bubolo.world.entity.concrete;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

import bubolo.audio.Audio;
import bubolo.audio.Sfx;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.MoveTank;
import bubolo.net.command.NetTankSpeed;
import bubolo.util.Coords;
import bubolo.world.ActorEntity;
import bubolo.world.Collidable;
import bubolo.world.Damageable;
import bubolo.world.Entity;
import bubolo.world.Terrain;
import bubolo.world.TerrainImprovement;
import bubolo.world.World;

/**
 * The tank, which may be controlled by a local player, a networked player, or an AI bot.
 *
 * @author BU CS673 - Clone Productions
 */
public class Tank extends ActorEntity implements Damageable
{
	private String playerName;

	// Max speed in world units per tick.
	private static final float maxSpeed = 4.f;

	/**
	 * Used to calculate the maxSpeed based upon the interaction with the intersected
	 * terrains
	 */
	private float modifiedMaxSpeed = maxSpeed;

	// The tank's current speed.
	private float speed = 0.f;

	// The rate of acceleration, in pixels per tick.
	private static final float accelerationRate = 0.01f;

	// The rate of deceleration, in pixels per tick.
	private static final float decelerationRate = 0.035f;

	// Specifies whether the tank accelerated this tick.
	private boolean accelerated;

	// Specifies whether the tank decelerated this tick.
	private boolean decelerated;

	// Specifies whether the tank is hidden in trees
	private boolean hidden;

	// The tank's rate of rotation per tick.
	private static final float rotationRate = 0.03f;

	// The reload speed of the tank's cannon, in milliseconds.
	private static final long cannonReloadSpeed = 500;

	// The time that the tank will respawn.
	private long respawnTime;

	private static final long TANK_RESPAWN_TIME_MILLIS = 1000L;

	// Minimum amount of time between laying mines.
	private static final long MINE_RELOAD_SPEED_MILLIS = 500;

	// The last time that the cannon was fired. Populate this with
	// System.currentTimeMillis().
	private long cannonFireTime = 0;

	// The next time a mine will be ready to be laid.
	private long mineAvailableTime = 0;

	private Polygon leftBumper = new Polygon();
	private Polygon rightBumper = new Polygon();
	private float bumperWidth = 4.0f;
	private float bumperHeight = 4.0f;

	/**
	 * The default amount to rotate the Tank by when a bumper collision is detected. Used
	 * to prevent getting 'stuck' on walls.
	 */
	private static final float rotationOffsetAmount = (float) Math.toRadians(1);

	/**
	 * The default amount to reposition the Tank by when a bumper collision is detected.
	 * Used to prevent getting 'stuck' on walls.
	 */
	private static final float positionOffsetAmount = 0.1f;

	private float hitPoints;


	public static final int TANK_MAX_HIT_POINTS = 100;

	private int ammoCount;

	public static final int TANK_MAX_AMMO = 100;


	private int mineCount;

	public static final int TANK_MAX_MINE = 10;


	private static final Random randomGenerator = new Random();

	private static final int width = 20;
	private static final int height = 20;

	/**
	 * Constructs a Tank.
	 *
	 * @param args the entity's construction arguments.
	 */
	public Tank(ConstructionArgs args)
	{
		super(args, width, height);

		updateBounds();
		hitPoints = TANK_MAX_HIT_POINTS;
		ammoCount = TANK_MAX_AMMO;
		mineCount = TANK_MAX_MINE;
	}

	@Override
	public void onUpdate(World world)
	{
		if (!isAlive()) {
			respawn(world);
		}

		updateSpeedForTerrain(world);
		moveTank(world);
		checkTrees(world);
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String name) {
		this.playerName = name;
	}

	/**
	 * Returns the tank's speed.
	 *
	 * @return the tank's speed.
	 */
	public float speed()
	{
		return speed;
	}

	/**
	 * Sets the tank's speed. For use with the network system.
	 *
	 * @param netTankSpeed a NetTankSpeed object that contains the tank's new speed.
	 */
	public void setSpeed(NetTankSpeed netTankSpeed)
	{
		if (netTankSpeed.getSpeed() > speed) {
			accelerated = true;
		}

		this.speed = netTankSpeed.getSpeed();
	}

	/**
	 * Accelerates the tank.
	 */
	public void accelerate()
	{
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
	public void decelerate()
	{
		if (speed > 0 && !decelerated) {
			speed -= decelerationRate;
			decelerated = true;
		}
		clampSpeed();
	}

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
	public void rotateRight()
	{
		setRotation(rotation() + rotationRate);
	}

	/**
	 * Rotates the tank counter-clockwise.
	 */
	public void rotateLeft()
	{
		setRotation(rotation() - rotationRate);
	}

	/**
	 * Returns true if the cannon is ready to fire.
	 *
	 * @return true if the cannon is ready to fire.
	 */
	public boolean isCannonReady()
	{
		return (System.currentTimeMillis() - cannonFireTime > cannonReloadSpeed);
	}

	/**
	 * Fires the tank's cannon, which adds a bullet to the world and initiates a cannon
	 * reload.
	 *
	 * @param world
	 *            reference to the world.
	 * @param startX
	 *            the bullet's start x position.
	 * @param startY
	 *            the bullet's start y position.
	 *
	 * @return bullet reference to the new bullet or null if the tank cannot fire.
	 */
	public Bullet fireCannon(World world, float startX, float startY)
	{
		if ((ammoCount > 0) && (cannonFireTime - System.currentTimeMillis() < 0))
		{
			cannonFireTime = System.currentTimeMillis();

			var args = new Entity.ConstructionArgs(UUID.randomUUID(), startX, startY, rotation());
			Bullet bullet = world.addEntity(Bullet.class, args);
			bullet.setOwner(this);

			ammoCount--;

			return bullet;
		}

		else
		{
			return null;
		}
	}

	private Polygon lookAheadBounds()
	{
		Polygon lookAheadBounds = bounds();

		float newX = (float) (x() + Math.cos(rotation()) * speed);
		float newY = (float) (y() + Math.sin(rotation()) * speed);

		lookAheadBounds.setPosition(newX, newY);
		return lookAheadBounds;
	}

	/**
	 * @return true if the Tank is hidden, false otherwise.
	 */
	public boolean isHidden() {
		return hidden || !isAlive();
	}

	/**
	 * Returns a list of all solid Entities that will overlap with this Tank if continues on its current path.
	 */
	private List<Collidable> getLookaheadEntities(World world)
	{
		var nextTickBounds = lookAheadBounds();
		var nextTickOverlappedEntities = new ArrayList<Collidable>();

		var collidables = world.getNearbyCollidables(this, true);
		for (Collidable collidable : collidables) {
			assert collidable.isSolid() : collidable.toString();
			if (overlapsEntity(collidable) || Intersector.overlapConvexPolygons(nextTickBounds, collidable.bounds())) {
				nextTickOverlappedEntities.add(collidable);
			}
		}

		return nextTickOverlappedEntities;
	}

	/**
	 * Update the left and right bumpers to use current positioning and speed information.
	 */
	private void updateBumpers()
	{
		updateLeftBumper();
		updateRightBumper();
	}

	/**
	 * Updates the bounding polygon for this Entity with its current position and
	 * rotation.
	 */
	private void updateLeftBumper()
	{
		float newX = (float) (x() + Math.cos(rotation()) * speed);
		float newY = (float) (y() + Math.sin(rotation()) * speed);
		float w = width();
		float h = height();

		// Defines the corners of the left bumper as a 4x4 pixel box, placed at the
		// top-left edge of the tank, with its left edge along the left edge of the
		// tank and its topmost edge aligned with the front edge of the tank.
		float[] corners = new float[] { -w / 2f, h / 2f, -w / 2f + 4, h / 2f, -w / 2f, h / 2f - 4,
				-w / 2f + 4, h / 2f - 4 };
		leftBumper = new Polygon();
		leftBumper.setPosition(newX, newY);
		leftBumper.setOrigin(0, 0);
		leftBumper.setVertices(corners);
		leftBumper.rotate((float) Math.toDegrees(rotation() - Math.PI / 2));
	}

	/**
	 * Updates the bounding polygon for this Entity with its current position and
	 * rotation.
	 */
	private void updateRightBumper()
	{
		float newX = (float) (x() + Math.cos(rotation()) * speed);
		float newY = (float) (y() + Math.sin(rotation()) * speed);
		float w = width();
		float h = height();

		// Defines the corners of the right bumper as a 4x4 pixel box, placed at the
		// top-right edge of the tank, with its left edge along the left edge of the
		// tank and its topmost edge aligned with the front edge of the tank.
		float[] corners = new float[] { w / 2f, h / 2f, w / 2f - bumperWidth, h / 2f, w / 2f,
				h / 2f - bumperHeight, w / 2f - bumperWidth, h / 2f - bumperHeight };
		rightBumper = new Polygon();
		rightBumper.setPosition(newX, newY);
		rightBumper.setOrigin(0, 0);
		rightBumper.setVertices(corners);
		rightBumper.rotate((float) Math.toDegrees(rotation() - Math.PI / 2));
	}

	/**
	 * Checks to see if an Entity overlaps with this Tank's left bumper.
	 */
	private boolean hitLeftBumper(Collidable e)
	{
		return Intersector.overlapConvexPolygons(e.bounds(), leftBumper);
	}

	/**
	 * Checks to see if an Entity overlaps with this Tank's right bumper.
	 */
	private boolean hitRightBumper(Collidable e)
	{
		return Intersector.overlapConvexPolygons(e.bounds(), rightBumper);
	}

	/**
	 * Checks to see if this Tank is facing to the Northeast (for bumper collisions)
	 */
	private boolean facingNE()
	{
		return (rotation() >= 0 && rotation() < (Math.PI / 2));
	}

	/**
	 * Checks to see if this Tank is facing to the Northwest (for bumper collisions)
	 */
	private boolean facingNW()
	{
		return (rotation() >= (Math.PI / 2) && rotation() < Math.PI);
	}

	/**
	 * Checks to see if this Tank is facing to the Southwest (for bumper collisions)
	 */
	private boolean facingSW()
	{
		return (rotation() >= Math.PI && rotation() < (3 * Math.PI) / 2);
	}

	/**
	 * Checks to see if this Tank is facing to the Southeast (for bumper collisions)
	 */
	private boolean facingSE()
	{
		return (rotation() >= (3 * Math.PI) / 2 && rotation() < (2 * Math.PI));
	}

	private void checkTrees(World world)
	{
		TerrainImprovement terrainImprovement = world.getTerrainImprovement(tileColumn(), tileRow());
		hidden = terrainImprovement instanceof Tree;

		// TODO (cdc - 2021-04-02): Is this needed?
//		boolean[] corners = TileUtil.getCornerMatches(closeTile, world, new Class[] { Tree.class });
//		boolean[] edges = TileUtil.getEdgeMatches(closeTile, world, new Class[] { Tree.class });
//		for (int i = 0; i < 4; i++)
//		{
//			if (corners[i] == false || edges[i] == false)
//			{
//				hidden = false;
//				return;
//			}
//		}
//
//		hidden = true;
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
	 * Updates the Tank's world position according to its speed, acceleration/deceleration
	 * state, and collision information.
	 *
	 * @param world reference to the game world.
	 */
	private void moveTank(World world)
	{
		float xPos = x();
		float yPos = y();
		float rotation = rotation();

		/*
		 * The position where the Tank will be after one game tick, if it continues its
		 * current trajectory and speed.
		 */
		float newX = (float) (xPos + Math.cos(rotation) * speed);
		float newY = (float) (yPos + Math.sin(rotation) * speed);

		// Prevent the tank from exiting the game world.
		if (world.containsPoint(newX, newY)) {
			return;
		}

		// Update (replace) the right and left bumper polygons to make sure collisions are accurate.
		updateBumpers();

		// Record which, if any, bumpers were hit.
		boolean collidingLeft = false;
		boolean collidingRight = false;

		for (var collider : getLookaheadEntities(world)) {
			if (!collidingLeft) {
				collidingLeft = hitLeftBumper(collider);
			}
			if (!collidingRight) {
				collidingRight = hitRightBumper(collider);
			}

			// If colliders were found on the left and right, there is no need to continue checking.
			if (collidingLeft && collidingRight) {
				break;
			}
		}

		/*
		 * Floats used the offset that should be applied to the Tank to record wall
		 * collisions.
		 */
		float rotationOffset = 0f;
		float xOffset = 0;
		float yOffset = 0;

		/*
		 * If the Tank hit something with its left bumper, restrict travel in the
		 * appropriate direction, and offset/rotate the Tank to 'slide' away from the
		 * collision.
		 */
		if (collidingLeft) {
			rotationOffset -= rotationOffsetAmount;
			if (facingNE())
			{
				if (newY > yPos)
				{
					newY = yPos;
					yOffset -= positionOffsetAmount;
				}
			}
			else if (facingNW())
			{
				if (newX < xPos)
				{
					newX = xPos;
					xOffset += positionOffsetAmount;
				}
			}
			else if (facingSW())
			{
				if (newY < yPos)
				{
					newY = yPos;
					yOffset += positionOffsetAmount;
				}
			}
			else if (facingSE())
			{
				if (newX > xPos)
				{
					newX = xPos;
					xOffset -= positionOffsetAmount;
				}
			}
		}

		/*
		 * If the Tank hit something with its right bumper, restrict travel in the
		 * appropriate direction, and offset/rotate the Tank to 'slide' away from the
		 * collision.
		 */
		if (collidingRight) {
			rotationOffset += rotationOffsetAmount;
			if (facingNE())
			{
				if (newX > xPos)
				{
					newX = xPos;
					xOffset -= positionOffsetAmount;
				}
			}
			else if (facingNW())
			{
				if (newY > yPos)
				{
					newY = yPos;
					yOffset -= positionOffsetAmount;
				}
			}
			else if (facingSW())
			{
				if (newX < xPos)
				{
					newX = xPos;
					xOffset += positionOffsetAmount;
				}
			}
			else if (facingSE())
			{
				if (newY < yPos)
				{
					newY = yPos;
					yOffset += positionOffsetAmount;
				}
			}
		}

		/*
		 * If the speed of the Tank is greater than zero, modify its position and rotation
		 * by the offsets given earlier. Note that if a Tank collides on the left and
		 * right bumpers simultaneously, the rotational offsets will cancel each other
		 * out.
		 */
		if (speed > 0)
		{
			setX(newX + xOffset);
			setY(newY + yOffset);
			setRotation(rotation + rotationOffset);

			if (!accelerated)
			{
				decelerate();
			}
		}

		accelerated = false;
		decelerated = false;
	}

	/**
	 * Returns the current health of the tank
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
	 *
	 * @return - Max Hit points for the entity
	 */
	@Override
	public int maxHitPoints()
	{
		return TANK_MAX_HIT_POINTS;
	}

	/**
	 * Returns the current ammo count of the tank
	 *
	 * @return current ammo count
	 */
	public int getAmmoCount()
	{
		return ammoCount;
	}

	/**
	 * Returns the number of mines the tank currently contains
	 *
	 * @return the current mine count
	 */
	public int getMineCount()
	{
		return mineCount;
	}

	/**
	 * Changes the hit point count after taking damage
	 *
	 * @param damagePoints
	 *            how much damage the tank has taken
	 */
	@Override
	public void receiveDamage(float damagePoints, World world)
	{
		assert(damagePoints >= 0);

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
	private void onDeath()
	{
		Audio.play(Sfx.TANK_EXPLOSION);
		respawnTime = System.currentTimeMillis() + TANK_RESPAWN_TIME_MILLIS;
	}

	/**
	 * Increments the tanks health by a given amount
	 *
	 * @param healPoints
	 *            - how many points the tank is given
	 */
	@Override
	public void heal(float healPoints)
	{
		if (hitPoints + Math.abs(healPoints) < TANK_MAX_HIT_POINTS)
		{
			hitPoints += Math.abs(healPoints);
		}
		else
		{
			hitPoints = TANK_MAX_HIT_POINTS;
		}
	}

	/**
	 * Supplies the tank ammo with given a set amount
	 *
	 * @param newAmmo
	 *            - amount of ammo being transfered to the tank
	 */
	public void gatherAmmo(int newAmmo)
	{
		assert newAmmo >= 0;
		ammoCount += newAmmo;
		if (ammoCount > TANK_MAX_AMMO) {
			ammoCount = TANK_MAX_AMMO;
		}
	}

	/**
	 * This method supplies the tank with mines
	 *
	 * @param minesGathered
	 *            - the number of mines to supply the tank with
	 */
	public void gatherMine(int minesGathered)
	{
		assert minesGathered >= 0;
		mineCount += minesGathered;
		if (mineCount > TANK_MAX_MINE) {
			mineCount = TANK_MAX_MINE;
		}
	}

	/**
	 * Creates the mine in world and passes it back to the caller. Returns null if a mine can't be created.
	 *
	 * @param world reference to the game world.
	 * @return The mine, or null if a mine is unable to be placed.
	 */
	public Mine dropMine(World world)
	{
		if (mineAvailableTime < System.currentTimeMillis() && mineCount > 0) {

			Terrain terrain = world.getTerrain(tileColumn(), tileRow());
			if (!terrain.isBuildable()) {
				mineAvailableTime = System.currentTimeMillis() + MINE_RELOAD_SPEED_MILLIS;

				int mineX = tileColumn() * Coords.TileToWorldScale;
				int mineY = tileRow() * Coords.TileToWorldScale;

				var args = new Entity.ConstructionArgs(UUID.randomUUID(), mineX, mineY, 0);
				Mine mine = world.addEntity(Mine.class, args);

				mineCount--;
				return mine;
			}
		}
		return null;
	}

	private void respawn(World world)
	{
		// Don't allow the tank to respawn until its respawn timer has expired.
		if (respawnTime > System.currentTimeMillis()) {
			return;
		}

		var spawns = world.getSpawns();
		if (spawns.size() > 0) {
			Spawn spawn = spawns.get(randomGenerator.nextInt(spawns.size()));
			setX(spawn.x());
			setY(spawn.y());

			Network net = NetworkSystem.getInstance();
			net.send(new MoveTank(this));
		}

		hitPoints = TANK_MAX_HIT_POINTS;
		ammoCount = TANK_MAX_AMMO;
		mineCount = TANK_MAX_MINE;
	}

	/**
	 * Maximum amount of ammo for tank
	 * @return maximum ammo count of tank
	 */
	public int getTankMaxAmmo()
	{
		return TANK_MAX_AMMO;
	}

	/**
	 * Maximum amount of mines for tank
	 * @return maximum amount of mines a tank can carry
	 */
	public int getTankMaxMineCount()
	{
		return TANK_MAX_MINE;
	}

	@Override
	public boolean isSolid() {
		return true;
	}
}
