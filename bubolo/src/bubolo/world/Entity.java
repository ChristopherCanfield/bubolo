package bubolo.world;

import java.util.UUID;

import bubolo.util.Coords;

/**
 * Base class for objects that live in the game world.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public abstract class Entity {

	public static record ConstructionArgs(UUID id, float x, float y, float rotationRadians) {}

	private final UUID id;
	private boolean disposed;

	protected Entity(UUID id) {
		assert id != null;
		this.id = id;
	}

	/**
	 * @return The object's unique ID.
	 */
	public final UUID id() {
		return id;
	}

	/**
	 * @return The object's width in world units.
	 */
	public abstract int width();
	/**
	 * @return The object's height in world units.
	 */
	public abstract int height();

	/**
	 * @return The object's x position in world units.
	 */
	public abstract float x();
	/**
	 * @return The object's y position in world units.
	 */
	public abstract float y();

	/**
	 * @return The object's rotation in radians.
	 */
	public abstract float rotation();

	public int gridX() {
		return (int) x() / Coords.TILE_TO_WORLD_SCALE;
	}

	public int gridY() {
		return (int) y() / Coords.TILE_TO_WORLD_SCALE;
	}

	/**
	 * Returns true if the entity should be removed from the game.
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
	public final void dispose() {
		disposed = true;
		onDispose();
	}

	/**
	 * Called when dispose is called.
	 */
	protected void onDispose()
	{
	}
}