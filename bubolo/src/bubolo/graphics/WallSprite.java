package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.entity.concrete.Wall;

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
		super(DrawLayer.THIRD, wall);

		var undamagedTexturePath = Graphics.TEXTURE_PATH + UNDAMAGED_TEXTURE_FILE;
		undamagedFrames = Graphics.getTextureRegion1d(undamagedTexturePath, getClass());

		var minorDamageTexturePath = Graphics.TEXTURE_PATH + MINOR_DAMAGE_TEXTURE_FILE;
		damagedFrames[0] = Graphics.getTextureRegion1d(minorDamageTexturePath, getClass());

		var mediumDamageTexturePath = Graphics.TEXTURE_PATH + MEDIUM_DAMAGE_TEXTURE_FILE;
		damagedFrames[1] = Graphics.getTextureRegion1d(mediumDamageTexturePath, getClass());

		var majorDamageTexturePath = Graphics.TEXTURE_PATH + MAJOR_DAMAGE_TEXTURE_FILE;
		damagedFrames[2] = Graphics.getTextureRegion1d(majorDamageTexturePath, getClass());
	}

	@Override
	public void draw(Graphics graphics)
	{
		if (isDisposed())
		{
			Sprites.getInstance().removeSprite(this);
		}
		else
		{
			TextureRegion[] frames;
			if (getEntity().getHitPoints() == Wall.MAX_HIT_POINTS) {
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
		int health = Math.round(entity.getHitPoints());
		if (health == Wall.MAX_HIT_POINTS) {
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