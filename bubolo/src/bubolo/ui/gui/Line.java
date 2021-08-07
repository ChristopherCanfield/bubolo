package bubolo.ui.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import bubolo.graphics.Graphics;
import bubolo.util.Units;

public class Line extends PositionableUiComponent {
	private final Color color;
	private final int width;
	private final int height;

	public Line(LayoutArgs layoutArgs, Color color, int width, int height) {
		super(layoutArgs);

		assert color != null;
		assert width > 0;
		assert height > 0;

		this.color = color;
		this.width = width;
		this.height = height;
	}

	@Override
	public void draw(Graphics graphics) {
		var batch = graphics.nonScalingShapeRenderer();
		batch.setColor(color);
		batch.begin(ShapeType.Filled);

		var cameraY = Units.screenYToCameraY(graphics.uiCamera(), top + padding);
		batch.rect(left() + padding, cameraY, width, height);

		batch.end();
	}

	@Override
	public float width() {
		return width + padding * 2;
	}

	@Override
	public float height() {
		return height + padding * 2;
	}

	@Override
	protected void onRecalculateLayout() {
	}
}
