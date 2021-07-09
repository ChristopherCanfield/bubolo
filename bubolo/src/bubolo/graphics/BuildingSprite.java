package bubolo.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import bubolo.util.GameLogicException;
import bubolo.world.Building;

/**
 * Renders a building image.
 *
 * @author Christopher D. Canfield
 */
public class BuildingSprite extends AbstractEntitySprite<Building> {
	private final TextureRegion[] texture;
	private final int appearanceIndex;

	private static final String textureFile = "buildings.png";

	private static final Color color1 = Color.LIGHT_GRAY;
	private static final Color color2 = Color.valueOf("B5B5B5");

	protected BuildingSprite(Building entity) {
		super(DrawLayer.TerrainImprovements, entity);

		texture = Graphics.getTextureRegion1d(textureFile, 2, 32, 0);
		appearanceIndex = MathUtils.random.nextInt(2);

		int colorIndex = MathUtils.random.nextInt(3);
		Color color;
		switch (colorIndex) {
		case 0:
			color = Color.WHITE;
			break;
		case 1:
			color = color1;
			break;
		case 2:
			color = color2;
			break;
		default:
			throw new GameLogicException("Failed to produce a color in BuildingSprite.");
		}
		setColor(color);
	}

	@Override
	void draw(Graphics graphics) {
		if (isDisposed()) {
			SpriteSystem spriteSystem = graphics.sprites();
			spriteSystem.addSprite(new BulletExplosionSprite(Math.round(getEntity().x()), Math.round(getEntity().y()), false));
			spriteSystem.removeSprite(this);
		} else {
			drawTexture(graphics, texture[appearanceIndex]);
		}
	}

	@Override
	public float getRotation() {
		return 0;
	}
}
