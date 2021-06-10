package bubolo.graphics.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.graphics.gui.Button.ButtonStatus;
import bubolo.util.Nullable;
import bubolo.util.Units;

/**
 * A vertical grouping of buttons. VButtonGroup objects use screen coordinates (y down; 0 is at top of screen).
 *
 * @author Christopher D. Canfield
 */
public class VButtonGroup {
	private final List<Button> buttons = new ArrayList<>();

	private final Args args;
	private float bottom;

	private int selectedButtonIndex = -1;
	private int hoveredButtonIndex = -1;

	/**
	 * VButtonGroup arguments.
	 */
	public static class Args implements Cloneable {
		public float left;
		// The top position, in screen coordinates (0 is at top).
		public float top;

		public int padding = 20;
		public int paddingBetweenButtons;

		public Color borderColor = Color.BLACK;
		public Color backgroundColor = Color.GREEN;

		public int buttonWidth;
		public int buttonHeight;
		public BitmapFont buttonFont = Fonts.Arial18;
		public Color buttonBorderColor = Color.DARK_GRAY;
		public Color buttonBackgroundColor = Color.WHITE;
		public Color buttonTextColor = Color.BLACK;
		public Color buttonSelectedBorderColor = Color.BLACK;
		public Color buttonSelectedBackgroundColor = Color.DARK_GRAY;
		public Color buttonSelectedTextColor = Color.YELLOW;
		public Color buttonHoverBorderColor = Color.BLACK;
		public Color buttonHoverBackgroundColor = Color.LIGHT_GRAY;
		public Color buttonHoverTextColor = Color.BLACK;

		@Override
		public Args clone() {
			try {
				return (Args) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public VButtonGroup(Args args) {
		assert args.borderColor != null;
		assert args.backgroundColor != null;

		assert args.buttonWidth > 0;
		assert args.buttonHeight > 0;
		assert args.buttonFont != null;

		assert args.buttonBackgroundColor != null;
		assert args.buttonBorderColor != null;
		assert args.buttonTextColor != null;
		assert args.buttonSelectedBackgroundColor != null;
		assert args.buttonSelectedBorderColor != null;
		assert args.buttonSelectedTextColor != null;
		assert args.buttonHoverBackgroundColor != null;
		assert args.buttonHoverBorderColor != null;
		assert args.buttonHoverTextColor != null;

		this.args = args.clone();

		bottom = args.top + args.padding * 2;
	}

	public float right() {
		return args.left + width();
	}

	public float width() {
		return args.padding * 2 + args.buttonWidth;
	}

	public float bottom() {
		return bottom;
	}

	public float height() {
		return args.top - bottom();
	}

	public void addButton(String text) {
		addButton(text, null);
	}

	public void addButton(String text, @Nullable Consumer<Button> action) {
		int buttonTop;
		if (buttons.isEmpty()) {
			buttonTop = (int) args.top + args.padding;
			bottom += args.buttonHeight;
		} else {
			buttonTop = (int) buttons.get(buttons.size() - 1).bottom() + args.paddingBetweenButtons;
			bottom += args.buttonHeight + args.paddingBetweenButtons;
		}
		buttons.add(new Button(args.left + args.padding, buttonTop, args.buttonWidth, args.buttonHeight, args.buttonFont, text, action));
	}

	public void draw(Graphics graphics) {
		var renderer = graphics.shapeRenderer();
		var camera = graphics.uiCamera();

		drawBackground(renderer, camera);
		drawBorder(renderer, camera);

		drawButtonBackgrounds(renderer, camera);
		drawButtonBorders(renderer, camera);
		drawButtonText(graphics.batch(), camera);
	}

	private void drawBackground(ShapeRenderer renderer, Camera camera) {
		renderer.begin(ShapeType.Filled);
		renderer.setColor(args.backgroundColor);
		renderer.rect(args.left, cameraTop(camera), width(), height());
		renderer.end();
	}

	private void drawBorder(ShapeRenderer renderer, Camera camera) {
		renderer.begin(ShapeType.Line);
		renderer.setColor(args.borderColor);
		renderer.rect(args.left, cameraTop(camera), width(), height());
		renderer.end();
	}

	private float cameraTop(Camera camera) {
		return Units.screenYToCameraY(camera, args.top);
	}

	private void drawButtonBackgrounds(ShapeRenderer renderer, Camera camera) {
		renderer.begin(ShapeType.Filled);
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).drawBackground(renderer, camera,
					args.buttonBackgroundColor,
					args.buttonHoverBackgroundColor,
					args.buttonSelectedBackgroundColor,
					ButtonStatus.getButtonStatus(i, selectedButtonIndex, hoveredButtonIndex));
		}
		renderer.end();
	}

