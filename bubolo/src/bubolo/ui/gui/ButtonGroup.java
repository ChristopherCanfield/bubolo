package bubolo.ui.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
 * A group of buttons. The buttons can be laid out vertically (default) or horizontally.
 * ButtonGroup objects use screen coordinates (y down; 0 is at top of screen).
 *
 * @author Christopher D. Canfield
 */
public class ButtonGroup extends UiComponent {
	private final List<Button> buttons = new ArrayList<>();

	private final Args args;
	private float height;
	private float width;

	private int selectedButtonIndex = NoIndex;
	private int hoveredButtonIndex = NoIndex;

	public enum Layout {
		Vertical,
		Horizontal
	}

	/**
	 * VButtonGroup arguments.
	 */
	public static class Args implements Cloneable {
		public static final Color Transparent = new Color(0, 0, 0, 0);

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

		public Layout buttonListLayout = Layout.Vertical;

		public int paddingBetweenButtons;

		public Color borderColor = Color.BLACK;
		public Color backgroundColor = Transparent;

		public int buttonWidth;
		public int buttonHeight;
		public BitmapFont buttonFont = Fonts.UiGeneralTextFont;
		public Color buttonBorderColor = Color.DARK_GRAY;
		public Color buttonBackgroundColor = Color.WHITE;
		public Color buttonTextColor = Color.BLACK;
		public Color buttonSelectedBorderColor = Color.BLACK;
		public Color buttonSelectedBackgroundColor = Color.DARK_GRAY;
		public Color buttonSelectedTextColor = Color.YELLOW;
		public Color buttonHoverBorderColor = Color.BLACK;
		public Color buttonHoverBackgroundColor = Color.LIGHT_GRAY;
		public Color buttonHoverTextColor = Color.BLACK;

		public Args(int buttonWidth, int buttonHeight) {
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

	public ButtonGroup(LayoutArgs layoutArgs, ButtonGroup.Args args) {
		super(layoutArgs);

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

		height = padding * 2;
		width = padding * 2;
	}

	@Override
	public float right() {
		return left + width();
	}

	@Override
	public float width() {
//		return padding * 2 + args.buttonWidth;
		return width;
	}

	@Override
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
		if (args.buttonListLayout == Layout.Vertical) {
			addButtonVertical(text, action);
		} else {
			addButtonHorizontal(text, action);
		}

		recalculateLayout(parentWidth, parentHeight);
	}

	private void addButtonVertical(String text, @Nullable Consumer<Button> action) {
		int buttonTop;
		if (buttons.isEmpty()) {
			buttonTop = (int) top + padding;
			height += args.buttonHeight;
		} else {
			buttonTop = (int) buttons.get(buttons.size() - 1).bottom() + args.paddingBetweenButtons;
			height += args.buttonHeight + args.paddingBetweenButtons;
		}
		width = padding * 2 + args.buttonWidth;
		buttons.add(new Button(left + padding, buttonTop, args.buttonWidth, args.buttonHeight, args.buttonFont, text, action));
	}

	private void addButtonHorizontal(String text, @Nullable Consumer<Button> action) {
		int buttonLeft;
		if (buttons.isEmpty()) {
			buttonLeft = (int) top + padding;
			width += args.buttonWidth;
		} else {
			buttonLeft = (int) buttons.get(buttons.size() - 1).right() + args.paddingBetweenButtons;
			width += args.buttonWidth + args.paddingBetweenButtons;
		}
		height = padding * 2 + args.buttonHeight;
		buttons.add(new Button(buttonLeft, top + padding, args.buttonWidth, args.buttonHeight, args.buttonFont, text, action));
	}

	@Override
	public void draw(Graphics graphics) {
		var renderer = graphics.nonScalingShapeRenderer();
		var camera = graphics.uiCamera();

		drawBackground(renderer, camera);
		drawBorder(renderer, camera);

		drawButtonBackgrounds(renderer, camera);
		drawButtonBorders(renderer, camera);
		drawButtonText(graphics.nonScalingBatch(), camera);
	}

