package bubolo.world;

import com.badlogic.gdx.math.Polygon;

public class BoundingBox {
	private Polygon bounds;

	public void updateBounds(Entity entity) {
		updateBounds(entity, entity.height(), entity.width());
	}

	public void updateBounds(Entity entity, int width, int height) {
		float x = entity.x();
		float y = entity.y();
		float w = width;
		float h = height;

		float[] corners = new float[] {
				w / 2f, h / 2f,
				w / 2f, -h / 2f,
				-w / 2f, h / 2f,
				-w / 2f, -h / 2f };
		bounds = new Polygon();
		bounds.setPosition(x, y);
		bounds.rotate((float) Math.toDegrees(entity.rotation() - Math.PI / 2));
		bounds.setVertices(corners);
	}

	public Polygon bounds() {
		return bounds;
	}
}
