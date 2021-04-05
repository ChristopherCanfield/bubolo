package bubolo.graphics;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;

import bubolo.world.Grass;

/**
 * The graphical representation of grass entity.
 *
 * @author BU673 - Clone Industries
 */
class GrassSprite extends AbstractEntitySprite<Grass>
{
	private final Texture image;

	private float rotation;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "grass.png";

	/**
	 * Constructor for the GrassSprite. This is Package-private because sprites should not
	 * be directly created outside of the graphics system.
	 *
	 * @param grass
	 *            Reference to the Grass that this GrassSprite represents.
	 */
	GrassSprite(Grass grass)
	{
		super(DrawLayer.FIRST, grass);

		image = Graphics.getTexture(Graphics.TEXTURE_PATH + TEXTURE_FILE);
		Random rand = new Random();
		rotation = (float) (rand.nextInt(4) * (Math.PI/2));
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
			drawTexture(graphics, image);
		}
	}

	@Override
	public float getRotation()
	{
		return rotation;
	}
}
