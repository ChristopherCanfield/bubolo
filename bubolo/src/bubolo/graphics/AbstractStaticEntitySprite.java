package bubolo.graphics;

import bubolo.world.Entity;
import bubolo.world.EntityRemovedObserver;
import bubolo.world.StaticEntity;

/**
 * Abstract base class for sprites that render simple static entities. This parent sprite provides only positional and lifetime
 * information. More complex sprites should inherit from AbstractEntitySprite.
 *
 * @author Christopher D. Canfield
 */
abstract class AbstractStaticEntitySprite extends Sprite implements EntityRemovedObserver {
	private boolean entityDisposed;

	/* The width, height, x, and y maximum sizes are enforced by StaticEntities. Their values are set in
		Entity.EntityMaxSize, Config.MaxWorldX and Config.MaxWorldY. */

	private byte width;
	private byte height;

	private short x;
	private short y;

	private float rotation;

	/**
	 * @param layer the layer that the sprite is drawn to.
	 * @param entity reference to the Entity that this sprite represents.
	 */
	protected AbstractStaticEntitySprite(DrawLayer layer, StaticEntity entity) {
		this(layer, entity, entity.rotation());
	}

	protected AbstractStaticEntitySprite(DrawLayer layer, StaticEntity entity, float rotation) {
		super(layer);

		entity.addEntityRemovedObserver(this);

		this.x = (short) entity.x();
		this.y = (short) entity.y();
		this.width = (byte) entity.width();
		this.height = (byte) entity.height();
		this.rotation = rotation;
	}

	/**
	 * Returns true if the underlying entity is destroyed, or false otherwise.
	 *
	 * @return true if the underlying entity is destroyed, or false otherwise.
	 */
	@Override
	protected boolean isDisposed() {
		return entityDisposed;
	}

	@Override
	public float getX() {
		// Make unsigned.
		return x & 0xffff;
	}

	@Override
	public float getY() {
		// Make unsigned.
		return y & 0xffff;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public final void onEntityRemoved(Entity entity) {
		entityDisposed = true;
	}
}
