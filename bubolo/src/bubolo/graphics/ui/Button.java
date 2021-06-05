package bubolo.graphics.ui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import bubolo.graphics.Graphics;
import bubolo.util.Units;

class Button {
	int width;
	int height;
	float left;
	// The top position, in screen coordinates (0 is at top).
	float top;

	String text;

	private boolean selected;

	Button(float left, float top, int width, int height, String text) {
		this.width = width;
		this.height = height;
		this.left = left;
		this.top = top;
		this.text = text;
	}

	float right() {
		return left + width;
	}

	float bottom() {
		return top - height;
	}

	void setSelected(boolean selected) {
		this.selected = selected;
	}

	boolean isSelected() {
		return selected;
	}

	boolean contains(float screenX, float screenY) {
		return left <= screenX && right() >= screenX &&
				top <= screenY && bottom() >= screenY;
	}

	public void draw(Graphics graphics) {
		ShapeRenderer renderer = graphics.shapeRenderer();
		float cameraTop = Units.screenYToCameraY(graphics.camera(), top);
		renderer.rect(cameraTop, left, width, height);
	}
}
