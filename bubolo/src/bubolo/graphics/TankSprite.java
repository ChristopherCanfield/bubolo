package bubolo.graphics;

import java.text.DecimalFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import bubolo.util.Units;
import bubolo.world.Tank;

/**
 * The graphical representation of a Tank.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
class TankSprite extends AbstractEntitySprite<Tank> implements UiDrawable {
	private Color color;
	private Color bodyHiddenColor;
	private static final Color treadHiddenColor = new Color(Color.WHITE).mul(1, 1, 1, 0.6f);

	// The index representing which animation frame will be drawn.
	private int frameIndex;

	// An array of all frames held in the texture sheet, by row and then column. Row = color set.
	private TextureRegion[][] frames;

	// The number of milliseconds per animation frame.
	private static final long millisPerFrame = 100;

	// The amount of time remaining for the current frame.
	private long frameTimeRemaining;

	// The time of the last frame, in milliseconds.
	private long lastFrameTime;

	// Ensures that only one tank explosion is created per death.
	private boolean deathAnimationCreated;

	// For player name drawing.
	private static final BitmapFont font = Fonts.Arial16;

	private static final Color ENEMY_TANK_NAME_COLOR = new Color(229 / 255f, 74 / 255f, 39 / 255f, 1);

	/** The file name of the texture. */
	private static final String textureFileName = "tank.png";

	private static final String BULLET_TEXTURE_FILE = "bullet.png";
	private static final String MINE_TEXTURE_FILE = "mine.png";

	private final Texture bulletTexture;
	private final TextureRegion[][] mineTexture;

	private static final Color TANK_UI_BOX_COLOR = new Color(50 / 255f, 50 / 255f, 50 / 255f, 110 / 255f);
	private static final Color TANK_UI_FONT_COLOR = new Color(240 / 255f, 240 / 255f, 240 / 255f, 1f);

	private final ParticleEffect[] smokeEmitter = new ParticleEffect[3];
	private static final String smokeParticleEffectLowDamageFile = "res/particles/Particle Park Smoke Low Damage.p";
	private static final String smokeParticleEffectMediumDamageFile = "res/particles/Particle Park Smoke Medium Damage.p";
	private static final String smokeParticleEffectHighDamageFile = "res/particles/Particle Park Smoke High Damage.p";

	/**
	 * Constructor for the TankSprite. This is Package-private because sprites should not be directly created outside of the
	 * graphics system.
	 *
	 * @param tank Reference to the tank that this TankSprite represents.
	 */
	TankSprite(Tank tank) {
		super(DrawLayer.Tanks, tank);

		bulletTexture = Graphics.getTexture(BULLET_TEXTURE_FILE);
		mineTexture = Graphics.getTextureRegion2d(MINE_TEXTURE_FILE, 21, 20);

		smokeEmitter[0] = new ParticleEffect();
		smokeEmitter[0].load(Gdx.files.internal(smokeParticleEffectLowDamageFile), Gdx.files.internal("res/particles"));
		smokeEmitter[0].start();

		smokeEmitter[1] = new ParticleEffect();
		smokeEmitter[1].load(Gdx.files.internal(smokeParticleEffectMediumDamageFile), Gdx.files.internal("res/particles"));
		smokeEmitter[1].start();

		smokeEmitter[2] = new ParticleEffect();
		smokeEmitter[2].load(Gdx.files.internal(smokeParticleEffectHighDamageFile), Gdx.files.internal("res/particles"));
		smokeEmitter[2].start();
	}

	/**
	 * Draws the tank's name. This is a separate method to ensure that tank UI elements are drawn above all other objects. begin()
	 * must have been called on graphics.batch() before calling this method.
	 */
	void drawTankPlayerName(Graphics graphics) {
		var tank = getEntity();
		// Render names for visible network tanks.
		if (!tank.isOwnedByLocalPlayer() && visibility() != Visibility.NetworkTankHidden) {
			var tankCameraCoords = tankCameraCoordinates(getEntity(), graphics.camera());
			font.setColor(ENEMY_TANK_NAME_COLOR);
			font.draw(graphics.batch(), tank.playerName(), tankCameraCoords.x - 20, tankCameraCoords.y + 35);
		}
	}

	/**
	 * Draws the tank's health bar. Uses the shape renderer, so it should be called after completing drawing that uses the Batch.
	 * Unlike with the methods that use batch(), begin() does not need to be called on graphics.shapeRenderer() before calling
	 * this method.
	 */
	@Override
	public void drawUiElements(Graphics graphics) {
		var tank = getEntity();
		if (tank.isOwnedByLocalPlayer()) {
			if (tank.isAlive()) {
				StatusBarRenderer.drawHealthBar(tank, graphics.shapeRenderer(), graphics.camera());
			}
			drawStatusBar(tank, graphics);
		}
	}

	private void drawStatusBar(Tank tank, Graphics graphics) {
		drawStatusBarBackground(graphics);
		drawStatusBarValues(tank, graphics);
	}

	private static void drawStatusBarBackground(Graphics graphics) {
		// Blending is required to enable transparency.
		Gdx.gl.glEnable(GL20.GL_BLEND);
		var shapeRenderer = graphics.shapeRenderer();
		shapeRenderer.begin(ShapeType.Filled);

		shapeRenderer.setColor(TANK_UI_BOX_COLOR);

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

	private static final DecimalFormat speedFormatter = new DecimalFormat("0.0 Kph");

	private void drawStatusBarValues(Tank tank, Graphics graphics) {
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
		font.setColor(TANK_UI_FONT_COLOR);
		font.draw(spriteBatch, "x " + tank.ammo(), screenHalfWidth - 100 + 12, textVerticalPosition);

		// Render the tank's speed.
		int tankSpeedTextLocation = (int) ((tank.speedKph() < 10) ? screenHalfWidth - 20 : screenHalfWidth - 25);
		font.draw(spriteBatch, speedFormatter.format(tank.speedKph()), tankSpeedTextLocation, textVerticalPosition);

		// Mine texture divided by number of frames per row.
		float mineWidth = mineTexture[0][0].getRegionWidth();
		// Mine texture divided by number of frames per column.
		float mineHeight = mineTexture[0][0].getRegionHeight();
		// Draw the mine.
		spriteBatch.draw(mineTexture[0][1], screenHalfWidth + 53, screenHeight - 22, mineWidth, mineHeight);
		// Draw the darkest light on top of the mine.
		spriteBatch.draw(mineTexture[0][0], screenHalfWidth + 53, screenHeight - 22, mineWidth, mineHeight);

		// Render the mine count text.
		font.draw(spriteBatch, "x " + tank.mines(), screenHalfWidth + 53 + 22, textVerticalPosition);

		spriteBatch.end();
	}

	private static Vector2 tankCameraCoordinates(Tank tank, Camera camera) {
		return Units.worldToCamera(camera, tank.x(), tank.y());
	}

	@Override
	public void draw(Graphics graphics) {
		if (frames == null) {
			initialize(graphics);
		} else if (!getEntity().isAlive()) {
			if (!deathAnimationCreated) {
				deathAnimationCreated = true;

				if (getEntity().drowned()) {
					graphics.sprites().addSprite(new TankSinkingSprite(getEntity()));
				} else {
					graphics.sprites().addSprite(new TankExplosionSprite((int) getEntity().x(), (int) getEntity().y()));
				}
			}
		} else if (visibility() != Visibility.NetworkTankHidden) {
			deathAnimationCreated = false;
			drawTank(graphics);
			drawSmoke(graphics);
			animate();
		}
	}

	private void drawTank(Graphics graphics) {
		Color treadColor;
		Color bodyColor;
		if (visibility() == Visibility.Visible) {
			treadColor = Color.WHITE;
			bodyColor = color;
		} else {
			treadColor = treadHiddenColor;
			bodyColor = bodyHiddenColor;
		}

		// Draw treads.
		setColor(treadColor);
		drawTexture(graphics, frames[frameIndex][0]);

		// Draw body.
		setColor(bodyColor);
		drawTexture(graphics, frames[frameIndex][1]);
	}

	private void animate() {
		// Animate the tank if it is moving.
		if (getEntity().speed() > 0) {
			animate(frames);
		}
	}

	private final Vector2 tankCameraPos = new Vector2();

	private void drawSmoke(Graphics graphics) {
		var smokeEffectIndex = getSmokeEffectIndex(getEntity());
		if (smokeEffectIndex != -1) {
			Units.worldToCamera(graphics.camera(), getEntity().x(), getEntity().y(), tankCameraPos);
			smokeEmitter[smokeEffectIndex].setPosition(tankCameraPos.x, tankCameraPos.y);
			smokeEmitter[smokeEffectIndex].draw(graphics.batch(), Gdx.graphics.getDeltaTime());
		} else {
			for (var emitter : smokeEmitter) {
				// @NOTE (cdc 2021-06-06): Reset and move the emitter offscreen. This shouldn't be necessary, but it was added
				// to try to address a network issue where smoke would display in the wrong location.
				emitter.reset();
				emitter.setPosition(-100, -100);
			}
		}
	}

	private static int getSmokeEffectIndex(Tank tank) {
		var pctHealth = tank.hitPoints() / tank.maxHitPoints();
		if (pctHealth >= 0.85f) {
			return -1;
		} else if (pctHealth >= 0.5f) {
			return 0;
		} else if (pctHealth >= 0.25f) {
			return 1;
		} else {
			return 2;
		}
	}

	private Visibility visibility() {
		if (getEntity().isHidden()) {
			if (getEntity().isOwnedByLocalPlayer()) {
				return Visibility.Hidden;
			} else {
				return Visibility.NetworkTankHidden;
			}
		} else {
			return Visibility.Visible;
		}
	}

	private void animate(TextureRegion[][] animationFrames) {
		frameTimeRemaining -= (System.currentTimeMillis() - lastFrameTime);
		lastFrameTime = System.currentTimeMillis();
		if (frameTimeRemaining < 0) {
			frameTimeRemaining = millisPerFrame;
			frameIndex = (frameIndex == animationFrames.length - 1) ? 0 : frameIndex + 1;
		}
	}

	/**
	 * Initializes the tank sprite. This is needed because the Tank entity may not know whether it is local or not at construction time.
	 *
	 * @param graphics reference to the graphics system.
	 */
	private void initialize(Graphics graphics) {
		frames = Graphics.getTextureRegion2d(textureFileName, 32, 32);

		frameIndex = 0;
		frameTimeRemaining = millisPerFrame;

		var tank = getEntity();
		if (tank.isOwnedByLocalPlayer()) {
			CameraController controller = new TankCameraController(tank);
			graphics.setCameraController(controller);
			controller.setCamera(graphics.camera());
		}

		color = tank.teamColor().color;
		bodyHiddenColor = new Color(color).mul(1.f, 1.f, 1.f, 0.6f);
	}

	private enum Visibility {
		Visible, NetworkTankHidden, Hidden
	}
}
