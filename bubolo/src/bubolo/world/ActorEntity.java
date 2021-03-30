package bubolo.world;

import java.util.ArrayList;
import java.util.List;

import bubolo.controllers.Controller;

/**
 * Game objects that may move, and that may have update logic.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public class ActorEntity extends Entity {
	private float x;
	private float y;

	private float rotation;

	private List<Controller> controllers;

	ActorEntity(ConstructionArgs args, int width, int height) {
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
