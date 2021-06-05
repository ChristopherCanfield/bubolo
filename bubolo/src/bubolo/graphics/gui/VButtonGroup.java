package bubolo.graphics.gui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import bubolo.graphics.Graphics;

/**
 * A vertical grouping of buttons. VButtonGroup objects use screen coordinates (y down; 0 is at top of screen).
 *
 * @author Christopher D. Canfield
 */
public class VButtonGroup {
	private final List<Button> buttons = new ArrayList<>();

	private final float left;
	// The top position, in screen coordinates (0 is at top).
	private final float top;

	private int padding;
	private int paddingBetweenButtons;

	private final int buttonWidth;
	private final int buttonHeight;
	private final BitmapFont buttonFont;

	public VButtonGroup(float left, float top, int buttonWidth, int buttonHeight, BitmapFont buttonFont) {
		this.left = left;
		this.top = top;
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		this.buttonFont = buttonFont;
	}

	public void addButton(String text) {
		int buttonTop;
		if (buttons.isEmpty()) {
			buttonTop = (int) top + padding;
		} else {
			buttonTop = (int) buttons.get(buttons.size() - 1).bottom() + paddingBetweenButtons;
		}

		buttons.add(new Button(left + padding, buttonTop, buttonWidth, buttonHeight, buttonFont, text));
	}

	public void draw(Graphics graphics) {
		for (Button button : buttons) {
			button.draw(graphics);
		}
	}
}
