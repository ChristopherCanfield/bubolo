package bubolo.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.Mine;
import bubolo.world.Tank;

/**
 * The graphical representation of a Mine.
 *
 * @author BU673 - Clone Industries
 * @author Christopher D. Canfield
 */
class MineSprite extends AbstractEntitySprite<Mine> {
	// The index representing which animation frame will be drawn.
	private int frameIndex;
	private final TextureRegion[][] frames;
	private static final int lightsFramesRow = 0;
	private static final int mineFramesRow = 1;

	private Color color;

	private boolean initialized;

	// The number of milliseconds per frame.
	private static final long millisPerFrame = 150;

	// The amount of time remaining for the current frame.
	private long frameTimeRemaining;

	// The time of the last frame, in milliseconds.
	private long lastFrameTime;

	/** The file name of the texture. */
	private static final String textureFileName = "mine.png";

	/**
	 * Constructor for the MineSprite. This is Package-private because sprites should not be directly created outside of the
	 * graphics system.
	 *
	 * @param mine reference to the Mine that this MineSprite represents.
	 */
	MineSprite(Mine mine) {
		super(DrawLayer.Mines, mine);

		frames = Graphics.getTextureRegion2d(textureFileName, 21, 20);
	}

	@Override
	public void draw(Graphics graphics) {
		if (!initialized) {
			initialize();
		}

		var mine = getEntity();
		// Hide other people's mines, unless they were seen when placed.
		if (!mine.isVisibleToLocalPlayer() && mine.isArmed()) {
			return;

		} else {
			setColor(Color.WHITE);
			drawTexture(graphics, frames[frameIndex][mineFramesRow]);

			setColor(color);
			drawTexture(graphics, frames[frameIndex][lightsFramesRow]);

			animate();
		}
	}

	private void animate() {
		if (getEntity().isArmed()) {
			frameTimeRemaining -= (System.currentTimeMillis() - lastFrameTime);
			lastFrameTime = System.currentTimeMillis();
			if (frameTimeRemaining < 0) {
				frameTimeRemaining = millisPerFrame;
				frameIndex++;
				if (frameIndex == frames.length) {
					frameIndex = 0;
				}
			}
		} else {
			frameIndex = 0;
		}
	}

	private void initialize() {
		var mineOwner = getEntity().owner();
		if (mineOwner instanceof Tank tank) {
			color = tank.teamColor().color;
		} else {
			color = TeamColor.Neutral.color;
		}
		initialized = true;
	}
}
