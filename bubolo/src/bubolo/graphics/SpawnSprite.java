package bubolo.graphics;

import com.badlogic.gdx.graphics.Texture;

import bubolo.world.Spawn;

/**
 * The graphical representation of Spawn entity.
 *
 * @author BU673 - Clone Industries
 */
class SpawnSprite extends AbstractEntitySprite<Spawn> {
	private final Texture image;

	// Whether the sprite should be drawn.
	private boolean visible;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "spawn.png";

	/**
	 * Constructor for the SpawnSprite. This is Package-private because sprites should not be directly created outside
	 * of the graphics system.
	 *
	 * @param spawn Reference to the spawn that this SpawnSprite represents.
	 */
	SpawnSprite(Spawn spawn) {
		super(DrawLayer.Top, spawn);

		image = Graphics.getTexture(TEXTURE_FILE);
	}

	@Override
	public void draw(Graphics graphics) {
		if (visible) {
			drawTexture(graphics, image);
		}
	}

	/**
	 * Specifies whether the spawn sprite should be visible. The default is false.
	 *
	 * @param visible true if the spawn sprite should be visible, or false otherwise.
	 */
	void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Specifies whether the spawn sprite is visible.
	 *
	 * @return true if the spawn sprite is visible, or false otherwise.
	 */
	boolean getVisible() {
		return visible;
	}
}
