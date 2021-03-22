package bubolo.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import bubolo.util.Coords;
import bubolo.util.GameLogicException;
import bubolo.world.entity.concrete.Tank;

/**
 * The graphical representation of a Tank.
 *
 * @author BU CS673 - Clone Productions
 */
class TankSprite extends AbstractEntitySprite<Tank>
{
	// The index representing which animation frame will be drawn.
	private int frameIndex;

	// An index representing which row of the sprite sheet to use, based on color set.
	private int colorId = ColorSets.RED;

	// An array of all frames held in the texture sheet, by ROW and then COLUMN.
	// Why the y value first? Because the y value represents the color set to be used.
	private TextureRegion[][] frames;

	// An array of the frames to be used for the driving backward animation.
	private TextureRegion[][] backwardFrames;

	// An array of the frames to be used for the driving forward animation.
	private TextureRegion[][] forwardFrames;

	// Frame to be used for the standing (idle) animation
	private TextureRegion[] standingFrames;

	// The number of milliseconds per frame.
	private static final long millisPerFrame = 100;

	// The amount of time remaining for the current frame.
	private long frameTimeRemaining;

	// The time of the last frame, in milliseconds.
	private long lastFrameTime;

	// The current animation state of the tank, determines which animation to play.
	private int animationState = 1;

	// The last animation state that the tank was in, used to determine when to reset
	// the starting frame.
	private int lastAnimationState = 0;

	// Ensures that only one tank explosion is created per death.
	private boolean explosionCreated;

	// For player name drawing.
	private static final BitmapFont font = new BitmapFont();

	private static final Color ENEMY_TANK_NAME_COLOR = new Color(229/255f, 74/255f, 39/255f, 1);

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "tank.png";

	private static final String BULLET_TEXTURE_FILE = "bullet.png";
	private static final String MINE_TEXTURE_FILE = "mine.png";

	private final Texture bulletTexture;
	private final Texture mineTexture;

	private static final Color TANK_UI_BOX_COLOR = new Color(50/255f, 50/255f, 50/255f, 110/255f);
	private static final Color TANK_UI_FONT_COLOR = new Color(240/255f, 240/255f, 240/255f, 1f);

	/**
	 * Constructor for the TankSprite. This is Package-private because sprites should not be
	 * directly created outside of the graphics system.
	 *
	 * @param tank
	 *            Reference to the tank that this TankSprite represents.
	 */
	TankSprite(Tank tank)
	{
		super(DrawLayer.FOURTH, tank);

		bulletTexture = Graphics.getTexture(Graphics.TEXTURE_PATH + BULLET_TEXTURE_FILE);
		mineTexture = Graphics.getTexture(Graphics.TEXTURE_PATH + MINE_TEXTURE_FILE);
	}

	/**
	 * Draws the tank's name. This is a separate method to ensure that tank UI elements are drawn above all other objects.
	 * begin() must have been called on graphics.batch() before calling this method.
	 */
	void drawTankPlayerName(Graphics graphics) {
		var tank = getEntity();
		// Render non-hidden network tank names.
		if (!tank.isLocalPlayer() && visibility() != Visibility.NETWORK_TANK_HIDDEN) {
			var tankCameraCoords = tankCameraCoordinates(getEntity(), graphics.camera());
			font.setColor(ENEMY_TANK_NAME_COLOR);
			font.draw(graphics.batch(), tank.getPlayerName(), tankCameraCoords.x - 20, tankCameraCoords.y + 35);
		}
	}

	/**
	 * Draws the tank's health bar. Uses the shape renderer, so it should be called after completing drawing that uses the Batch.
	 * Unlike with the methods that use batch(), begin() does not need to be called on graphics.shapeRenderer() before calling this method.
	 */
	void drawTankUi(Graphics graphics) {
		var tank = getEntity();
		if (tank.isLocalPlayer()) {
			drawHealthBar(tank, graphics);
			drawTankAmmo(tank, graphics);
		}
	}

