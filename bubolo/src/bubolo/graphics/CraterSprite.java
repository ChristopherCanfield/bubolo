package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.Crater;

/**
 * The graphical representation of a Crater
 *
 * @author BU673 - Clone Industries
 * @author Christopher D. Canfield
 */
class CraterSprite extends AbstractEntitySprite<Crater> {
	private static final String textureFileName = "crater.png";
	private static final int textureFileHashCode = textureFileName.hashCode();

	private static final int frameCount = 10;
	private static final int frameWidth = 32;
	private static final int framePaddingWidth = 1;

	private static final float initialFloodFrameTimeSeconds = 1;
	private static final float secondsPerRemainingFloodFrame = (Crater.FloodTimeSeconds - initialFloodFrameTimeSeconds) / frameCount;

	private final TextureRegion[] frames;

	private boolean flooding;
	private int frame;

	/**
	 * Constructs a CraterSprite. This is Package-private because sprites should not be directly created outside
	 * of the graphics system.
	 *
	 * @param graphics reference to the graphics system.
	 * @param crater reference to the crater that this CraterSprite represents.
	 */
	CraterSprite(Graphics graphics, Crater crater) {
		super(DrawLayer.TerrainImprovements, crater);

		frames = Graphics.getTextureRegion1d(textureFileName, frameCount, frameWidth, framePaddingWidth);
	}

	@Override
	protected int getTextureId() {
		return textureFileHashCode;
	}

	@Override
	public void draw(Graphics graphics) {
		if (!flooding) {
			flooding = getEntity().isFlooding();
			if (flooding) {
				scheduleFloodAnimations(graphics);
			}
		}

		drawTexture(graphics, frames[frame]);
	}

	private void scheduleFloodAnimations(Graphics graphics) {
		graphics.timer().scheduleSeconds(initialFloodFrameTimeSeconds, g -> {
			frame++;
		});

		for (int i = 1; i < frameCount - 2; i++) {
			graphics.timer().scheduleSeconds(initialFloodFrameTimeSeconds + (i * secondsPerRemainingFloodFrame),
					g -> { frame++; });
		}
	}
}
