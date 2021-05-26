package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.Base;

/**
 * The graphical representation of a Base Entity.
 *
 * @author BU673 - Clone Industries
 * @author Christopher D. Canfield
 */
class BaseSprite extends AbstractEntitySprite<Base> implements UiDrawable
{
	// The index representing which animation frame will be drawn.
	private int frameIndex;

	// An index representing which row of the sprite sheet to use, based on color set.
	private int colorId = SpriteColorSet.BLUE;

	// An array of all frames held in the texture sheet, by ROW and COLUMN.
	private final TextureRegion[][] allFrames;

	// The list of texture regions to be used for the charging animation of an owned base.
	private final TextureRegion[][] chargingFrames;

	// The texture regions to be used for an owned base's idle animation.
	private final TextureRegion[] idleFrames;

	// The number of milliseconds per frame.
	private static final long millisPerFrame = 100;

	// The amount of time remaining for the current frame.
	private long frameTimeRemaining;

	// The time of the last frame, in milliseconds.
	private long lastFrameTime;

	// The last animation state that the Base was in, used to determine when to reset
	// the starting frame.
	private int lastAnimationState = 0;

	private final int minRefuelingAnimationTimeMillis = 1_000;
	private long refuelingAnimationEndTime;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "base.png";

	/**
	 * Constructor for the BaseSprite. This is Package-private because sprites should not
	 * be directly created outside of the graphics system (instead, call the
	 * Sprite.create(entity) static method).
	 *
	 * @param base
	 *            Reference to the Base that this BaseSprite represents.
	 */
	BaseSprite(Base base)
	{
		super(DrawLayer.TerrainImprovements, base);

		allFrames = Graphics.getTextureRegion2d(TEXTURE_FILE, 32, 32);
		chargingFrames = new TextureRegion[][] { allFrames[0], allFrames[1], allFrames[2],
				allFrames[3], allFrames[4], allFrames[5], allFrames[6], allFrames[7] };
		idleFrames = allFrames[0];
	}

	private void updateColorSet()
	{
		if (!getEntity().hasOwner()) {
			colorId = SpriteColorSet.NEUTRAL;
		} else if (getEntity().isOwnedByLocalPlayer()) {
			colorId = SpriteColorSet.BLUE;
		} else {
			colorId = SpriteColorSet.RED;
		}
	}

	@Override
	public void draw(Graphics graphics)
	{
		updateColorSet();

		if (refuelingAnimationEndTime < System.currentTimeMillis()) {
			if (getEntity().isRefueling()) {
				refuelingAnimationEndTime = System.currentTimeMillis() + minRefuelingAnimationTimeMillis;
			}
		}

		if (refuelingAnimationEndTime < System.currentTimeMillis()) {
			if (lastAnimationState != 0) {
				lastAnimationState = 0;
				frameIndex = 0;
			}
			drawTexture(graphics, idleFrames[colorId]);
		} else {
			if (lastAnimationState != 1) {
				lastAnimationState = 1;
				frameIndex = 0;
			}
			drawTexture(graphics, chargingFrames[frameIndex][colorId]);

			// Progress the Base charging animation.
			frameTimeRemaining -= (System.currentTimeMillis() - lastFrameTime);
			lastFrameTime = System.currentTimeMillis();
			if (frameTimeRemaining < 0) {
				frameTimeRemaining = millisPerFrame;
				frameIndex = (frameIndex == chargingFrames.length - 1) ? 0 : frameIndex + 1;
			}
		}
	}

	@Override
	public void drawUiElements(Graphics graphics) {
		var e = getEntity();
		if (e.isOwnedByLocalPlayer()) {
			StatusBarRenderer.drawHealthBar(getEntity(), graphics.shapeRenderer(), graphics.camera());
		}
	}
}
