package bubolo.graphics;

import java.util.Random;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import bubolo.world.entity.Entity;

/**
 * The graphical representation of grass entity.
 *
 * @author BU673 - Clone Industries
 */
class GrassSprite extends AbstractEntitySprite<Entity>
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
	GrassSprite(Entity grass)
	{
		super(DrawLayer.FIRST, grass);

		image = Graphics.getTexture(Graphics.TEXTURE_PATH + TEXTURE_FILE);
		Random rand = new Random();
		rotation = (float) (rand.nextInt(4) * (Math.PI/2));
	}

	@Override
	public void draw(SpriteBatch batch, Camera camera)
	{
		if (isDisposed())
		{
			Sprites.getInstance().removeSprite(this);
		}
		else
		{
			drawTexture(batch, camera, image);
		}
	}

	@Override
	public float getRotation()
	{
		return rotation;
	}
}
