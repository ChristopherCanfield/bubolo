package bubolo.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.world.TankObserver;
import bubolo.world.World;

/**
 * The game user interface elements.
 *
 * @author Christopher D. Canfield
 */
public class GameScreen extends AbstractScreen implements TankObserver {
	private static final Color clearColor =  new Color(0.15f, 0.15f, 0.15f, 1);

	private final World world;

	private final BitmapFont font = Fonts.Arial16;

	public GameScreen(World world) {
		this.world = world;
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
//		var batch = graphics.nonScalingBatch();
//		batch.begin();
//		font.setColor(Color.WHITE);
//		font.draw(batch, text, 0, graphics.uiCamera().viewportHeight - 100, 0, text.length(), graphics.uiCamera().viewportWidth, Align.center, false, null);
//		batch.end();
	}

	@Override
	public void onViewportResized(int newWidth, int newHeight) {
	}

	@Override
	public void onTankAmmoCountChanged(int ammo) {
	}

	@Override
	public void onTankMineCountChanged(int mines) {
	}

	@Override
	public void onTankSpeedChanged(float speedWorldUnits, float speedKph) {
	}
}
