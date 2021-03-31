package bubolo.world.entity.concrete;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

import bubolo.audio.Audio;
import bubolo.audio.Sfx;
import bubolo.util.TileUtil;
import bubolo.world.ActorEntity;
import bubolo.world.BoundingBox;
import bubolo.world.Collidable;
import bubolo.world.Damageable;
import bubolo.world.World;
import bubolo.world.entity.OldEntity;

/**
 * Bullets are shot by Tanks and Pillboxes, and can cause damage to StationaryElements and other
 * Actors.
 *
 * @author BU CS673 - Clone Productions
 */
public class Bullet extends ActorEntity implements Collidable
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

	private ActorEntity parent = null;

	private static final int width = 4;
	private static final int height = 8;

	private final BoundingBox boundingBox = new BoundingBox();

	/**
	 * Constructs a new Bullet.
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

		// TODO (cdc - 2014-03-21): This could be made into a controller. However, it's so
		// simple, what's the point?
		move(world);
	}

	/**
	 * @return the entity that spawned this bullet
	 */
	public ActorEntity getParent() {
		return parent;
	}
	/**
	 * Sets the Parent of this bullet. Bullets never hurt their own parents.
	 *
	 * @param parent the ActorEntity to set as the parent of this bullet.
	 */
	public void setParent(ActorEntity parent) {
		this.parent = parent;

	}

	/**
	 * Moves the bullet. Calls dispose() on this entity if the distance travelled has exceeded the
	 * MAX_DISTANCE value.
	 */
	private void move(World world)
	{
		if (distanceTraveled > maxDistance)
		{
			world.removeEntity(this);
			return;
		}

		setX(x() + movementX);
		setY(y() + movementY);

		distanceTraveled += (Math.abs(movementX) + Math.abs(movementY));

		for(OldEntity collider:getLookaheadEntities(world))
		{
			if (collider instanceof Damageable)
			{
				if (Intersector.overlapConvexPolygons(collider.getBounds(), bounds()))
				{
					Damageable damageableCollider = (Damageable)collider;
					damageableCollider.takeHit(damage);
					world.removeEntity(this);
					return;
				}
			}
		}
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
	 * Returns a list of all Entities that would overlap with this Tank if it was where it
	 * will be in one game tick, along its current trajectory.
	 */
	private List<OldEntity> getLookaheadEntities(World w)
	{
		ArrayList<OldEntity> intersects = new ArrayList<OldEntity>();

		for (OldEntity localEntity: TileUtil.getLocalEntities(getX(),getY(), w))
		{
			if ((localEntity != this && localEntity != parent) && localEntity instanceof Collidable collidable)
			{
				if (overlapsEntity(collidable) || Intersector.overlapConvexPolygons(lookAheadBounds(), collidable.bounds())) {
					intersects.add(localEntity);
				}
			}
		}

		return intersects;
	}

	private Polygon lookAheadBounds()
	{
		Polygon lookAheadBounds = bounds();

		float newX = (float) (x() + Math.cos(rotation()) * speed);
		float newY = (float) (y() + Math.sin(rotation()) * speed);

		lookAheadBounds.setPosition(newX, newY);
		return lookAheadBounds;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public Polygon bounds() {
		return boundingBox.bounds();
	}

	@Override
	public void updateBounds() {
		boundingBox.updateBounds(this);
	}
}
