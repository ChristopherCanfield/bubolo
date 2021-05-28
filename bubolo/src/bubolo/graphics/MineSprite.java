package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.Mine;

/**
 * The graphical representation of a Mine.
 *
 * @author BU673 - Clone Industries
 * @author Christopher D. Canfield
 */
class MineSprite extends AbstractEntitySprite<Mine> {
	// The index representing which animation frame will be drawn.
	private int frameIndex;

	// An index representing which row of the sprite sheet to use.
	private int colorId;

	// An array of all frames held in the texture sheet, by ROW and COLUMN.
	private final TextureRegion[][] allFrames;

	// The texture regions to be used for the idle animation.
	private final TextureRegion[][] idleFrames;

	// The number of milliseconds per frame.
	private static final long millisPerFrame = 150;

	// The amount of time remaining for the current frame.
	private long frameTimeRemaining;

	// The time of the last frame, in milliseconds.
	private long lastFrameTime;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "mine.png";

	/**
	 * Constructor for the MineSprite. This is Package-private because sprites should not be directly created outside of the
	 * graphics system.
	 *
	 * @param mine reference to the Mine that this MineSprite represents.
	 */
	MineSprite(Mine mine) {
		super(DrawLayer.Mines, mine);

		allFrames = Graphics.getTextureRegion2d(TEXTURE_FILE, 21, 21);
		idleFrames = new TextureRegion[][] { allFrames[0], allFrames[1], allFrames[2], allFrames[1] };
	}

	@Override
	public void draw(Graphics graphics) {
		if (!getEntity().isOwnedByLocalPlayer() && getEntity().isArmed()) {
			// Hide other people's mines, but give other players a chance to see it while the mine
			// is arming.
			return;
		} else {
			colorId = getEntity().isOwnedByLocalPlayer() ? SpriteColorSet.Blue.row : SpriteColorSet.Red.row;

			if (!getEntity().isArmed()) {
				frameIndex = 0;
			}

			drawTexture(graphics, idleFrames[frameIndex][colorId]);

			// Progress the Mine idle animation.
			frameTimeRemaining -= (System.currentTimeMillis() - lastFrameTime);
			lastFrameTime = System.currentTimeMillis();
			if (frameTimeRemaining < 0) {
				frameTimeRemaining = millisPerFrame;
				frameIndex = (frameIndex == idleFrames.length - 1) ? 0 : frameIndex + 1;
			}

		}
	}
}
