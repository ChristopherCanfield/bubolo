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
		public Color buttonBorderColor = Color.BLACK;
		public Color buttonBackgroundColor = Color.BLUE;
		public Color buttonTextColor = Color.WHITE;

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
		assert args.buttonBorderColor != null;
		assert args.buttonBackgroundColor != null;
		assert args.buttonTextColor != null;

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

		renderer.setColor(args.buttonBackgroundColor);
		renderer.begin(ShapeType.Filled);
		for (Button button : buttons) {
			button.drawBackground(renderer, graphics.camera());
		}
		renderer.end();

		renderer.setColor(args.buttonBorderColor);
		renderer.begin(ShapeType.Line);
		for (Button button : buttons) {
			button.drawBorder(renderer, graphics.camera());
		}
		renderer.end();

		graphics.batch().setColor(args.buttonTextColor);
		graphics.batch().begin();
		for (Button button : buttons) {
			button.drawBatch(graphics);
		}
		graphics.batch().end();
	}
}