	private static void drawHealthBar(Tank tank, Graphics graphics) {
		if (tank.isAlive() && tank.getHitPoints() < tank.getMaxHitPoints()) {
			var shapeRenderer = graphics.shapeRenderer();
			shapeRenderer.begin(ShapeType.Filled);

			float healthPct = tank.getHitPoints() / Tank.TANK_MAX_HIT_POINTS;
			float healthBarInteriorBackgroundWidth = tank.getWidth() + 10;
			float healthBarInteriorWidth = healthBarInteriorBackgroundWidth * healthPct;

			var tankCameraCoords = tankCameraCoordinates(tank, graphics.camera());

			// Health bar's exterior.
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(tankCameraCoords.x - 17, tankCameraCoords.y + 18, healthBarInteriorBackgroundWidth + 4, 8);

			// Health bar's interior background.
			shapeRenderer.setColor(Color.GRAY);
			shapeRenderer.rect(tankCameraCoords.x - 15, tankCameraCoords.y + 20, healthBarInteriorBackgroundWidth, 4);

			// Health bar's interior.
			shapeRenderer.setColor(healthBarColor(healthPct));
			shapeRenderer.rect(tankCameraCoords.x - 15, tankCameraCoords.y + 20, healthBarInteriorWidth, 4);

			shapeRenderer.end();
		}
	}

	private static final Color RED_ORANGE = new Color(1.0f, 0.53f, 0.0f, 1.0f);

	private static Color healthBarColor(float healthPct) {
		if (healthPct > 0.85) {
			return Color.GREEN;
		} else if (healthPct > 0.65) {
			return Color.YELLOW;
		} else if (healthPct > 0.3) {
			return RED_ORANGE;
		} else {
			return Color.RED;
		}
	}

	private void drawTankAmmo(Tank tank, Graphics graphics) {
		drawTankAmmoBackground(graphics);
		drawTankAmmoIconsAndValues(tank, graphics);
	}

