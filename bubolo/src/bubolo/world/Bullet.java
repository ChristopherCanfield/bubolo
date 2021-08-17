package bubolo.world;

import bubolo.Systems;
import bubolo.audio.Sfx;

/**
 * Bullets are shot by Tanks and Pillboxes, and can cause damage to StationaryElements and other Actors.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Bullet extends ActorEntity {
	// The max distance the bullet can travel, in world units.
	private static final float maxDistance = 600;

	// The distance the bullet has traveled.
	private int distanceTraveled;

	// The x movement per tick.
	private float movementX;

	// The y movement per tick.
	private float movementY;

	// The bullet's movement speed, in game world units per tick.
	private static final float speed = 6.f;

	// The amount of damage the bullet causes.
	private static final float damage = 10;

	static float Damage() {
		return damage;
	}

	// Specifies whether the bullet is initialized.
	private boolean initialized;

	public interface BulletHitObjectObserver {
		/**
		 * Called when the bullet hits an object. Will not be called if the bullet reaches the end of its range.
		 */
		void onBulletHitObject();
	}

	private BulletHitObjectObserver observer;

	public static final int Width = 4;
	public static final int Height = 8;

	/**
	 * Constructs a new Bullet.
	 *
	 * @param args the entity's construction arguments.
	 * @param world reference to the game world.
	 */
	protected Bullet(ConstructionArgs args, World world) {
		super(args, Width, Height);
		updateBounds();

		Systems.audio().play(Sfx.CannonFired, args.x(), args.y());
	}

	@Override
	protected void onUpdate(World world) {
		if (!initialized) {
			initialize();
		}

		move(world);
	}

	/**
	 * Sets the x and y movement values. Should be called once (but not in the constructor, since the rotation is not yet set).
	 */
	private void initialize() {
		movementX = (float) (Math.cos(rotation()) * speed);
		movementY = (float) (Math.sin(rotation()) * speed);

		initialized = true;
	}

	public void setBulletHitObjectObserver(BulletHitObjectObserver observer) {
		assert this.observer == null : "Only one BulletHitObjectObserver can be assigned to a bullet.";
		this.observer = observer;
	}

	/**
	 * Moves the bullet. Calls dispose() on this entity if the distance travelled has exceeded the MAX_DISTANCE value.
	 */
	private void move(World world) {
		float newX = x() + movementX;
		float newY = y() + movementY;

		if (distanceTraveled > maxDistance || !world.containsPoint(newX, newY)) {
			dispose();
			return;
		}

		setPosition(x() + movementX, y() + movementY);
		distanceTraveled += Math.abs(movementX) + Math.abs(movementY);

		processCollisions(world);
	}

	private void processCollisions(World world) {
		for (Collidable collidable : world.getCollidablesWithinTileDistance(this, 1, false, Damageable.class)) {
			Entity e = (Entity) collidable;
			if (e != owner() && overlapsEntity(collidable) && !isBaseAlliedOrBroken(e)) {
				// We know the collision object is Damageable, because we filtered for that in the getNearbyCollidables method.
				Damageable collisionObject = (Damageable) e;
				collisionObject.receiveDamage(world, damage, owner());
				observer.onBulletHitObject();
				dispose();
				break;
			}
		}
	}

	/**
	 * Used to allow bullets to pass over allied or broken bases.
	 */
	private static boolean isBaseAlliedOrBroken(Entity e) {
		if (e instanceof Base base) {
			return base.owner().isAlliedWithLocalPlayer() || base.hitPoints() <= 0;
		}
		return false;
	}

	@Override
	public boolean isSolid() {
		return false;
	}
}
