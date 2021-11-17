package bubolo.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import bubolo.world.Swamp;

/**
 * Renders a swamp.
 *
 * @author BU673 - Clone Industries
 * @author Christopher D. Canfield
 */
class SwampSprite extends AbstractStaticEntitySprite {
	private final Texture texture;

	/** The file name of the texture. */
	private static final String textureFileName = "swamp.png";
	private static final int textureFileHashCode = textureFileName.hashCode();

	/**
	 * Constructor for the SwampSprite. This is Package-private because sprites should not be directly created outside of the graphics
	 * system.
	 *
	 * @param swamp Reference to the Swamp that this SwampSprite represents.
	 */
	SwampSprite(Swamp swamp) {
		super(DrawLayer.TerrainLevel1, swamp, (float) (MathUtils.random.nextInt(4) * (Math.PI / 2)));

		texture = Graphics.getTexture(textureFileName);
	}

	@Override
	protected int getTextureId() {
		return textureFileHashCode;
	}

	@Override
	public void draw(Graphics graphics) {
		drawTexture(graphics, texture);
	}
}