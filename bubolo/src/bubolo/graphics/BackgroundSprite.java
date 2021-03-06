package bubolo.graphics;

import com.badlogic.gdx.graphics.Texture;

/**
 * Grass image used to fix gaps between certain tiles.
 *
 * @author BU673 - Clone Industries
 */
class BackgroundSprite extends Sprite
{
	private final Texture texture;

	// Package private to allow the Graphics class to update this.
	int x;
	int y;

	/** The sprite's height. **/
	static final int HEIGHT = 48;
	/** The sprite's width. **/
	static final int WIDTH = 48;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "grass.png";

	BackgroundSprite(int x, int y)
	{
		super(DrawLayer.BACKGROUND);

		texture = Graphics.getTexture(Graphics.TEXTURE_PATH + TEXTURE_FILE);

		this.x = x;
		this.y = y;
	}

	@Override
	public void draw(Graphics graphics)
	{
		drawTexture(graphics, texture);
	}

	@Override
	public boolean isDisposed()
	{
		return false;
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