package bubolo.graphics.gui;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

import bubolo.util.Nullable;
import bubolo.util.Units;

class Button {
	private final BitmapFont font;

	float left;
	// The top position, in screen coordinates (0 is at top).
	float top;

	int width;
	int height;

	String text;

	private Consumer<Button> action = b -> {};

	private boolean selected;
	private boolean hovered;

	Button(float left, float top, int width, int height, BitmapFont font, String text, @Nullable Consumer<Button> action) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.font = font;
		this.text = text;
		if (action != null) {
			this.action = action;
		}
	}

	float right() {
		return left + width;
	}

	/**
	 * @return the bottom, in screen coordinates (y down).
	 */
	float bottom() {
		return top + height;
	}

	void setSelected(boolean selected) {
		this.selected = selected;
	}

	boolean isSelected() {
		return selected;
	}

	void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

	boolean isHovered() {
		return hovered;
	}

	boolean contains(float screenX, float screenY) {
		return left <= screenX && right() >= screenX &&
				top <= screenY && bottom() >= screenY;
	}

	private float cameraTop(Camera camera) {
		return Units.screenYToCameraY(camera, top + height);
	}

	public void drawBorder(ShapeRenderer renderer, Camera camera, Color defaultColor, Color hoveredColor, Color selectedColor) {
		setShapeRendererColor(renderer, defaultColor, hoveredColor, selectedColor);
		renderer.rect(left, cameraTop(camera), width, height);
		if (selected) {
			renderer.rect(left + 1, cameraTop(camera) + 1, width - 2, height - 2);
		}
	}

	public void drawBackground(ShapeRenderer renderer, Camera camera, Color defaultColor, Color hoveredColor, Color selectedColor) {
		setShapeRendererColor(renderer, defaultColor, hoveredColor, selectedColor);
		renderer.rect(left, cameraTop(camera), width, height);
	}

	private void setShapeRendererColor(ShapeRenderer renderer, Color defaultColor, Color hoveredColor, Color selectedColor) {
		if (selected) {
			renderer.setColor(selectedColor);
		} else if (hovered) {
			renderer.setColor(hoveredColor);
		} else {
			renderer.setColor(defaultColor);
		}
	}

	public void drawBatch(Batch batch, Camera camera, Color defaultColor, Color hoveredColor, Color selectedColor) {
		if (selected) {
			font.setColor(selectedColor);
		} else if (hovered) {
			font.setColor(hoveredColor);
		} else {
			font.setColor(defaultColor);
		}
		font.draw(batch, text, left, cameraTop(camera) + (font.getCapHeight() + height) / 2, 0, text.length(), width, Align.center, false);
	}

	protected void onAction() {
		action.accept(this);
	}

	@Override
	public String toString() {
		return String.format("%s{left=%f,top=%f,right=%f,bottom=%f,width=%d,height=%d,text=%s}",
				getClass().getName(),
				left,
				top,
				right(),
				bottom(),
				width,
				height,
				text);
	}
}
