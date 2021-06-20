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
	private final String text;
	private GlyphLayout layout;

	public Label(LayoutArgs layoutArgs, BitmapFont font, Color color, String text) {
		super(layoutArgs);

		this.font = font;
		this.color = color;
		this.text = text;

		recalculateLayout(layoutArgs.startLeft(), layoutArgs.startTop(), layoutArgs.parentWidth(), layoutArgs.parentHeight());
	}

	@Override
	public void draw(Graphics graphics) {
		var batch = graphics.nonScalingBatch();
		batch.begin();
		font.setColor(color);
		var screenTop = Units.screenYToCameraY(graphics.uiCamera(), top + padding);
		layout = font.draw(batch, text, left + padding, screenTop, 0, text.length(), 0, Align.left, false, null);
		batch.end();
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
