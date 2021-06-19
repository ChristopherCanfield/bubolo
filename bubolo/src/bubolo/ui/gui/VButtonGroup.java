package bubolo.ui.gui;

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
import bubolo.ui.gui.Button.ButtonStatus;
import bubolo.util.Nullable;
import bubolo.util.Units;

/**
 * A vertical grouping of buttons. VButtonGroup objects use screen coordinates (y down; 0 is at top of screen).
 *
 * @author Christopher D. Canfield
 */
public class VButtonGroup extends UiComponent {
	private final List<Button> buttons = new ArrayList<>();

	private final Args args;
	private float left;
	private float top;
	private float height;

	private int selectedButtonIndex = -1;
	private int hoveredButtonIndex = -1;

	/**
	 * VButtonGroup arguments.
	 */
	public static class Args implements Cloneable {
		public int startLeft;
		/** The starting top position, in screen coordinates (y-down). */
		public int startTop;
		int parentWidth;
		int parentHeight;

		/**
		 * The left offset, which is either from 0 (if centeredHorizontally is false) or the
		 * viewport's horizontal center - width()/2.
		 */
//		public float leftOffset;
		/** The top offset, which is either from 0 (if centeredVertically is false) or the
		 * viewport's vertical center - width()/2.
		 */
//		public float topOffset;

		/** If true, the object is centered horizontally, and is then offset by {@code -width()/2 + left}. */
//		public boolean centeredHorizontally = false;
		/** If true, the object is centered vertically, and is then offset by the {@code -width()/2 + top}. */
//		public boolean centeredVertically = false;

		/** The padding between the button edges and the VButtonGroup borders. */
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

		public Args(int parentWidth, int parentHeight, int buttonWidth, int buttonHeight) {
			this.parentWidth = parentWidth;
			this.parentHeight = parentHeight;
			this.buttonWidth = buttonWidth;
			this.buttonHeight = buttonHeight;
		}

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
		assert args.parentWidth > 0;
		assert args.parentHeight > 0;

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

		this.left = horizontalPosition(args.startLeft, args.parentWidth);
		this.top = verticalPosition(args.startTop, args.parentWidth);

		height = args.padding * 2;
	}

	public float right() {
		return left + width();
	}

	@Override
	public float width() {
		return args.padding * 2 + args.buttonWidth;
	}

	public float bottom() {
		return top + height();
	}

	@Override
	public float height() {
		return height;
	}

	public void addButton(String text) {
		addButton(text, null);
	}

	public void addButton(String text, @Nullable Consumer<Button> action) {
		int buttonTop;
		if (buttons.isEmpty()) {
			buttonTop = (int) top + args.padding;
			height += args.buttonHeight;
		} else {
			buttonTop = (int) buttons.get(buttons.size() - 1).bottom() + args.paddingBetweenButtons;
			height += args.buttonHeight + args.paddingBetweenButtons;
		}
		buttons.add(new Button(left + args.padding, buttonTop, args.buttonWidth, args.buttonHeight, args.buttonFont, text, action));

		recalculateLayout(args.startLeft, args.startTop, args.parentWidth, args.parentHeight);
	}

	public void draw(Graphics graphics) {
		var renderer = graphics.nonScalingShapeRenderer();
		var camera = graphics.uiCamera();

		drawBackground(renderer, camera);
		drawBorder(renderer, camera);

		drawButtonBackgrounds(renderer, camera);
		drawButtonBorders(renderer, camera);
		drawButtonText(graphics.nonScalingBatch(), camera);
	}

	private void drawBackground(ShapeRenderer renderer, Camera camera) {
		renderer.begin(ShapeType.Filled);
		renderer.setColor(args.backgroundColor);
		renderer.rect(left, cameraTop(camera), width(), height());
		renderer.end();
	}

	private void drawBorder(ShapeRenderer renderer, Camera camera) {
		renderer.begin(ShapeType.Line);
		renderer.setColor(args.borderColor);
		renderer.rect(left, cameraTop(camera), width(), height());
		renderer.end();
	}

	private float cameraTop(Camera camera) {
		return Units.screenYToCameraY(camera, top + height);
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

	private void recalculateButtonPositions() {
		for (int i = 0; i < buttons.size(); i++) {
			var button = buttons.get(i);
			button.top = (int) top + args.padding + (i * args.buttonHeight) + (i * args.paddingBetweenButtons);
			button.left = (int) left + args.padding;
		}
	}

	@Override
	public void recalculateLayout(int left, int top, int parentWidth, int parentHeight) {
		args.parentWidth = parentWidth;
		args.parentHeight = parentHeight;
		args.startLeft = left;
		args.startTop = top;

		this.left = horizontalPosition(left, parentWidth);
		this.top = verticalPosition(top, parentHeight);
		recalculateButtonPositions();
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
