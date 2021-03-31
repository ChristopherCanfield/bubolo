package bubolo.world.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

import bubolo.controllers.Controller;
import bubolo.graphics.Drawable;
import bubolo.world.World;

/**
 * Base class for game objects. Anything that is part of the game world should inherit from this
 * class.
 *
 * @author BU CS673 - Clone Productions
 */
@Deprecated
public abstract class OldEntity implements Serializable, Drawable
{
	// Used when serializing and deserializing.
	private static final long serialVersionUID = -7558368147503376322L;

	private UUID myID;

	// height of this Entity in pixels
	private int height;
	// width of this Entity in pixels
	private int width;

	// The entity's x position.
	private float xPos;

	// The entity's y position.
	private float yPos;

	// rotation of this Entity in radians.
	private float rotation;

	private Polygon bounds = new Polygon();

	private boolean solid = false;

	// The list of controllers attached to this Entity.
	private List<Controller> controllers;

	// true if this entity should be removed from the game, or false otherwise. This is
	// used by sprites.
	private boolean disposed;

	/**
	 * Construct a new Entity with the specified UUID.
	 *
	 * @param newID
	 *            is the existing UUID to be assigned to the new Entity.
	 */
	public OldEntity(UUID newID)
	{
		updateBounds();
		myID = newID;
	}

	/**
	 * Set the basic parameters for an Entity. Intended to be used after construction.
	 *
	 * @param x
	 *            is the initial x position in world coordinates.
	 * @param y
	 *            is the initial y position in world coordinates.
	 * @param rot
	 *            is the initial rotation in radians.
	 * @return a reference to this Entity.
	 */
	public OldEntity setTransform(float x, float y, float rot)
	{
		setX(x);
		setY(y);
		setRotation(rot);
		updateBounds();
		return this;
	}

	/**
	 * Set the basic parameters for an Entity. Intended to be used after construction.
	 *
	 * @param x
	 *            is the initial x position in world coordinates.
	 * @param y
	 *            is the initial y position in world coordinates.
	 * @param rot
	 *            is the initial rotation in radians.
	 * @return a reference to this Entity.
	 */
	public OldEntity setTransform(float x, float y, double rot) {
		return setTransform(x, y, (float) rot);
	}

	/**
	 * Checks to see whether this Entity's bounding box overlaps that of another Entity,
	 *
	 * @param e
	 *            is the Entity that this Entity should be checked against.
	 * @return true if this Entity overlaps with the Entity specified, false otherwise.
	 */
	public boolean overlapsEntity(OldEntity e)
	{
		updateBounds();
		e.updateBounds();
		return Intersector.overlapConvexPolygons(bounds, e.getBounds());
	}

	/**
	 * The Entity's unique id.
	 *
	 * @return the Entity's unique id.
	 */
	public UUID getId()
	{
		return myID;
	}

