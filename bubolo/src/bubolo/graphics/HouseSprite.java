package bubolo.graphics;

import com.badlogic.gdx.graphics.Texture;

import bubolo.world.House;

/**
 * Renders a house texture.
 *
 * @author Christopher D. Canfield
 */
public class HouseSprite extends AbstractEntitySprite<House> {
	private final Texture texture;

	/** The file name of the texture. */
	private static final String textureFile = "house2.png";

	protected HouseSprite(House entity) {
		super(DrawLayer.TerrainImprovements, entity);

		texture = Graphics.getTexture(textureFile);
	}

	@Override
	void draw(Graphics graphics) {
		drawTexture(graphics, texture);
	}
}
