package bubolo.world;

import com.badlogic.gdx.math.Polygon;

/**
 * Contains bounds updating functionality, and an underlying Polygon for collision detection.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public class BoundingBox {
	private Polygon bounds = new Polygon(new float[8]);

	public BoundingBox(Entity entity) {
		this(entity, entity.width(), entity.height());
	}

	public BoundingBox(Entity entity, int width, int height) {
		updateBounds(entity, width, height);
	}

	public void updateBounds(Entity entity) {
		updateBounds(entity, entity.height(), entity.width());
	}

	public void updateBounds(Entity entity, int width, int height) {
		float x = entity.x();
		float y = entity.y();
		float w = width;
		float h = height;

		float[] corners = bounds.getVertices();
		corners[0] = w / 2f;
		corners[1] = h / 2f;

		corners[2] = w / 2f;
		corners[3] = -h / 2f;

		corners[4] = -w / 2f;
		corners[5] = h / 2f;

		corners[6] = w / 2f;
		corners[7] = -h / 2f;

		bounds.setPosition(x, y);
		bounds.dirty();
	}

	public Polygon bounds() {
		return bounds;
	}
}
