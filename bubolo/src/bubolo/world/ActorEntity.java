package bubolo.world;

import com.badlogic.gdx.math.Polygon;

import bubolo.controllers.Controller;
import bubolo.util.Nullable;

/**
 * Game objects that may move, and that may have update logic.
 *
 * The primary differences between ActorEntities and StaticEntities are:
 * <ul>
 * 	<li>Unlike StaticEntities, ActorEntities can be moved after construction.</li>
 * 	<li>ActorEntities have a public update method that is called by the world each game tick.</li>
 * 	<li>ActorEntities are always Collidable, though they may not be solid.</li>
 * </ul>
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public abstract class ActorEntity extends Entity implements Collidable {
	private float x;
	private float y;

	private float rotation;

	private ActorEntity owner;

	private final BoundingBox boundingBox;

	private static final Controller nullController = world -> {
	};

	private Controller controller = nullController;

	protected ActorEntity(ConstructionArgs args, int width, int height) {
		super(args.id(), width, height);

		this.x = args.x();
		this.y = args.y();

		this.rotation = args.rotationRadians();

		boundingBox = new BoundingBox(this);
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

	@Override
	public float y() {
		return y;
	}

	/**
	 * Sets the actor's position, and updates its bounds.
	 *
	 * @param x the actor's new x position. >= 0.
	 * @param y the actor's new y position. >= 0.
	 * @return reference to this actor.
	 */
	public ActorEntity setPosition(float x, float y) {
		assert x >= 0 && y >= 0;
		this.x = x;
		this.y = y;
		updateBounds();
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

	/**
	 * Sets the object's owner. May be null.
	 *
	 * @param owner the object's new owner. May be null.
	 */
	public final void setOwner(@Nullable ActorEntity owner) {
		boolean isNewOwner = this.owner != owner;
		this.owner = owner;

		if (isNewOwner) {
			onOwnerChanged(owner);
		}
	}

	/**
	 * Called when the ActorEntity receives a new owner. Derived classes can override this method to be notified of owner changes.
	 *
	 * @param newOwner the actor's new owner. May be null.
	 */
	protected void onOwnerChanged(@Nullable ActorEntity newOwner) {
	}

	/**
	 * @return Whether this object is owned by the local player.
	 */
	public boolean isOwnedByLocalPlayer() {
		return owner == null ? false : owner.isOwnedByLocalPlayer();
	}

	/**
	 * Called once per tick by the game world.
	 *
	 * @param world reference to the game world.
	 */
	public final void update(World world) {
		assert (!isDisposed());

		checkOwner();
		updateControllers(world);
		onUpdate(world);
	}

	/**
	 * Sets the owner to null if it becomes disposed.
	 */
	private void checkOwner() {
		if (owner != null && owner.isDisposed()) {
			setOwner(null);
		}
	}

	/**
	 * Called once per tick, after any attached controllers have been updated. Derived classes can override this to perform
	 * updates, if needed.
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
	 * Attaches a controller to the actor. Most actors can only have a single controller attached.
	 *
	 * @param c the controller to add.
	 */
	public void addController(Controller c) {
		controller = c;
	}

	/**
	 * Updates all attached controllers.
	 *
	 * @param world reference to the World.
	 */
	protected void updateControllers(World world) {
		controller.update(world);
	}
}
