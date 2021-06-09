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

public class Button {
	private final BitmapFont font;

	float left;
	// The top position, in screen coordinates (0 is at top).
	float top;

	int width;
	int height;

	String text;

	private Consumer<Button> action = b -> {};

	enum ButtonStatus {
		Unselected,
		Selected,
		Hovered;

		static ButtonStatus getButtonStatus(int buttonIndexToCheck, int selectedButtonIndex, int hoveredButtonIndex) {
			if (buttonIndexToCheck == selectedButtonIndex) {
				return Selected;
			} else if (buttonIndexToCheck == hoveredButtonIndex) {
				return Hovered;
			} else {
				return Unselected;
			}
		}
	}

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

	public String text() {
		return text;
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

	boolean contains(float screenX, float screenY) {
		return left <= screenX && right() >= screenX &&
				top <= screenY && bottom() >= screenY;
	}

	private float cameraTop(Camera camera) {
		return Units.screenYToCameraY(camera, top + height);
	}

	public void drawBorder(ShapeRenderer renderer, Camera camera, Color defaultColor, Color hoveredColor, Color selectedColor, ButtonStatus status) {
		renderer.setColor(getColorForState(defaultColor, hoveredColor, selectedColor, status));
		renderer.rect(left, cameraTop(camera), width, height);
		if (status == ButtonStatus.Selected) {
			renderer.rect(left + 1, cameraTop(camera) + 1, width - 2, height - 2);
		}
	}

	public void drawBackground(ShapeRenderer renderer, Camera camera, Color defaultColor, Color hoveredColor, Color selectedColor, ButtonStatus status) {
		renderer.setColor(getColorForState(defaultColor, hoveredColor, selectedColor, status));
		renderer.rect(left, cameraTop(camera), width, height);
	}

	private static Color getColorForState(Color defaultColor, Color hoveredColor, Color selectedColor, ButtonStatus status) {
		switch (status) {
		case Unselected:
			return defaultColor;
		case Selected:
			return selectedColor;
		case Hovered:
			return hoveredColor;
		default:
			return defaultColor;
		}
	}

	public void drawBatch(Batch batch, Camera camera, Color defaultColor, Color hoveredColor, Color selectedColor, ButtonStatus status) {
		font.setColor(getColorForState(defaultColor, hoveredColor, selectedColor, status));
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
