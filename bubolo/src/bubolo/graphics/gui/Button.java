package bubolo.graphics.gui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

import bubolo.graphics.Graphics;
import bubolo.util.Units;

class Button {
	private final BitmapFont font;

	int width;
	int height;
	float left;
	// The top position, in screen coordinates (0 is at top).
	float top;

	String text;

	private boolean selected;

	Button(float left, float top, int width, int height, BitmapFont font, String text) {
		this.width = width;
		this.height = height;
		this.left = left;
		this.top = top;
		this.font = font;
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

	private float cameraTop(Camera camera) {
		return Units.screenYToCameraY(camera, top);
	}

	public void drawShapes(Graphics graphics) {
		ShapeRenderer renderer = graphics.shapeRenderer();
		renderer.rect(cameraTop(graphics.camera()), left, width, height);
	}

	public void drawBatch(Graphics graphics) {
		font.draw(graphics.batch(), text, left, cameraTop(graphics.camera()), 0, text.length(), width, Align.center, false);
	}
}
