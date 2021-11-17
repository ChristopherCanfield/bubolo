package bubolo.ui.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;

import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.util.Units;

public class Label extends PositionableUiComponent {
	private final BitmapFont font;
	private Color color;
	private String text;
	private final boolean canWrap;
	private int maxLineWidth;
	private GlyphLayout layout;

	private static Color transparent = Color.valueOf("00000000");
	private Color backgroundColor = new Color(transparent);

	public Label(LayoutArgs layoutArgs, String text) {
		this(layoutArgs, text, Fonts.UiGeneralTextFont, Color.BLACK, false, Gdx.graphics.getWidth());
	}

	public Label(LayoutArgs layoutArgs, String text, BitmapFont font, Color color) {
		this(layoutArgs, text, font, color, false, 0);
	}

	public Label(LayoutArgs layoutArgs, String text, BitmapFont font, Color color, boolean canWrap, int maxLineWidth) {
		super(layoutArgs);

		this.font = font;
		this.color = new Color(color);
		this.text = text;
		this.canWrap = canWrap;
		this.maxLineWidth = maxLineWidth;

		this.layout = new GlyphLayout(font, text, 0, text.length(), color, maxLineWidth, Align.left, canWrap, null);
		recalculateLayout(parentWidth, parentHeight);
	}

	public void setText(String text) {
		this.text = text;
		recalculateLayout(parentWidth, parentHeight);
	}

	public void setMaxLineWidth(int maxLineWidth) {
		this.maxLineWidth = maxLineWidth;
	}

	public void setTextColor(Color color) {
		this.color.set(color);
	}

	public void setTextAlpha(float alpha) {
		this.color.a = alpha;
	}

	public void setBackgroundColor(Color color) {
		this.backgroundColor.set(color);
	}

	@Override
	public void draw(Graphics graphics) {
		if (!text.isEmpty()) {
			var cameraY = Units.screenYToCameraY(graphics.uiCamera(), top + padding);
			drawBackground(graphics, cameraY);
			drawText(graphics, cameraY);
		}
	}

	private void drawBackground(Graphics graphics, float cameraY) {
		if (backgroundColor.a != 0) {
			var renderer = graphics.nonScalingShapeRenderer();
			Gdx.gl.glEnable(GL20.GL_BLEND);
			renderer.setColor(backgroundColor);
			renderer.begin(ShapeType.Filled);
			renderer.rect(left + padding - 3, cameraY - layout.height - 5, layout.width + 6, layout.height + 10);
			renderer.end();
		}
	}

	private void drawText(Graphics graphics, float cameraY) {
		Color previousFontColor = font.getColor();
		var batch = graphics.nonScalingBatch();
		batch.begin();
		font.setColor(color);
		font.draw(batch, text, left + padding, cameraY, 0, text.length(), maxLineWidth, Align.left, canWrap, null);
		batch.end();
		font.setColor(previousFontColor);
	}

	@Override
	public float width() {
		return layout.width + padding * 2;
	}

	@Override
	public float height() {
		return layout.height + padding * 2;
	}

	@Override
	protected void onRecalculateLayout() {
		this.layout = new GlyphLayout(font, text, 0, text.length(), color, maxLineWidth, Align.left, canWrap, null);
	}
}
