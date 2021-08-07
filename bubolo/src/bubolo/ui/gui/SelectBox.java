package bubolo.ui.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

/**
 * Allows the user to select from a list of text items.
 *
 * @author Christopher D. Canfield
 */
public class SelectBox extends PositionableUiComponent implements Focusable {
	private final Args args;

	private boolean hasFocus;

	private GlyphLayout layout;
	private GlyphLayout leftIconLayout;
	private GlyphLayout rightIconLayout;

	private boolean highlightLeftArrow;
	private boolean highlightRightArrow;

	private record Item(String text, @Nullable Consumer<SelectBox> action) {
	}

	private int itemIndex;
	private final List<Item> items = new ArrayList<>();

	public static class Args implements Cloneable {
		public BitmapFont font = Fonts.UiGeneralTextFont;
		public Color textColor = Color.BLACK;
		/** The select box's width. Does not include the label's width. */
		public int textWidth;

		public Color arrowColor = new Color(0.4f, 0.4f, 0.4f, 1);
		public Color arrowHoverColor = Color.BLACK;

		/** May be null. */
		public @Nullable String labelText;
		public int labelWidth;
		public Color labelColor = Color.BLACK;

		public Color backgroundColor = Color.WHITE;
		public Color focusedBackgroundColor = new Color(0.95f, 0.95f, 0.95f, 1);

		void validate() {
			assert font != null;
			assert textColor != null;
			assert textWidth > 0;
			assert labelWidth >= 0;
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

	public SelectBox(LayoutArgs layoutArgs, Args args) {
		super(layoutArgs);

		args.validate();
		this.args = args.clone();

		this.layout = new GlyphLayout(args.font, "", 0, 0, args.textColor, args.textWidth, Align.left, false, null);
		recalculateLayout(parentWidth, parentHeight);
	}

	/**
	 * Adds the item to this select box, with no associated action.
	 *
	 * @param text the item's text.
	 */
	public void addItem(String text) {
		addItem(text, null);
	}

	/**
	 * Adds the item to this select box, with an optional associated action. If this is the first item added to this select box,
	 * the action will be immediately executed.
	 *
	 * @param text the item's text.
	 * @param action [optional] an action that is called when this item is selected. May be null.
	 */
	public void addItem(String text, @Nullable Consumer<SelectBox> action) {
		items.add(new Item(text, action));

		if (items.size() == 1 && action != null) {
			action.accept(this);
		}
	}

	public void setSelectedIndex(int index) {
		assert !items.isEmpty();
		assert index >= 0 && index < items.size();

		itemIndex = index;

		var action = items.get(itemIndex).action();
		if (action != null) {
			action.accept(this);
		}
	}

	public void setTextColor(Color color) {
		args.textColor = color;
	}

	public String selectedItem() {
		return items.get(itemIndex).text();
	}

	public int itemCount() {
		return items.size();
	}

	@Override
	public void onKeyDown(int keycode) {
		if (hasFocus && !items.isEmpty()) {
			if (keycode == Keys.LEFT || keycode == Keys.A || keycode == Keys.NUMPAD_4) {
				selectPrevious();
			} else if (keycode == Keys.RIGHT || keycode == Keys.D || keycode == Keys.NUMPAD_6) {
				selectNext();
			}
		}
	}

	private void selectNext() {
		selectNext(1);
	}

	private void selectPrevious() {
		selectNext(-1);
	}

	private void selectNext(int direction) {
		assert !items.isEmpty();
		assert direction == -1 || direction == 1;

		itemIndex += direction;
		if (itemIndex >= items.size()) {
			itemIndex = 0;
		} else if (itemIndex < 0) {
			itemIndex = items.size() - 1;
		}

		var action = items.get(itemIndex).action();
		if (action != null) {
			action.accept(this);
		}
	}

	@Override
	public ClickedObjectInfo onMouseClicked(int screenX, int screenY) {
		if (contains(screenX, screenY)) {
			gainFocus();
			if (withinLeftArrow(screenX)) {
				selectPrevious();
			} else if (withinRightArrow(screenX)) {
				selectNext();
			}
			return new ClickedObjectInfo(this, 0);
		}
		return null;
	}

	@Override
	public HoveredObjectInfo onMouseMoved(int screenX, int screenY) {
		highlightLeftArrow = highlightRightArrow = false;

		if (contains(screenX, screenY)) {
			gainFocus();
			highlightLeftArrow = withinLeftArrow(screenX);
			highlightRightArrow = withinRightArrow(screenX);

			return new HoveredObjectInfo(this, 0);
		} else {
			lostFocus();
		}
		return null;
	}

	private boolean contains(float screenX, float screenY) {
		return left <= screenX && right() >= screenX &&
				top <= screenY && bottom() >= screenY;
	}

	/**
	 * Assumes that the y position was already verified to be within the select box.
	 */
	private boolean withinLeftArrow(int screenX) {
		return leftIconLayout != null && screenX >= boxLeft() && screenX <= boxLeft() + leftIconLayout.width;
	}

	/**
	 * Assumes that the y position was already verified to be within the select box.
	 */
	private boolean withinRightArrow(int screenX) {
		return rightIconLayout != null && screenX >= right() - rightIconLayout.width && screenX <= right();
	}

	@Override
	public void draw(Graphics graphics) {
		var screenTop = Units.screenYToCameraY(graphics.uiCamera(), top + padding);

		drawBackground(graphics, screenTop);

		if (!items.isEmpty()) {
			drawText(graphics, screenTop);
		}
		drawLabelText(graphics, screenTop);
	}

	private void drawBackground(Graphics graphics, float screenTop) {
		var shapeRenderer = graphics.nonScalingShapeRenderer();
		shapeRenderer.setColor(hasFocus ? args.focusedBackgroundColor : args.backgroundColor);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.rect(boxLeft(), screenTop - height() - 4, args.textWidth + 4, height() + 7);
		shapeRenderer.end();
	}

	private void drawText(Graphics graphics, float screenTop) {
		var font = args.font;
		var text = items.get(itemIndex).text();

		var batch = graphics.nonScalingBatch();
		batch.begin();

		Color textColor = args.textColor;
		Color leftArrowColor = highlightLeftArrow ? args.arrowHoverColor : args.arrowColor;
		Color rightArrowColor = highlightRightArrow ? args.arrowHoverColor : args.arrowColor;

		font.setColor(leftArrowColor);
		this.leftIconLayout = font.draw(batch, "<", boxLeft() + 3, screenTop - 2, 0, 1, args.textWidth, Align.left, false, null);
		font.setColor(textColor);
		this.layout = font.draw(batch, text, boxLeft() + 2, screenTop - 2, 0, text.length(), args.textWidth - 2, Align.center, false, "");
		font.setColor(rightArrowColor);
		this.rightIconLayout = font.draw(batch, ">", left() + width() - 7, screenTop - 2, 0, 1, args.textWidth, Align.left, false, null);

		batch.end();
	}

	private void drawLabelText(Graphics graphics, float screenTop) {
		if (args.labelText != null) {
			var batch = graphics.nonScalingBatch();
			batch.begin();
			args.font.setColor(args.labelColor);
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
		} else if (!items.isEmpty()) {
			this.layout = new GlyphLayout(args.font, items.get(itemIndex).text, 0, items.get(itemIndex).text.length(), args.textColor, args.textWidth, Align.left, false, null);
		}
	}

	@Override
	public void gainFocus() {
		hasFocus = true;
	}

	@Override
	public void lostFocus() {
		hasFocus = false;
		highlightLeftArrow = highlightRightArrow = false;
	}
}
