package bubolo.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.util.TextureUtil;

/**
 * Grass image used to fix gaps between certain tiles.
 *
 * @author BU673 - Clone Industries
 */
class BackgroundSprite extends Sprite
{
	private TextureRegion[][] frames;

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

		Texture image = Graphics.getTexture(Graphics.TEXTURE_PATH + TEXTURE_FILE);
		frames = TextureUtil.splitFrames(image, HEIGHT, WIDTH);

		this.x = x;
		this.y = y;
	}

	@Override
	public void draw(SpriteBatch batch, Camera camera, DrawLayer layer)
	{
		drawTexture(batch, camera, layer, frames[0][0]);
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