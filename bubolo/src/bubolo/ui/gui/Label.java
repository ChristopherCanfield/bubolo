package bubolo.ui.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

import bubolo.graphics.Graphics;
import bubolo.util.Units;

public class Label extends UiComponent {
	private final BitmapFont font;
	private final Color color;
	private String text;
	private final boolean canWrap;
	private int maxWidth;
	private GlyphLayout layout;

	public Label(LayoutArgs layoutArgs, BitmapFont font, Color color, String text) {
		this(layoutArgs, font, color, text, false, 0);
	}

	public Label(LayoutArgs layoutArgs, BitmapFont font, Color color, String text, boolean canWrap, int maxWidth) {
		super(layoutArgs);

		this.font = font;
		this.color = color;
		this.text = text;
		this.canWrap = canWrap;
		this.maxWidth = maxWidth;

		this.layout = new GlyphLayout(font, text, 0, text.length(), color, maxWidth, Align.left, canWrap, null);
		recalculateLayout(startLeft, startTop, parentWidth, parentHeight);
	}

	public void setText(String text) {
		this.text = text;
		recalculateLayout(startLeft, startTop, parentWidth, parentHeight);
	}

	public void setMaxRowSize(int maxRowSize) {
		this.maxWidth = maxRowSize;
	}

	@Override
	public void draw(Graphics graphics) {
		Color previousFontColor = font.getColor();
		var batch = graphics.nonScalingBatch();
		batch.begin();
		font.setColor(color);
		var screenTop = Units.screenYToCameraY(graphics.uiCamera(), top + padding);
		layout = font.draw(batch, text, left + padding, screenTop, 0, text.length(), maxWidth, Align.left, canWrap, null);
		batch.end();
		font.setColor(previousFontColor);
	}

	@Override
	protected float width() {
		return layout.width + padding * 2;
	}

	@Override
	protected float height() {
		return layout.height + padding * 2;
	}

	@Override
	protected void onRecalculateLayout() {
	}
}
