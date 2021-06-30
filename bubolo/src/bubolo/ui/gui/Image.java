package bubolo.ui.gui;

import com.badlogic.gdx.graphics.Texture;

import bubolo.graphics.Graphics;
import bubolo.util.Units;

public class Image extends UiComponent {
	private int width;
	private int height;
	private Texture texture;

	public Image(LayoutArgs layoutArgs, Texture texture, int width, int height) {
		super(layoutArgs);

		this.texture = texture;
		this.width = width;
		this.height = height;

		recalculateLayout(parentWidth, parentHeight);
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
		recalculateLayout(parentWidth, parentHeight);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void draw(Graphics graphics) {
		if (texture != null) {
			var batch = graphics.nonScalingBatch();
			batch.begin();
			var screenBottom = Units.screenYToCameraY(graphics.uiCamera(), bottom() - padding);
			batch.draw(texture, left(), screenBottom, width, height);
			batch.end();
		}
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
