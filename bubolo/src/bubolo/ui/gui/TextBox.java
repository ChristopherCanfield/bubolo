package bubolo.ui.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;

import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.util.Nullable;
import bubolo.util.Timer;
import bubolo.util.Units;

public class TextBox extends PositionableUiComponent implements Focusable {
	private final Args args;

	private String text = "";
	private boolean hasFocus;

	private final Timer<Void> timer = new Timer<>(2);
	private final float cursorBlinkTimeSeconds = 0.5f;
	private final int cursorBlinkTimerId;
	private boolean cursorVisible = true;

	private GlyphLayout layout;

	private final String filteredCharacters = "\t\r\n";

	public static class Args implements Cloneable {
		public BitmapFont font = Fonts.UiGeneralTextFont;
		public Color textColor = Color.BLACK;
		/** The text box's width. Does not include the label's width. */
		public int textWidth;

		/** May be null. */
		public @Nullable String labelText;
		public int labelWidth;

		public Color backgroundColor = new Color(0.9f, 0.9f, 0.9f, 1);
		public Color borderColor = Color.GRAY;
		public Color selectedBorderColor = Color.BLACK;

		void validate() {
			assert font != null;
			assert textColor != null;
			assert textWidth > 0;
			assert labelWidth >= 0;
			assert borderColor != null;
			assert selectedBorderColor != null;
		}

		@Override
		public Args clone() {
			try {
				return (Args) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	public TextBox(LayoutArgs layoutArgs, Args args) {
		super(layoutArgs);

		args.validate();
		this.args = args.clone();

		cursorBlinkTimerId = timer.scheduleSeconds(cursorBlinkTimeSeconds, this::onCursorBlinkTimerExpired);

		this.layout = new GlyphLayout(args.font, text, 0, text.length(), args.textColor, args.textWidth, Align.left, false, null);
		recalculateLayout(parentWidth, parentHeight);
	}

	/**
	 * Controls cursor blinking.
	 *
	 * @param unused not used. Required because the Timer calls a Consumer<T>.
	 */
	private void onCursorBlinkTimerExpired(Void unused) {
		cursorVisible = !cursorVisible;
		timer.rescheduleSeconds(cursorBlinkTimerId, cursorBlinkTimeSeconds);
	}

	public void setText(String text) {
		this.text = text;
		recalculateLayout(parentWidth, parentHeight);
	}

	public String text() {
		return text;
	}

	/**
	 * @return true if the text box contains no characters.
	 */
	public boolean isEmpty() {
		return text.isEmpty();
	}

	/**
	 * @return true if the text box contains no characters, or only whitespace characters.
	 */
	public boolean isBlank() {
		return text.isBlank();
	}

	@Override
	public void onKeyTyped(char character) {
		if (hasFocus) {
			// Don't capture filtered characters.
			if (filteredCharacters.contains(String.valueOf(character))) {

			// Handle backspace.
			} else if (character == '\b') {
				if (!text.isEmpty()) {
					this.text = text.substring(0, text.length() - 1);
				}
			} else {
				this.text += character;
			}
		}
	}

	@Override
	public void onKeyDown(int keycode) {
		if (hasFocus && keycode == Keys.V) {
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)) {
				String clipboardText = Gdx.app.getClipboard().getContents();
				if (clipboardText.length() > 0) {
					this.text += clipboardText;
				}
			}
		}
	}

	@Override
	public ClickedObjectInfo onMouseClicked(int screenX, int screenY) {
		if (contains(screenX, screenY)) {
			gainFocus();
			return new ClickedObjectInfo(this, 0);
		}
		return null;
	}

	private boolean contains(float screenX, float screenY) {
		return left <= screenX && right() >= screenX &&
				top <= screenY && bottom() >= screenY;
	}

	@Override
	public void draw(Graphics graphics) {
		timer.update(null);
		var screenTop = Units.screenYToCameraY(graphics.uiCamera(), top + padding);

		drawBackground(graphics, screenTop);
		drawBorder(graphics, screenTop);

		drawText(graphics, screenTop);
		drawLabelText(graphics, screenTop);
	}

	private void drawBackground(Graphics graphics, float screenTop) {
		var shapeRenderer = graphics.nonScalingShapeRenderer();
		shapeRenderer.setColor(args.backgroundColor);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.rect(boxLeft(), screenTop - height() - 3, args.textWidth + 4, height() + 5);
		shapeRenderer.end();
	}

	private void drawBorder(Graphics graphics, float screenTop) {
		var shapeRenderer = graphics.nonScalingShapeRenderer();
		if (hasFocus) {
			shapeRenderer.setColor(args.selectedBorderColor);
		} else {
			shapeRenderer.setColor(args.borderColor);
		}
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.rect(boxLeft(), screenTop - height() - 4 , args.textWidth + 4, height() + 6);
		shapeRenderer.end();
	}

	private void drawText(Graphics graphics, float screenTop) {
		var textWithCursor = (cursorVisible && hasFocus) ? text + "|" : text;

		var batch = graphics.nonScalingBatch();
		batch.begin();
		args.font.setColor(args.textColor);
		this.layout = args.font.draw(batch, textWithCursor, boxLeft() + 2, screenTop - 2, 0, textWithCursor.length(), args.textWidth, Align.left, false, "");
		batch.end();
	}

	private void drawLabelText(Graphics graphics, float screenTop) {
		if (args.labelText != null) {
			var batch = graphics.nonScalingBatch();
			batch.begin();
			args.font.setColor(args.textColor);
			GlyphLayout layout = args.font.draw(batch, args.labelText, left() + padding, screenTop - 1, 0, args.labelText.length(), args.labelWidth, Align.left, false, null);
			if (layout.height > this.layout.height) {
				this.layout = layout;
			}
			batch.end();
		}
	}

	private float boxLeft() {
		return left() + padding + args.labelWidth;
	}

	@Override
	public float width() {
		return args.textWidth + args.labelWidth + padding * 2;
	}

	@Override
	public float height() {
		return layout.height * 1.3f + padding * 2;
	}

	@Override
	protected void onRecalculateLayout() {
		if (args.labelText != null) {
			this.layout = new GlyphLayout(args.font, args.labelText, 0, args.labelText.length(), args.textColor, args.labelWidth, Align.left, false, null);
		} else {
			this.layout = new GlyphLayout(args.font, text, 0, text.length(), args.textColor, args.textWidth, Align.left, false, null);
		}
	}

	@Override
	public void gainFocus() {
		hasFocus = true;
	}

	@Override
	public void lostFocus() {
		hasFocus = false;
	}
}
