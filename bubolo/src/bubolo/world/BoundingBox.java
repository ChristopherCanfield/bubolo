package bubolo.world;

import com.badlogic.gdx.math.Polygon;

public class BoundingBox {
	private Polygon bounds;

	public void updateBounds(Entity entity)
	{
		float x = entity.x();
		float y = entity.y();
		float w = entity.width();
		float h = entity.height();

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
