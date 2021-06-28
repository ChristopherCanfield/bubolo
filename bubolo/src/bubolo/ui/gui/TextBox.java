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
import bubolo.util.Units;

public class TextBox extends UiComponent {
	private final Args args;

	private String text = "";
	private boolean isSelected;

	private GlyphLayout layout;

	public static class Args implements Cloneable {
		public BitmapFont font = Fonts.Arial18;
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

		this.layout = new GlyphLayout(args.font, text, 0, text.length(), args.textColor, args.textWidth, Align.left, false, null);
		recalculateLayout(startLeft, startTop, parentWidth, parentHeight);
	}

	public void setText(String text) {
		this.text = text;
		recalculateLayout(startLeft, startTop, parentWidth, parentHeight);
	}

	public void setSelected(boolean selected) {
		System.out.println("Selected: " + selected);
		this.isSelected = selected;
	}

	public void onKeyTyped(char character) {
		if (isSelected) {
			System.out.println(character);
			if (character == '') {
				if (!text.isEmpty()) {
					this.text = text.substring(0, text.length() - 1);
				}
			} else {
				this.text += character;
			}
		}
	}

	public void onKeyDown(int keycode) {
		if (isSelected && keycode == Keys.V) {
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)) {
				String clipboardText = Gdx.app.getClipboard().getContents();
				if (clipboardText.length() > 0) {
					this.text += clipboardText;
				}
			}
		}

	}

	public boolean onMouseClicked(int screenX, int screenY) {
		if (contains(screenX, screenY)) {
			setSelected(true);
		}
		return true;
	}

	private boolean contains(float screenX, float screenY) {
		return left <= screenX && right() >= screenX &&
				top <= screenY && bottom() >= screenY;
	}

	@Override
	public void draw(Graphics graphics) {
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
		shapeRenderer.rect(boxLeft(), screenTop - height(), width(), height());
		shapeRenderer.end();
	}

	private void drawBorder(Graphics graphics, float screenTop) {
		var shapeRenderer = graphics.nonScalingShapeRenderer();
		if (isSelected) {
			shapeRenderer.setColor(args.selectedBorderColor);
		} else {
			shapeRenderer.setColor(args.borderColor);
		}
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.rect(boxLeft(), screenTop - 1 - height(), width(), height() + 2);
		shapeRenderer.end();
	}

	private void drawText(Graphics graphics, float screenTop) {
		var batch = graphics.nonScalingBatch();
		batch.begin();
		args.font.setColor(args.textColor);
		this.layout = args.font.draw(batch, text, boxLeft(), screenTop - 1, 0, text.length(), args.textWidth, Align.left, false, null);
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
}
