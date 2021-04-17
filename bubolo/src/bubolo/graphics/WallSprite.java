package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.Wall;

/**
 * The graphical representation of a Wall
 *
 * @author BU673 - Clone Industries
 */
class WallSprite extends AbstractEntitySprite<Wall>
{
	private TextureRegion[] undamagedFrames;
	private TextureRegion[][] damagedFrames = new TextureRegion[3][];

	/** The file name of the texture. */
	private static final String UNDAMAGED_TEXTURE_FILE = "wall.png";
	private static final String MINOR_DAMAGE_TEXTURE_FILE = "wall_damaged_minor.png";
	private static final String MEDIUM_DAMAGE_TEXTURE_FILE = "wall_damaged_medium.png";
	private static final String MAJOR_DAMAGE_TEXTURE_FILE = "wall_damaged_major.png";

	/**
	 * Constructor for the WallSprite. This is Package-private because sprites should not be
	 * directly created outside of the graphics system.
	 *
	 * @param wall
	 *            the wall entity.
	 */
	WallSprite(Wall wall)
	{
		super(DrawLayer.TERRAIN_IMPROVEMENTS, wall);

		undamagedFrames = Graphics.getTextureRegion1d(UNDAMAGED_TEXTURE_FILE, getClass());
		damagedFrames[0] = Graphics.getTextureRegion1d(MINOR_DAMAGE_TEXTURE_FILE, getClass());
		damagedFrames[1] = Graphics.getTextureRegion1d(MEDIUM_DAMAGE_TEXTURE_FILE, getClass());
		damagedFrames[2] = Graphics.getTextureRegion1d(MAJOR_DAMAGE_TEXTURE_FILE, getClass());
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
			TextureRegion[] frames;
			if (getEntity().hitPoints() == getEntity().maxHitPoints()) {
				frames = undamagedFrames;
			} else {
				frames = damagedFrames[calculateDamagedFramesIndex(getEntity())];
			}
			drawTexture(graphics, frames[getEntity().getTilingState()]);
		}
	}

	/**
	 * Returns the index into the damaged frames, or -1 if the wall is undamaged.
	 */
	private static int calculateDamagedFramesIndex(Wall entity) {
		int health = Math.round(entity.hitPoints());
		if (health == entity.maxHitPoints()) {
			return -1;
		} else if (health >= 75) {
			return 0;
		} else if (health >= 25) {
			return 1;
		} else {
			return 2;
		}
	}
}