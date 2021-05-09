package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.Pillbox;

/**
 * The graphical representation of a Pillbox
 *
 * @author BU673 - Clone Industries
 */
class PillboxSprite extends AbstractEntitySprite<Pillbox> implements UiDrawable
{
	private TextureRegion[][] frames;

	/**
	 * Represents the total number of different damaged states that exist in this sprite's
	 * texture.
	 */
	// TODO (cdc - 3/27/2014): Uncomment the next line once the damaged state
	// functionality is implemented.
	//private final int DAMAGED_STATES = 5;

	private int colorId = ColorSets.NEUTRAL;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "pillbox.png";

	/**
	 * Constructor for the PillboxSprite. This is Package-private because sprites should
	 * not be directly created outside of the graphics system.
	 *
	 * @param pillbox
	 *            Reference to the pillbox that this PillboxSprite represents.
	 */
	PillboxSprite(Pillbox pillbox)
	{
		super(DrawLayer.TerrainImprovements, pillbox);

		frames = Graphics.getTextureRegion2d(TEXTURE_FILE, 32, 32);
	}

	private void updateColorSet()
	{
		if (!getEntity().hasOwner())
		{
			colorId = ColorSets.NEUTRAL;
		}
		else if (getEntity().isOwnedByLocalPlayer())
		{
			colorId = ColorSets.BLUE;
		}
		else
		{
			colorId = ColorSets.RED;
		}
	}

	@Override
	public void draw(Graphics graphics)
	{
		updateColorSet();

		if (isDisposed())
		{
			graphics.sprites().removeSprite(this);
			return;
		}
		else
		{
			// TODO: Point to different texture regions based on the damagedState field,
			// which changes with Entity HP percentage.
			drawTexture(graphics, frames[0][colorId]);
		}
	}

	@Override
	public void drawUiElements(Graphics graphics) {
		var e = getEntity();
		if (e.isOwnedByLocalPlayer()) {
			StatusBarRenderer.drawHealthBar(getEntity(), graphics.shapeRenderer(), graphics.camera());
		}
	}
}
