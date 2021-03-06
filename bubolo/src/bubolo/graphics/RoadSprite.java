package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.entity.concrete.Road;

/**
 * The graphical representation of a Road
 *
 * @author BU673 - Clone Industries
 */
class RoadSprite extends AbstractEntitySprite<Road>
{
	private TextureRegion[] frames;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "road.png";

	/**
	 * Constructor for the RoadSprite. This is Package-private because sprites should not
	 * be directly created outside of the graphics system.
	 *
	 * @param road
	 *            Reference to the road that this RoadSprite represents.
	 */
	RoadSprite(Road road)
	{
		super(DrawLayer.SECOND, road);

		var path = Graphics.TEXTURE_PATH + TEXTURE_FILE;
		frames = Graphics.getTextureRegion1d(path, getClass());
	}

	@Override
	public void draw(Graphics graphics)
	{
		if (isDisposed())
		{
			Sprites.getInstance().removeSprite(this);
		}
		else
		{
			drawTexture(graphics, frames[this.getEntity().getTilingState()]);
		}
	}
}
