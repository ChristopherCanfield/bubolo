package bubolo.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import bubolo.world.Grass;

/**
 * Renders grass.
 *
 * @author BU673 - Clone Industries
 * @author Christopher D. Canfield.
 */
class GrassSprite extends AbstractStaticEntitySprite {
	private final Texture texture;

	/** The file name of the texture. */
	private static final String textureFileName = "grass.png";
	private static final int textureFileHashCode = textureFileName.hashCode();

	/**
	 * Constructor for the GrassSprite. This is Package-private because sprites should not be directly created outside
	 * of the graphics system.
	 *
	 * @param grass Reference to the Grass that this GrassSprite represents.
	 */
	GrassSprite(Grass grass) {
		super(DrawLayer.TerrainLevel1, grass, (float) (MathUtils.random.nextInt(4) * (Math.PI / 2)) );

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
