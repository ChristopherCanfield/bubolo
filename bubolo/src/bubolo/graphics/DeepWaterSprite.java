package bubolo.graphics;

import com.badlogic.gdx.graphics.Texture;

import bubolo.world.DeepWater;

/**
 * DeepWater sprites should only be placed next to Water tiles, because they are not designed to match up with other tiles.
 *
 * @author BU673 - Clone Industries
 */
class DeepWaterSprite extends AbstractEntitySprite<DeepWater>
{
	/** The texture's file name. */
	private static final String TEXTURE_FILE = "deepwater.png";
	
	private final Texture texture;

	/**
	 * Constructor for the DeepWaterSprite. This is Package-private because sprites should not be
	 * directly created outside of the graphics system.
	 *
	 * @param deepWater
	 *            Reference to the DeepWater that this DeepWaterSprite represents.
	 */
	DeepWaterSprite(DeepWater deepWater)
	{
		super(DrawLayer.TERRAIN, deepWater);

		texture = Graphics.getTexture(TEXTURE_FILE);
	}

	@Override
	public void draw(Graphics graphics)
	{
		if (isDisposed()) {
			graphics.sprites().removeSprite(this);
		} else {
			drawTexture(graphics, texture);
		}
	}
}