	/**
	 * Sets the Entity's unique id.
	 *
	 * @param id
	 *            the Entity's unique id.
	 */
	public void setId(UUID id)
	{
		this.myID = id;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	/**
	 * Updates the state of this Entity. Must be called once per game tick to maintain the Entity's
	 * state.
	 *
	 * @param world
	 *            reference to the World
	 */
	public abstract void update(World world);

	@Override
	public float getRotation()
	{
		return rotation;
	}

	@Override
	public float getX()
	{
		return xPos;
	}

	@Override
	public float getY()
	{
		return yPos;
	}

	/**
	 * Checks whether this Entity should be considered "solid" for the purpose of movement
	 * collisions.
	 *
	 * @return true if this Entity is solid, false otherwise.
	 */
	public boolean isSolid()
	{
		return solid;
	}

	/**
	 * Sets whether this Entity should be considered "solid" for the purpose of movement collision
	 * checks.
	 *
	 * @param solidity
	 *            is true if the Entity should be considered "solid", false otherwise.
	 * @return a reference to this Entity, for chaining.
	 */
	protected OldEntity setSolid(boolean solidity)
	{
		solid = solidity;
		return this;
	}

	/**
	 * Updates the bounding polygon for this Entity with its current position and rotation.
	 */
	public void updateBounds()
	{
		float x = getX();
		float y = getY();
		float w = getWidth();
		float h = getHeight();

		float[] corners = new float[] {
				w / 2f, h / 2f,
				w / 2f, -h / 2f,
				-w / 2f, h / 2f,
				-w / 2f, -h / 2f };
		bounds = new Polygon();
		bounds.setPosition(x, y);
		bounds.rotate((float)Math.toDegrees(getRotation() - Math.PI / 2));
		bounds.setVertices(corners);
	}

	/**
	 * Returns the bounding polygon for this Entity.
	 *
	 * @return the Polygon representing the bounds for this Entity, for intersections/collisions.
	 *
	 */
	public Polygon getBounds()
	{
		return bounds;
	}

	/**
	 * Set the rotation of this Entity.
	 *
	 * @param newRotation
	 *            is the desired rotation state of this Entity in radians.
	 * @return this Entity, after the rotation action has been completed.
	 */
	public OldEntity setRotation(float newRotation)
	{
		float procRotation = newRotation;
		if (newRotation < 0)
		{
			procRotation += 2 * Math.PI;
		}
		rotation = procRotation % (float)(2 * Math.PI);
		updateBounds();
		return this;
	}

	/**
	 * Set the rotation of this Entity.
	 *
	 * @param newRotation
	 *            is the desired rotation state of this Entity in radians.
	 * @return this Entity, after the rotation action has been completed.
	 */
	public OldEntity setRotation(double newRotation)
	{
		return setRotation((float) newRotation);
	}

	/**
	 * Sets this Entity's x position.
	 *
	 * @param x
	 *            is the desired x position in world coordinates.
	 * @return this Entity.
	 */

	public OldEntity setX(float x)
	{
		xPos = x;
		updateBounds();
		return this;
	}

	/**
	 * Set this Entity's y position.
	 *
	 * @param y
	 *            is the desired y position in world coordinates.
	 * @return this Entity.
	 */
	public OldEntity setY(float y)
	{
		yPos = y;
		updateBounds();
		return this;
	}

	/**
	 * Set this Entity's width.
	 *
	 * @param size
	 *            is the desired Entity width in world coordinates.
	 * @return this Entity.
	 */
	public OldEntity setWidth(int size)
	{
		width = size;
		return this;
	}

	/**
	 * Set this Entity's height.
	 *
	 * @param size
	 *            is the desired Entity height in world coordinates.
	 * @return this Entity.
	 */
	public OldEntity setHeight(int size)
	{
		height = size;
		return this;
	}

	/**
	 * Adds a controller to this Entity.
	 *
	 * @param c
	 *            the controller to add.
	 */
	public void addController(Controller c)
	{
		if (controllers == null)
		{
			controllers = new ArrayList<Controller>();
		}
		controllers.add(c);
	}

	/**
	 * Updates all attached controllers.
	 *
	 * @param world
	 *            reference to the World.
	 */
	protected void updateControllers(World world)
	{
		if (controllers != null)
		{
			for (Controller c : controllers)
			{
				c.update(world);
			}
		}
	}

	/**
	 * Returns the number of controllers attached to this Entity.
	 *
	 * @return the number of controllers attached to this Entity.
	 */
	public int getControllerCount()
	{
		return (controllers == null) ? 0 : controllers.size();
	}

	/**
	 * Returns true if the entity should be removed from the game. This is needed by the graphics
	 * system.
	 *
	 * @return true if the entity should be removed from the game.
	 */
	public final boolean isDisposed()
	{
		return disposed;
	}

	/**
	 * This method must be called when the entity should be removed from the game.
	 */
	public final void dispose()
	{
		onDispose();
		disposed = true;
	}

	/**
	 * Called when dispose is called. Entities can override this to perform additional processing
	 * on dispose.
	 */
	protected void onDispose()
	{
	}
}
