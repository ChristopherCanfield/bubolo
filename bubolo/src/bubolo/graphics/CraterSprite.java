package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.Crater;

/**
 * The graphical representation of a Crater
 *
 * @author BU673 - Clone Industries
 */
class CraterSprite extends AbstractEntitySprite<Crater>
{
	private TextureRegion[] frames;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "crater.png";

	/**
	 * Constructor for the CraterSprite. This is Package-private because sprites should
	 * not be directly created outside of the graphics system.
	 *
	 ** @param crater
	 *            Reference to the crater that this CraterSprite represents.
	 */
	CraterSprite(Crater crater)
	{
		super(DrawLayer.TERRAIN_IMPROVEMENTS, crater);

		var path = Graphics.TEXTURE_PATH + TEXTURE_FILE;
		frames = Graphics.getTextureRegion1d(path, getClass());
	}

	@Override
	public void draw(Graphics graphics)
	{
		if (isDisposed())
		{
			graphics.sprites().removeSprite(this);
		}
		else
		{
			drawTexture(graphics, frames[this.getEntity().getTilingState()]);
		}

	}
}
