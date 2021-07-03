package bubolo.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;

public class LoadingScreen extends AbstractScreen {
	private final Color clearColor =  new Color(0.15f, 0.15f, 0.15f, 1);
	private final BitmapFont font = Fonts.Arial20;
	private final String text;
	private int drawCount;

	public LoadingScreen(String mapName) {
		this.text = "Loading " + mapName + "...";
	}

	@Override
	public Color clearColor() {
		return clearColor;
	}

	@Override
	protected void preDraw(Graphics graphics) {
	}

	@Override
	protected void postDraw(Graphics graphics) {
		var batch = graphics.nonScalingBatch();
		batch.begin();
		font.setColor(Color.WHITE);
		font.draw(batch, text, 0, graphics.uiCamera().viewportHeight - 100, 0, text.length(), graphics.uiCamera().viewportWidth, Align.center, false, null);
		batch.end();

		drawCount++;
	}

	public int drawCount() {
		return drawCount;
	}

	@Override
	public void onViewportResized(int newWidth, int newHeight) {
	}
}
