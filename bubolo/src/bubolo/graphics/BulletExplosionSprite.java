package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A graphical bullet explosion.
 *
 * @author BU673 - Clone Industries
 */
class BulletExplosionSprite extends Sprite {
	private final TextureRegion[] frames;

	// The number of milliseconds per frame.
	private static final long millisPerFrame = 50;

	// The amount of time remaining for the current frame.
	private long frameTimeRemaining;

	// The time of the last frame.
	private long lastFrameTime;

	private int frameIndex;

	private boolean disposed;

	private final int x;
	private final int y;

	private final int width;
	private final int height;

	private static final int hitObjectWidthAndHeight = 32;
	private static final int maxRangeWidthAndHeight = 16;

	private static final String hitObjectTextureFileName = "bullet_explosion.png";
	private static final String maxRangeTextureFileName = "bullet_explosion_smoke.png";

	/**
	 * Constructs a BulletExplosionSprite. This is Package-private because sprites should not be directly created
	 * outside of the graphics system.
	 *
	 * @param x the x position of the explosion.
	 * @param y the y position of the explosion.
	 * @param bulletHitObject true if the bullet hit an object, or false if the bullet reached its maximum range.
	 */
	BulletExplosionSprite(int x, int y, boolean bulletHitObject) {
		super(DrawLayer.Effects);

		if (bulletHitObject) {
			frames = Graphics.getTextureRegion1d(hitObjectTextureFileName, 3, hitObjectWidthAndHeight, 0);
			width = height = hitObjectWidthAndHeight;
		} else {
			frames = Graphics.getTextureRegion1d(maxRangeTextureFileName, 3, maxRangeWidthAndHeight, 0);
			width = height = maxRangeWidthAndHeight;
		}

		frameTimeRemaining = millisPerFrame;
		lastFrameTime = System.currentTimeMillis();

		this.x = x;
		this.y = y;
	}

	@Override
	public void draw(Graphics graphics) {
		drawTexture(graphics, frames[frameIndex]);
		animate();
	}

	private void animate() {
		frameTimeRemaining -= (System.currentTimeMillis() - lastFrameTime);
		lastFrameTime = System.currentTimeMillis();
		if (frameTimeRemaining < 0) {
			frameTimeRemaining = millisPerFrame;

			if (frameIndex < frames.length - 1) {
				++frameIndex;
			} else {
				disposed = true;
			}
		}
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public float getRotation() {
		return 0.f;
	}
}