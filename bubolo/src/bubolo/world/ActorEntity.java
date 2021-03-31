package bubolo.world;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Polygon;

import bubolo.controllers.Controller;

/**
 * Game objects that may move, and that may have update logic.
 *
 * The primary differences between ActorEntities and StaticEntities are:
 * - Unlike StaticEntities, ActorEntities can be moved after construction.
 * - ActorEntities have a public update method that is called by the world each game tick.
 * - ActorEntities are always Collidable, though they may not be solid.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public abstract class ActorEntity extends Entity implements Collidable {
	private float x;
	private float y;

	private float rotation;

	private ActorEntity owner;
	private boolean ownedByLocalPlayer;

	private BoundingBox boundingBox = new BoundingBox();

	private List<Controller> controllers;

	protected ActorEntity(ConstructionArgs args, int width, int height) {
		super(args.id(), width, height);

		this.x = args.x();
		this.y = args.y();

		this.rotation = args.rotationRadians();
	}

	@Override
	public float rotation() {
		return rotation;
	}

	public ActorEntity setRotation(float radians) {
		this.rotation = radians;
		return this;
	}

	@Override
	public float x() {
		return x;
	}

	public ActorEntity setX(float x) {
		this.x = x;
		return this;
	}

	@Override
	public float y() {
		return y;
	}

	public ActorEntity setY(float y) {
		this.y = y;
		return this;
	}

	/**
	 * @return The object's owner, or null if there isn't an owner.
	 */
	public ActorEntity owner() {
		return owner;
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public final void setOwner(ActorEntity owner) {
		boolean isNewOwner = this.owner != owner;
		this.owner = owner;

		if (isNewOwner) {
			onOwnerChanged(owner);
		}
	}

	/**
	 * Called when the ActorEntity receives a new owner. Derived classes can override this method to be notified
	 * of owner changes.
	 *
	 * @param newOwner the actor's new owner.
	 */
	protected void onOwnerChanged(ActorEntity newOwner) {
	}

	/**
	 * @return Whether this object is owned by the local player.
	 */
	public boolean isOwnedByLocalPlayer() {
		return ownedByLocalPlayer;
	}

	public void setOwnedByLocalPlayer(boolean ownedByLocalPlayer) {
		this.ownedByLocalPlayer = ownedByLocalPlayer;
	}

	/**
	 * Called once per tick by the game world.
	 *
	 * @param world reference to the game world.
	 */
	public final void update(World world) {
		updateControllers(world);
		onUpdate(world);
	}

	/**
	 * Called once per tick, after any attached controllers have been updated. Derived classes can override
	 * this to perform updates, if needed.
	 *
	 * @param world reference to the game world.
	 */
	protected void onUpdate(World world) {
	}

	@Override
	public Polygon bounds() {
		return boundingBox.bounds();
	}

	@Override
	public void updateBounds() {
		boundingBox.updateBounds(this);
	}

	/**
	 * Adds a controller to this actor.
	 *
	 * @param c the controller to add.
	 */
	public void addController(Controller c)
	{
		if (controllers == null) {
			controllers = new ArrayList<Controller>();
		}
		controllers.add(c);
	}

	/**
	 * Updates all attached controllers.
	 *
	 * @param world reference to the World.
	 */
	protected void updateControllers(World world)
	{
		if (controllers != null) {
			controllers.forEach(c -> c.update(world));
		}
	}
}
