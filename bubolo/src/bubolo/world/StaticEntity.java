package bubolo.world;

import java.util.UUID;

/**
 * Game world objects that never move, and that do not have intelligence.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public class StaticEntity extends Entity {
	private final float x;
	private final float y;

	private final int width;
	private final int height;

	protected StaticEntity(UUID id, float x, float y, int width, int height) {
		super(id);

		this.x = x;
		this.y = y;

		this.width = width;
		this.height = height;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
	}

	@Override
	public float x() {
		return x;
	}

	@Override
	public float y() {
		return y;
	}

	@Override
	public float rotation() {
		return (float) (Math.PI / 2.0);
	}
}
