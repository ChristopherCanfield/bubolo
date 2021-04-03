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

	private final int width;
	private final int height;

	protected Entity(UUID id, int width, int height) {
		assert id != null;
		assert width >= 0;
		assert height >= 0;

		this.id = id;
		this.width = width;
		this.height = height;
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
	public int width() {
		return width;
	}
	/**
	 * @return The object's height in world units.
	 */
	public int height() {
		return height;
	}

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

	/**
	 * @return the world column that the object is in.
	 */
	public int tileColumn() {
		return (int) x() / Coords.TileToWorldScale;
	}

	/**
	 * @return the world row that the object is in.
	 */
	public int tileRow() {
		return (int) y() / Coords.TileToWorldScale;
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
	 * Marks an entity for removal at the end of the current game tick.
	 */
	public final void dispose() {
		if (!disposed) {
			disposed = true;
			onDispose();
		}
	}

	/**
	 * Called when dispose is called.
	 */
	protected void onDispose() {
	}

	/**
	 * Two entity references are equal if the underlying entity points to the same ID.
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof Entity otherEntity) {
			return id.equals(otherEntity.id());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
