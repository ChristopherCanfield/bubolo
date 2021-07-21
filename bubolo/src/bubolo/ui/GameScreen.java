package bubolo.ui;

import java.text.DecimalFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import bubolo.Messenger.MessageObserver;
import bubolo.Systems;
import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.ui.gui.LayoutArgs;
import bubolo.ui.gui.MessageBar;
import bubolo.ui.gui.UiComponent.OffsetType;
import bubolo.ui.gui.UiComponent.VOffsetFrom;
import bubolo.world.TankObserver;

/**
 * The game user interface elements.
 *
 * @author Christopher D. Canfield
 */
public class GameScreen extends AbstractScreen implements TankObserver, MessageObserver {
	private static final Color clearColor =  new Color(0.15f, 0.15f, 0.15f, 1);

	private final BitmapFont font = Fonts.Arial16;

	// The message bar displays events reported by the game.
	private MessageBar messageBar;

	/* For tank hud/status bar. */

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

	/* End tank hud variables. */

	public GameScreen() {
		bulletTexture = Graphics.getTexture(bulletTextureFile);
		mineTexture = Graphics.getTextureRegion2d(mineTextureFile, 21, 20);

		addMessageBar();

		Systems.messenger().addObserver(this);
	}

	private void addMessageBar() {
		LayoutArgs messageBarLayoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		messageBar = new MessageBar(messageBarLayoutArgs, 10);
		messageBar.setVerticalOffset(50, OffsetType.ScreenUnits, VOffsetFrom.Top);

		root.add(messageBar);
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


	/* Messages from the tank. */

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


	/* Messages from the messenger subsystem. */

	private static final Color objectUnderAttackColor = Color.valueOf("E57D06FF");
	private static final Color thisPlayerLostObjectColor = Color.valueOf("FF72B4FF");
	private static final Color thisPlayerCapturedObjectColor = Color.valueOf("00D318FF");
	private static final Color otherPlayerLostObjectColor = Color.valueOf("C0C0C0FF");
	private static final Color thisPlayerDiedColor = thisPlayerLostObjectColor;
	private static final Color otherPlayerDiedColor = Color.CYAN;

	@Override
	public void messageObjectUnderAttack(String message) {
		messageBar.addMessage(message, objectUnderAttackColor);
	}

	@Override
	public void messageObjectCaptured(String message, boolean thisPlayerLostObject, boolean thisPlayerCapturedObject) {
		Color color;
		if (thisPlayerLostObject) {
			color = thisPlayerLostObjectColor;
		} else if (thisPlayerCapturedObject) {
			color = thisPlayerCapturedObjectColor;
		} else {
			color = otherPlayerLostObjectColor;
		}
		messageBar.addMessage(message, color);
	}

	@Override
	public void messagePlayerDied(String message, boolean thisPlayerDied) {
		messageBar.addMessage(message, thisPlayerDied ? thisPlayerDiedColor : otherPlayerDiedColor);
	}
}