	private void drawButtonBorders(ShapeRenderer renderer, Camera camera) {
		renderer.begin(ShapeType.Line);
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).drawBorder(renderer, camera,
					args.buttonBorderColor,
					args.buttonHoverBorderColor,
					args.buttonSelectedBorderColor,
					ButtonStatus.getButtonStatus(i, selectedButtonIndex, hoveredButtonIndex));
		}
		renderer.end();
	}

	private void drawButtonText(Batch batch, Camera camera) {
		batch.begin();
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).drawBatch(batch, camera,
					args.buttonTextColor,
					args.buttonHoverTextColor,
					args.buttonSelectedTextColor,
					ButtonStatus.getButtonStatus(i, selectedButtonIndex, hoveredButtonIndex));
		}
		batch.end();
	}

	public void selectNext() {
		assert !buttons.isEmpty();

		if (selectedButtonIndex == -1) {
			selectedButtonIndex = 0;
		} else {
			selectedButtonIndex = (selectedButtonIndex == (buttons.size() - 1)) ? 0 : selectedButtonIndex + 1;
		}
	}

	public void selectPrevious() {
		assert !buttons.isEmpty();

		if (selectedButtonIndex == -1) {
			selectedButtonIndex = buttons.size() - 1;
		} else {
			selectedButtonIndex = (selectedButtonIndex == 0) ? buttons.size() - 1 : selectedButtonIndex - 1;
		}
	}

	/**
	 * Selects the specified button.
	 *
	 * @param buttonIndex the button index to selected. >= -1 and < button count.
	 */
	public void selectButton(int buttonIndex) {
		assert !buttons.isEmpty();
		assert buttonIndex >= -1 && buttonIndex < buttons.size();

		selectedButtonIndex = buttonIndex;
	}

	/**
	 * @return the index of the selected button, or -1 if no button is selected.
	 */
	public int selectedButtonIndex() {
		return selectedButtonIndex;
	}

	/**
	 * @return the selected button's text, or null if no button is selected.
	 */
	public String selectedButtonText() {
		if (selectedButtonIndex != -1) {
			return buttons.get(selectedButtonIndex).text;
		} else {
			return null;
		}
	}

	/**
	 * Applies (activates) the button's action, if one is attached to it, and returns the selected button's index.
	 *
	 * @return the selected button's index, or -1 if no selected button
	 */
	public int activateSelectedButton() {
		if (selectedButtonIndex != -1) {
			buttons.get(selectedButtonIndex).onAction();
			return selectedButtonIndex;
		} else {
			return -1;
		}
	}

	public int onMouseClicked(int screenX, int screenY) {
		selectedButtonIndex = findButtonThatContainsPoint(screenX, screenY);
		return selectedButtonIndex;
	}

	public int onMouseMoved(int screenX, int screenY) {
		hoveredButtonIndex = findButtonThatContainsPoint(screenX, screenY);
		return hoveredButtonIndex;
	}

	private int findButtonThatContainsPoint(int screenX, int screenY) {
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).contains(screenX, screenY)) {
				return i;
			}
		}
		return -1;
	}
}
