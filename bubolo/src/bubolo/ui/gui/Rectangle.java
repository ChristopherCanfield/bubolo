package bubolo.ui.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import bubolo.graphics.Graphics;
import bubolo.util.Units;

public class Rectangle extends PositionableUiComponent {
	private final Color backgroundColor;
	private final Color borderColor;
	private final int width;
	private final int height;

	public Rectangle(LayoutArgs layoutArgs, Color backgroundColor, Color borderColor, int width, int height) {
		super(layoutArgs);

		assert backgroundColor != null;
		assert borderColor != null;
		assert width > 0;
		assert height > 0;

		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
		this.width = width;
		this.height = height;
	}

	@Override
	public void draw(Graphics graphics) {
		var batch = graphics.nonScalingShapeRenderer();
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		var cameraY = Units.screenYToCameraY(graphics.uiCamera(), top + padding + height);

		// Draw background.
		batch.setColor(backgroundColor);
		batch.begin(ShapeType.Filled);
		batch.rect(left() + padding, cameraY, width, height);
		batch.end();

		// Draw border.
		batch.setColor(borderColor);
		batch.begin(ShapeType.Line);
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
