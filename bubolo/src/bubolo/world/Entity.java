package bubolo.world;

import java.util.UUID;

import bubolo.util.Coords;

/**
 * Base class for objects that live in the game world. Entities are created using the World.addEntity method:
 * <p>
 * {@code world.addEntity(EntityType.class, args);} </code>
 * <p>
 * All entities must have a two-argument constructor, which takes an Entity.ConstructionArgs object and a reference to the game
 * world:
 * <p>
 * {@code EntityType(Entity.ConstructionArgs args, World world)}
 * </p>
 * The constructor does not need to be public.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public abstract class Entity {
	/** The max size that an entity's height and width can each be. */
	public static final int EntityMaxSize = Byte.MAX_VALUE;

	/**
	 * Generates and returns the next unique Entity ID.
	 *
	 * @return the next unique Entity ID.
	 */
	public static UUID nextId() {
		return UUID.randomUUID();
	}

	public static record ConstructionArgs(UUID id, float x, float y, float rotationRadians) {
	}

	private final UUID id;
	private boolean disposed;

	private final byte width;
	private final byte height;

	/**
	 * @param id the entity's unique ID. Use UUID.randomUUID() if the entity does not already have an ID.
	 * @param width the entity's width. > 0 && <= Entity.MaxSize.
	 * @param height the entity's height. > 0 && <= Entity.MaxSize.
	 */
	protected Entity(UUID id, int width, int height) {
		assert id != null;

		assert width > 0;
		assert width <= Entity.EntityMaxSize;
		assert height > 0;
		assert height <= Entity.EntityMaxSize;

		this.id = id;
		this.width = (byte) width;
		this.height = (byte) height;
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
	 * @return The object's left x position in world units.
	 */
	public abstract float x();

	/**
	 * @return The object's bottom y position in world units.
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
		return Coords.worldUnitToTile(x());
	}

	/**
	 * @return the world row that the object is in.
	 */
	public int tileRow() {
		return Coords.worldUnitToTile(y());
	}

	/**
	 * Returns true if the entity should be removed from the game.
	 *
	 * @return true if the entity should be removed from the game.
	 */
	public final boolean isDisposed() {
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

	@Override
	public String toString() {
		return String.format("%s {id=%s | position=%f,%f | tile=%d,%d | width=%d | height=%d | isDisposed=%b ",
				getClass().getName(), id().toString(), x(), y(), tileColumn(), tileRow(), width(), height(), isDisposed());
	}
}
