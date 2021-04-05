package bubolo.graphics;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;

import bubolo.world.Rubble;

/**
 * The graphical representation of Rubble.
 *
 * @author BU673 - Clone Industries
 */
class RubbleSprite extends AbstractEntitySprite<Rubble>
{
	private Texture image;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "rubble.png";

	private float rotation;

	/**
	 * Constructor for the RubbleSprite. This is Package-private because sprites should not
	 * be directly created outside of the graphics system.
	 *
	 * @param rubble
	 *            Reference to the Rubble that this RubbleSprite represents.
	 */
	RubbleSprite(Rubble rubble)
	{
		super(DrawLayer.SECOND, rubble);

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
