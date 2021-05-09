package bubolo.graphics;

import com.badlogic.gdx.graphics.Texture;

import bubolo.world.Crater;

/**
 * The graphical representation of a Crater
 *
 * @author BU673 - Clone Industries
 */
class CraterSprite extends AbstractEntitySprite<Crater> {
	/** The file name of the texture. */
	private static final String textureFile = "crater.png";

	private final Texture texture;

	/**
	 * Constructs a CraterSprite. This is Package-private because sprites should not be directly created outside
	 * of the graphics system.
	 *
	 ** @param crater Reference to the crater that this CraterSprite represents.
	 */
	CraterSprite(Crater crater) {
		super(DrawLayer.TERRAIN_IMPROVEMENTS, crater);

		texture = Graphics.getTexture(textureFile);
	}

	@Override
	public void draw(Graphics graphics) {
		drawTexture(graphics, texture);
	}
}