	private static void drawTankAmmoBackground(Graphics graphics) {
		// Blending is required to enable transparency.
		Gdx.gl.glEnable(GL20.GL_BLEND);
		var shapeRenderer = graphics.shapeRenderer();
		shapeRenderer.begin(ShapeType.Filled);

		shapeRenderer.setColor(TANK_UI_BOX_COLOR);

		float screenHalfWidth = graphics.camera().viewportWidth / 2.0f;
		float screenHeight = graphics.camera().viewportHeight;
		shapeRenderer.rect(screenHalfWidth - 70, screenHeight - 25, 140, 30);

		shapeRenderer.end();

		// Draw a thin border around the ammo UI box.
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(screenHalfWidth - 70, screenHeight - 25, 140, 30);

		shapeRenderer.end();

		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	private void drawTankAmmoIconsAndValues(Tank tank, Graphics graphics) {
		var spriteBatch = graphics.batch();
		spriteBatch.begin();

		float screenHalfWidth = graphics.camera().viewportWidth / 2.0f;
		float screenHeight = graphics.camera().viewportHeight;
		float bulletWidth = bulletTexture.getWidth() * 2;
		float bulletHeight = bulletTexture.getHeight() * 2;
		// Draw the bullet texture.
		spriteBatch.draw(bulletTexture, screenHalfWidth - 60, screenHeight - 20, bulletWidth, bulletHeight);

		int textVerticalPosition = (int) screenHeight - 5;
		// Render the ammo count text.
		font.setColor(TANK_UI_FONT_COLOR);
		font.draw(graphics.batch(), "x " + Integer.toString(tank.getAmmoCount()), screenHalfWidth - 60 + 12, textVerticalPosition);

		// Mine texture divided by number of frames per row.
		float mineWidth = mineTexture.getWidth() / 6;
		// Mine texture divided by number of frames per column.
		float mineHeight = mineTexture.getHeight() / 3;
		// Draw the mine texture.
		spriteBatch.draw(mineTexture, screenHalfWidth + 13, screenHeight - 22, mineWidth, mineHeight, 0, 0, 0.167f, 0.33f);

		// Render the mine count text.
		font.draw(graphics.batch(), "x " + Integer.toString(tank.getMineCount()), screenHalfWidth + 13 + 22, textVerticalPosition);

		spriteBatch.end();
	}

	private static Vector2 tankCameraCoordinates(Tank tank, Camera camera) {
		return Coords.worldToCamera(camera, new Vector2(tank.getX(), tank.getY()));
	}

	@Override
	public void draw(Graphics graphics)
	{
		if (isDisposed())
		{
			graphics.sprites().removeSprite(this);
			return;
		}
		else if (frames == null)
		{
			initialize(graphics);
		}
		else if (!getEntity().isAlive())
		{
			if(!explosionCreated)
			{
				explosionCreated = true;
				SpriteSystem spriteSystem = graphics.sprites();
				spriteSystem.addSprite(
						new TankExplosionSprite((int)getEntity().getX(), (int)getEntity().getY()));
			}
			return;
		}
		explosionCreated = false;

		if (visibility() != Visibility.NETWORK_TANK_HIDDEN) {
			animateAndDraw(graphics);
		}
	}

	private void animateAndDraw(Graphics graphics)
	{
		animationState = (getEntity().getSpeed() > 0.f) ? 1 : 0;
		switch (animationState)
		{
		case 0:
			if (lastAnimationState != 0)
			{
				lastAnimationState = 0;
				frameIndex = 0;
			}
			drawTexture(graphics, standingFrames[colorId]);
			break;

		case 1:
			if (lastAnimationState != 1)
			{
				frameIndex = 0;
				lastAnimationState = 1;
			}
			drawTexture(graphics, forwardFrames[frameIndex][colorId]);

			// Progress the tank drive forward animation.
			animate(forwardFrames);

			break;

		case 2:
			if (lastAnimationState != 2)
			{
				frameIndex = 0;
				lastAnimationState = 2;
			}
			drawTexture(graphics, backwardFrames[frameIndex][colorId]);

			// Progress the tank drive backward animation.
			animate(backwardFrames);

			break;

		default:
			throw new GameLogicException("Programming error in tankSprite: default case reached.");
		}
	}

	private static final Color tankHiddenColor = new Color(Color.WHITE).mul(1.f, 1.f, 1.f, 0.6f);

	private Visibility visibility()
	{
		if (getEntity().isHidden()) {
			if (getEntity().isLocalPlayer()) {
				setColor(tankHiddenColor);
				return Visibility.HIDDEN;
			} else {
				return Visibility.NETWORK_TANK_HIDDEN;
			}
		} else {
			setColor(Color.WHITE);
			return Visibility.VISIBLE;
		}
	}

	private void animate(TextureRegion[][] animationFrames)
	{
		frameTimeRemaining -= (System.currentTimeMillis() - lastFrameTime);
		lastFrameTime = System.currentTimeMillis();
		if (frameTimeRemaining < 0)
		{
			frameTimeRemaining = millisPerFrame;
			frameIndex = (frameIndex == animationFrames.length - 1) ? 0 : frameIndex + 1;
		}
	}

	/**
	 * Initializes the tank. This is needed because the Tank entity may not know whether it is local
	 * or not at construction time.
	 *
	 * @param camera
	 *            reference to the camera.
	 */
	private void initialize(Graphics graphics)
	{
		Texture texture = Graphics.getTexture(Graphics.TEXTURE_PATH + TEXTURE_FILE);
		frames = TextureUtil.splitFrames(texture, 32, 32);

		forwardFrames = new TextureRegion[][] { frames[0], frames[1], frames[2] };
		backwardFrames = new TextureRegion[][] { frames[0], frames[2], frames[1] };
		standingFrames = frames[0];
		frameIndex = 0;
		frameTimeRemaining = millisPerFrame;

		if (getEntity().isLocalPlayer())
		{
			CameraController controller = new TankCameraController(getEntity());
			graphics.addCameraController(controller);
			controller.setCamera(graphics.camera());
		}

		colorId = determineColorSet(getEntity());
	}

	private static int determineColorSet(Tank tank)
	{
		return tank.isLocalPlayer() ? ColorSets.BLUE : ColorSets.RED;
	}

	private enum Visibility
	{
		VISIBLE, NETWORK_TANK_HIDDEN, HIDDEN
	}
}
