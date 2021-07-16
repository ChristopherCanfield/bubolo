package bubolo.ui;

import java.text.DecimalFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

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

	private static final String bulletTextureFile = "bullet.png";
	private static final String mineTextureFile = "mine.png";

	private final Texture bulletTexture;
	private final TextureRegion[][] mineTexture;

	private static final Color uiBoxColor = new Color(50 / 255f, 50 / 255f, 50 / 255f, 110 / 255f);
	private static final Color uiFontColor = new Color(240 / 255f, 240 / 255f, 240 / 255f, 1f);

	private static final DecimalFormat speedFormatter = new DecimalFormat("0.0 Kph");

	private int ammoCount;
	private String ammoCountText;

	private int mineCount;
	private String mineCountText;

	private float speedKph;
	private String speedText;

	public GameScreen(World world) {
		this.world = world;

		bulletTexture = Graphics.getTexture(bulletTextureFile);
		mineTexture = Graphics.getTextureRegion2d(mineTextureFile, 21, 20);
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

		drawStatusBar(graphics);
	}

	private void drawStatusBar(Graphics graphics) {
		drawStatusBarBackground(graphics);
		drawStatusBarValues(graphics);
	}

	private static void drawStatusBarBackground(Graphics graphics) {
		// Blending is required to enable transparency.
		Gdx.gl.glEnable(GL20.GL_BLEND);
		var shapeRenderer = graphics.shapeRenderer();
		shapeRenderer.begin(ShapeType.Filled);

		shapeRenderer.setColor(uiBoxColor);

		float screenHalfWidth = graphics.camera().viewportWidth / 2.0f;
		float screenHeight = graphics.camera().viewportHeight;
		shapeRenderer.rect(screenHalfWidth - 110, screenHeight - 25, 220, 30);

		shapeRenderer.end();

		// Draw a thin border around the ammo UI box.
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(screenHalfWidth - 110, screenHeight - 25, 220, 30);

		shapeRenderer.end();
	}

	private void drawStatusBarValues(Graphics graphics) {
		var spriteBatch = graphics.batch();
		spriteBatch.begin();
		spriteBatch.setColor(Color.WHITE);

		float screenHalfWidth = graphics.camera().viewportWidth / 2.0f;
		float screenHeight = graphics.camera().viewportHeight;
		float bulletWidth = bulletTexture.getWidth() * 2;
		float bulletHeight = bulletTexture.getHeight() * 2;
		// Draw the bullet texture.
		spriteBatch.draw(bulletTexture, screenHalfWidth - 100, screenHeight - 20, bulletWidth, bulletHeight);

		int textVerticalPosition = (int) screenHeight - 5;
		// Render the ammo count text.
		font.setColor(uiFontColor);
		font.draw(spriteBatch, ammoCountText, screenHalfWidth - 100 + 12, textVerticalPosition);

		// Render the tank's speed.
		int tankSpeedTextLocation = (int) ((speedKph < 10) ? screenHalfWidth - 20 : screenHalfWidth - 25);
		font.draw(spriteBatch, speedText, tankSpeedTextLocation, textVerticalPosition);

		// Mine texture divided by number of frames per row.
		float mineWidth = mineTexture[0][0].getRegionWidth();
		// Mine texture divided by number of frames per column.
		float mineHeight = mineTexture[0][0].getRegionHeight();
		// Draw the mine.
		spriteBatch.draw(mineTexture[0][1], screenHalfWidth + 53, screenHeight - 22, mineWidth, mineHeight);
		// Draw the darkest light on top of the mine.
		spriteBatch.draw(mineTexture[0][0], screenHalfWidth + 53, screenHeight - 22, mineWidth, mineHeight);

		// Render the mine count text.
		font.draw(spriteBatch, mineCountText, screenHalfWidth + 53 + 22, textVerticalPosition);

		spriteBatch.end();
	}


	@Override
	public void onViewportResized(int newWidth, int newHeight) {
	}

	@Override
	public void onTankAmmoCountChanged(int ammo) {
		this.ammoCount = ammo;
		this.ammoCountText = "x " + ammoCount;
	}

	@Override
	public void onTankMineCountChanged(int mines) {
		this.mineCount = mines;
		this.mineCountText = "x " + mineCount;
	}

	@Override
	public void onTankSpeedChanged(float speedWorldUnits, float speedKph) {
		this.speedKph = speedKph;
		this.speedText = speedFormatter.format(speedKph);
	}
}
