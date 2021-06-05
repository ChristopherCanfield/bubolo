package bubolo.graphics.gui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;

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
		int buttonTop;
		if (buttons.isEmpty()) {
			buttonTop = (int) args.top + args.padding;
		} else {
			buttonTop = (int) buttons.get(buttons.size() - 1).bottom() + args.paddingBetweenButtons;
		}

		buttons.add(new Button(args.left + args.padding, buttonTop, args.buttonWidth, args.buttonHeight, args.buttonFont, text));
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

	public boolean onMouseClicked(int screenX, int screenY) {
		buttons.forEach(b -> b.setSelected(false));

		Button clickedButton = findButtonThatContainsPoint(screenX, screenY);
		if (clickedButton != null) {
			clickedButton.setSelected(true);
			return true;
		}
		return false;
	}

	public boolean onMouseMoved(int screenX, int screenY) {
		buttons.forEach(b -> b.setHovered(false));

		Button hoverButton = findButtonThatContainsPoint(screenX, screenY);
		if (hoverButton != null) {
			hoverButton.setHovered(true);
			return true;
		}
		return false;
	}

	private Button findButtonThatContainsPoint(int screenX, int screenY) {
		for (Button button : buttons) {
			if (button.contains(screenX, screenY)) {
				return button;
			}
		}
		return null;
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
