package bubolo.graphics.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.util.Nullable;

/**
 * A vertical grouping of buttons. VButtonGroup objects use screen coordinates (y down; 0 is at top of screen).
 *
 * @author Christopher D. Canfield
 */
public class VButtonGroup {
	private final List<Button> buttons = new ArrayList<>();

	private final Args args;

	/**
	 * VButtonGroup arguments.
	 */
	public static class Args implements Cloneable {
		public float left;
		// The top position, in screen coordinates (0 is at top).
		public float top;

		public int padding;
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
	}

	public float right() {
		return args.left + args.padding * 2 + args.buttonWidth;
	}

	public void addButton(String text) {
		addButton(text, null);
	}

	public void addButton(String text, @Nullable Consumer<Button> action) {
		int buttonTop;
		if (buttons.isEmpty()) {
			buttonTop = (int) args.top + args.padding;
		} else {
			buttonTop = (int) buttons.get(buttons.size() - 1).bottom() + args.paddingBetweenButtons;
		}

		buttons.add(new Button(args.left + args.padding, buttonTop, args.buttonWidth, args.buttonHeight, args.buttonFont, text, action));
	}

	public void draw(Graphics graphics) {
		var renderer = graphics.shapeRenderer();

		renderer.begin(ShapeType.Filled);
		for (Button button : buttons) {
			button.drawBackground(renderer, graphics.camera(), args.buttonBackgroundColor, args.buttonHoverBackgroundColor, args.buttonSelectedBackgroundColor);
		}
		renderer.end();

		renderer.begin(ShapeType.Line);
		for (Button button : buttons) {
			button.drawBorder(renderer, graphics.camera(), args.buttonBorderColor, args.buttonHoverBorderColor, args.buttonSelectedBorderColor);
		}
		renderer.end();

		graphics.batch().begin();
		for (Button button : buttons) {
			button.drawBatch(graphics.batch(), graphics.camera(), args.buttonTextColor, args.buttonHoverTextColor, args.buttonSelectedTextColor);
		}
		graphics.batch().end();
	}

	public void selectNext() {
		assert !buttons.isEmpty();

		int index = findSelectedButtonIndex();
		if (index == -1) {
			buttons.get(0).setSelected(true);
		} else {
			int newSelectedIndex = (index == (buttons.size() - 1)) ? 0 : index + 1;
			buttons.get(index).setSelected(false);
			buttons.get(newSelectedIndex).setSelected(true);
		}
	}

	public void selectPrevious() {
		assert !buttons.isEmpty();

		int index = findSelectedButtonIndex();
		if (index == -1) {
			buttons.get(buttons.size() - 1).setSelected(true);
		} else {
			int newSelectedIndex = (index == 0) ? buttons.size() - 1 : index - 1;
			buttons.get(index).setSelected(false);
			buttons.get(newSelectedIndex).setSelected(true);
		}
	}

	public void selectButton(int buttonIndex) {
		assert !buttons.isEmpty();

		buttons.forEach(b -> b.setSelected(false));
		buttons.forEach(b -> b.setHovered(false));
		if (buttonIndex != -1) {
			buttons.get(buttonIndex).setSelected(true);
		}
	}

	/**
	 * @return the index of the selected button, or -1 if no button is selected.
	 */
	public int selectedButtonIndex() {
		return findSelectedButtonIndex();
	}

	/**
	 * @return the selected button's text, or null if no button is selected.
	 */
	public String selectedButtonText() {
		int selectedButtonIndex = findSelectedButtonIndex();
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
		int selectedButtonIndex = findSelectedButtonIndex();
		if (selectedButtonIndex != -1) {
			buttons.get(selectedButtonIndex).onAction();
			return selectedButtonIndex;
		} else {
			return -1;
		}
	}

	public int onMouseClicked(int screenX, int screenY) {
		buttons.forEach(b -> b.setSelected(false));

		int clickedButtonIndex = findButtonThatContainsPoint(screenX, screenY);
		if (clickedButtonIndex != -1) {
			buttons.get(clickedButtonIndex).setSelected(true);
		}
		return clickedButtonIndex;
	}

	public int onMouseMoved(int screenX, int screenY) {
		buttons.forEach(b -> b.setHovered(false));

		int hoverButtonIndex = findButtonThatContainsPoint(screenX, screenY);
		if (hoverButtonIndex != -1) {
			buttons.get(hoverButtonIndex).setHovered(true);
		}
		return hoverButtonIndex;
	}

	private int findButtonThatContainsPoint(int screenX, int screenY) {
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).contains(screenX, screenY)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @return the index of the selected button, or -1 if no button is selected.
	 */
	private int findSelectedButtonIndex() {
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).isSelected()) {
				return i;
			}
		}
		return -1;
	}
}