	private float cameraY(Camera camera) {
		return Units.screenYToCameraY(camera, top + height);
	}

	private void drawBackground(ShapeRenderer renderer, Camera camera) {
		renderer.begin(ShapeType.Filled);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		renderer.setColor(args.backgroundColor);
		renderer.rect(left, cameraY(camera), width(), height());
		renderer.end();
	}

	private void drawBorder(ShapeRenderer renderer, Camera camera) {
		renderer.begin(ShapeType.Line);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		renderer.setColor(args.borderColor);
		renderer.rect(left, cameraY(camera), width(), height());
		renderer.end();
	}

	private void drawButtonBackgrounds(ShapeRenderer renderer, Camera camera) {
		renderer.begin(ShapeType.Filled);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
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
		Gdx.gl20.glEnable(GL20.GL_BLEND);
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
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).drawText(batch, camera,
					args.buttonTextColor,
					args.buttonHoverTextColor,
					args.buttonSelectedTextColor,
					ButtonStatus.getButtonStatus(i, selectedButtonIndex, hoveredButtonIndex));
		}
		batch.end();
	}

	private void recalculateButtonPositions() {
		if (args.buttonListLayout == Layout.Vertical) {
			recalculateButtonPositionsVertical();
		} else {
			recalculateButtonPositionsHorizontal();
		}
	}

	private void recalculateButtonPositionsVertical() {
		for (int i = 0; i < buttons.size(); i++) {
			var button = buttons.get(i);
			button.top = (int) top + padding + (i * args.buttonHeight) + (i * args.paddingBetweenButtons);
			button.left = (int) left + padding;
		}
	}

	private void recalculateButtonPositionsHorizontal() {
		for (int i = 0; i < buttons.size(); i++) {
			var button = buttons.get(i);
			button.top = (int) top + padding;
			button.left = (int) left + padding + (i * args.buttonWidth) + (i * args.paddingBetweenButtons);
		}
	}

	@Override
	protected void onRecalculateLayout() {
		recalculateButtonPositions();
	}

	public void selectNext() {
		assert !buttons.isEmpty();

		if (selectedButtonIndex == NoIndex) {
			selectedButtonIndex = 0;
		} else {
			selectedButtonIndex = (selectedButtonIndex == (buttons.size() - 1)) ? 0 : selectedButtonIndex + 1;
		}
	}

	public void selectPrevious() {
		assert !buttons.isEmpty();

		if (selectedButtonIndex == NoIndex) {
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
		assert buttonIndex >= NoIndex && buttonIndex < buttons.size();

		selectedButtonIndex = buttonIndex;
	}

	/**
	 * @return the index of the selected button, or UiComponent.NoIndex (-1) if no button is selected.
	 */
	public int selectedButtonIndex() {
		return selectedButtonIndex;
	}

	/**
	 * @return the selected button's text, or null if no button is selected.
	 */
	public String selectedButtonText() {
		if (selectedButtonIndex != NoIndex) {
			return buttons.get(selectedButtonIndex).text;
		} else {
			return null;
		}
	}

	/**
	 * Applies (activates) the button's action, if one is attached to it, and returns the selected button's index.
	 *
	 * @return the selected button's index, or UiComponent.NoIndex (-1) if no selected button
	 */
	public int activateSelectedButton() {
		if (selectedButtonIndex != NoIndex) {
			buttons.get(selectedButtonIndex).onAction();
			return selectedButtonIndex;
		} else {
			return NoIndex;
		}
	}

	@Override
	public int onMouseClicked(int screenX, int screenY) {
		selectedButtonIndex = findButtonThatContainsPoint(screenX, screenY);
		activateSelectedButton();
		return selectedButtonIndex;
	}

	@Override
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
		return NoIndex;
	}
}
