package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.DeepWater;

/**
 * The graphical representation of a DeepWater.
 *
 * @author BU673 - Clone Industries
 */
class DeepWaterSprite extends AbstractEntitySprite<DeepWater>
{
	// list of texture regions, used for different tiling states
	private TextureRegion[] frames;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "deepwater.png";

	/**
	 * Constructor for the DeepWaterSprite. This is Package-private because sprites should not be
	 * directly created outside of the graphics system.
	 *
	 * @param deepWater
	 *            Reference to the DeepWater that this DeepWaterSprite represents.
	 */
	DeepWaterSprite(DeepWater deepWater)
	{
		super(DrawLayer.SECOND, deepWater);

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

		if (currentState == 0 || currentState == 13 || currentState == 5 || currentState == 7)
		{
			if (!corners[0])
			{
				drawTexture(graphics, frames[20]);
			}
			else
			{
				drawTexture(graphics, frames[16]);
			}
		}

		if (currentState == 0 || currentState == 11 || currentState == 9 || currentState == 13)
		{
			if (!corners[1])
			{
				drawTexture(graphics, frames[21]);
			}
			else
			{
				drawTexture(graphics, frames[17]);
			}

		}

		if (currentState == 0 || currentState == 14 || currentState == 6 || currentState == 7)
		{
			if (!corners[2])
			{
				drawTexture(graphics, frames[22]);
			}
			else
			{
				drawTexture(graphics, frames[18]);
			}

		}

		if (currentState == 0 || currentState == 10 || currentState == 14 || currentState == 11)
		{
			if (!corners[3])
			{
				drawTexture(graphics, frames[23]);
			}
			else
			{
				drawTexture(graphics, frames[19]);
			}
		}
	}
}
