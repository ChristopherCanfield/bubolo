package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.Water;

/**
 * The graphical representation of a Water.
 *
 * @author BU673 - Clone Industries
 */
class WaterSprite extends AbstractEntitySprite<Water>
{
	private TextureRegion[] frames;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "water.png";

	/**
	 * Constructor for the WaterSprite. This is Package-private because sprites should not be
	 * directly created outside of the graphics system.
	 *
	 * @param water
	 *            Reference to the Water that this WaterSprite represents.
	 */
	WaterSprite(Water water)
	{
		super(DrawLayer.SECOND, water);

		var path = Graphics.TEXTURE_PATH + TEXTURE_FILE;
		frames = Graphics.getTextureRegion1d(path, getClass());
	}

	@Override
	public void draw(Graphics graphics)
	{
		int currentState = this.getEntity().getTilingState();
		if (isDisposed())
		{
			graphics.sprites().removeSprite(this);

		}
		else
		{
			drawTexture(graphics, frames[currentState]);
		}

		boolean[] corners = this.getEntity().getCornerMatches();
		if (currentState == 15 || currentState == 13 || currentState == 5 || currentState == 7)
		{
			if (!corners[0])
			{
				drawTexture(graphics, frames[16]);
			}
			else
			{
				drawTexture(graphics, frames[20]);
			}
		}

		if (currentState == 15 || currentState == 11 || currentState == 9 || currentState == 13)
		{
			if (!corners[1])
			{
				drawTexture(graphics, frames[17]);
			}
			else
			{
				drawTexture(graphics, frames[21]);
			}

		}

		if (currentState == 15 || currentState == 14 || currentState == 6 || currentState == 7)
		{
			if (!corners[2])
			{
				drawTexture(graphics, frames[18]);
			}
			else
			{
				drawTexture(graphics, frames[22]);
			}

		}

		if (currentState == 15 || currentState == 10 || currentState == 14 || currentState == 11)
		{
			if (!corners[3])
			{
				drawTexture(graphics, frames[19]);
			}
			else
			{
				drawTexture(graphics, frames[23]);
			}

		}
	}
}
