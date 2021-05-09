package bubolo.graphics;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;

import bubolo.world.Swamp;

/**
 * The graphical representation of a Swamp.
 *
 * @author BU673 - Clone Industries
 */
class SwampSprite extends AbstractEntitySprite<Swamp>
{
	private final Texture image;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "swamp.png";

	private float rotation;

	/**
	 * Constructor for the SwampSprite. This is Package-private because sprites should not
	 * be directly created outside of the graphics system.
	 *
	 * @param swamp
	 *            Reference to the Swamp that this SwampSprite represents.
	 */
	SwampSprite(Swamp swamp)
	{
		super(DrawLayer.TERRAIN, swamp);

		image = Graphics.getTexture(TEXTURE_FILE);

		Random rand = new Random();
		rotation = (float) (rand.nextInt(4) * (Math.PI/2));
	}

	@Override
	public void draw(Graphics graphics)
	{
		drawTexture(graphics, image);
	}

	@Override
	public float getRotation()
	{
		return rotation;
	}
}