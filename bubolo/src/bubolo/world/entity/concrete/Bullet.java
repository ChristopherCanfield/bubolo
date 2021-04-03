package bubolo.world.entity.concrete;

import bubolo.audio.Audio;
import bubolo.audio.Sfx;
import bubolo.world.ActorEntity;
import bubolo.world.Collidable;
import bubolo.world.Damageable;
import bubolo.world.Entity;
import bubolo.world.World;

/**
 * Bullets are shot by Tanks and Pillboxes, and can cause damage to StationaryElements and other
 * Actors.
 *
 * @author BU CS673 - Clone Productions
 */
public class Bullet extends ActorEntity
{
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

	// The bullet's movement speed.
	private static final int damage = 10;

	// Specifies whether the bullet is initialized.
	private boolean initialized;

	private static final int width = 4;
	private static final int height = 8;

	/**
	 * Constructs a new Bullet.
	 *
	 * @param args the entity's construction arguments.
	 */
	public Bullet(ConstructionArgs args)
	{
		super(args, width, height);
		updateBounds();

		Audio.play(Sfx.CANNON_FIRED);
	}

	@Override
	protected void onUpdate(World world) {
		if (!initialized) {
			initialize();
		}

		move(world);
	}

	/**
	 * Sets the x and y movement values. Should be called once (but not in the constructor, since
	 * the rotation is not yet set).
	 */
	private void initialize()
	{
		movementX = (float) (Math.cos(rotation()) * speed);
		movementY = (float) (Math.sin(rotation()) * speed);

		initialized = true;
	}

	/**
	 * Moves the bullet. Calls dispose() on this entity if the distance travelled has exceeded the
	 * MAX_DISTANCE value.
	 */
	private void move(World world)
	{
		if (distanceTraveled > maxDistance)
		{
			dispose();
			return;
		}

		setX(x() + movementX);
		setY(y() + movementY);

		distanceTraveled += Math.abs(movementX) + Math.abs(movementY);

		processCollisions(world);
	}

	private void processCollisions(World w)
	{
		for (Collidable collidable : w.getNearbyCollidables(this, false, Damageable.class)) {
			Entity e = (Entity) collidable;
			if (e != owner() && overlapsEntity(collidable)) {
				// We know the collision object is Damageable, because we filtered for that in the getNearbyCollidables method.
				Damageable collisionObject = (Damageable) e;
				collisionObject.takeHit(damage, w);
				dispose();
				break;
			}
		}
	}

	@Override
	public boolean isSolid() {
		return false;
	}
}
