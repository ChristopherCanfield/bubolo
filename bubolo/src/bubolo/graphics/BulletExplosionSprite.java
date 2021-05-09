package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A graphical bullet explosion.
 *
 * @author BU673 - Clone Industries
 */
class BulletExplosionSprite extends Sprite
{
	private TextureRegion[][] frames;

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

	private static final int HEIGHT = 32;
	private static final int WIDTH = 32;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "explosion.png";

	/**
	 * Constructs a BulletExplosionSprite. This is Package-private because sprites should not be
	 * directly created outside of the graphics system.
	 *
	 * @param x
	 *            the x position of the explosion.
	 * @param y
	 *            the y position of the explosion.
	 */
	BulletExplosionSprite(int x, int y)
	{
		super(DrawLayer.Effects);

		frames = Graphics.getTextureRegion2d(TEXTURE_FILE, WIDTH, HEIGHT);

		frameTimeRemaining = millisPerFrame;
		lastFrameTime = System.currentTimeMillis();

		this.x = x;
		this.y = y;
	}

	@Override
	public void draw(Graphics graphics)
	{
		drawTexture(graphics, frames[frameIndex][0]);
		animate();
	}

	private void animate()
	{
		frameTimeRemaining -= (System.currentTimeMillis() - lastFrameTime);
		lastFrameTime = System.currentTimeMillis();
		if (frameTimeRemaining < 0)
		{
			frameTimeRemaining = millisPerFrame;

			if (frameIndex < frames.length - 1)
			{
				++frameIndex;
			}
			else
			{
				disposed = true;
			}
		}
	}

	@Override
	public boolean isDisposed()
	{
		return disposed;
	}

	@Override
	public float getX()
	{
		return x;
	}

	@Override
	public float getY()
	{
		return y;
	}

	@Override
	public int getWidth()
	{
		return WIDTH;
	}

	@Override
	public int getHeight()
	{
		return HEIGHT;
	}

	@Override
	public float getRotation()
	{
		return 0.f;
	}
}