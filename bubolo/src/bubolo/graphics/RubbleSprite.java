package bubolo.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import bubolo.world.Rubble;

/**
 * Renders rubble.
 *
 * @author BU673 - Clone Industries
 * @author Christopher D. Canfield
 */
class RubbleSprite extends AbstractStaticEntitySprite {
	private Texture texture;

	/** The file name of the texture. */
	private static final String textureFileName = "rubble.png";

	/**
	 * Constructor for the RubbleSprite. This is Package-private because sprites should not be directly created outside of the
	 * graphics system.
	 *
	 * @param rubble Reference to the Rubble that this RubbleSprite represents.
	 */
	RubbleSprite(Rubble rubble) {
		super(DrawLayer.TerrainLevel1, rubble, (float) (MathUtils.random.nextInt(4) * (Math.PI / 2)) );

		texture = Graphics.getTexture(textureFileName);
	}

	@Override
	public void draw(Graphics graphics) {
		drawTexture(graphics, texture);
	}
}
