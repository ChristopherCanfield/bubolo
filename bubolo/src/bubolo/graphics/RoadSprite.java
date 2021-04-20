package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.Road;

/**
 * The graphical representation of a Road.
 *
 * @author BU673 - Clone Industries
 */
class RoadSprite extends AbstractEntitySprite<Road>
{
	private TextureRegion[] frames;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "road.png";

	/**
	 * Constructs a RoadSprite. This is Package-private because sprites should not
	 * be directly created outside of the graphics system.
	 *
	 * @param road reference to the road that this RoadSprite represents.
	 */
	RoadSprite(Road road)
	{
		super(DrawLayer.TERRAIN_IMPROVEMENTS, road);

		frames = Graphics.getTextureRegion1d(TEXTURE_FILE, getClass());
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
