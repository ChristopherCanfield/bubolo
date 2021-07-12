package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.MineExplosion;

/**
 * The graphical representation of a MineExplosion Entity.
 *
 * @author BU673 - Clone Industries
 */
class MineExplosionSprite extends AbstractEntitySprite<MineExplosion> {
	private int frameIndex;
	private final TextureRegion[] frames;

	private static final String textureFileName = "mine_explosion.png";
	private static final int frameCount = 12;
	private static final int frameWidth = 60;

	private static final float secondsPerFrame = MineExplosion.LifetimeSeconds / frameCount;

	/**
	 * Constructs a MineExplosionSprite. This is Package-private because sprites should not be directly created outside
	 * of the graphics system.
	 *
	 * @param exp reference to the MineExplosion that this MineExplosionSprite represents.
	 */
	MineExplosionSprite(Graphics graphics, MineExplosion exp) {
		super(DrawLayer.Effects, exp);

		frames = Graphics.getTextureRegion1d(textureFileName, frameCount, frameWidth, 0);

		for (int i = 1; i < frameCount; i++) {
			graphics.timer().scheduleSeconds(secondsPerFrame * i, g -> {
				frameIndex++;
			});
		}
	}

	@Override
	public void draw(Graphics graphics) {
		drawTexture(graphics, frames[frameIndex]);
	}
}