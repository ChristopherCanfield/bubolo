package bubolo.world;

import java.util.ArrayList;
import java.util.List;

import bubolo.controllers.Controller;

/**
 * Game objects that may move, and that may have update logic.
 *
 * The primary differences between ActorEntities and StaticEntities are:
 * - Unlike StaticEntities, ActorEntities can be moved after construction.
 * - ActorEntities have a public update method that is called by the world each game tick.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public abstract class ActorEntity extends Entity {
	private float x;
	private float y;

	private float rotation;

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

	protected void setRotation(float radians) {
		this.rotation = radians;
	}

	@Override
	public float x() {
		return x;
	}

	protected void setX(float x) {
		this.x = x;
	}

	@Override
	public float y() {
		return y;
	}

	protected void setY(float y) {
		this.y = y;
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
	 * Called once per tick, after any attached controllers have been updated.
	 *
	 * @param world reference to the game world.
	 */
	abstract protected void onUpdate(World world);

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
			for (Controller c : controllers) {
				c.update(world);
			}
		}
	}
}
