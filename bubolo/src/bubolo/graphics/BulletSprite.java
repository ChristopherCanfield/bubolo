package bubolo.graphics;

import com.badlogic.gdx.graphics.Texture;

import bubolo.world.entity.concrete.Bullet;

/**
 * The graphical representation of a bullet entity.
 *
 * @author BU673 - Clone Industries
 */
class BulletSprite extends AbstractEntitySprite<Bullet>
{
	private Texture image;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "bullet.png";

	/**
	 * Constructor for the BulletSprite. This is Package-private because sprites should not be
	 * directly created outside of the graphics system.
	 *
	 * @param bullet
	 *            Reference to the Bullet that this BulletSprite represents.
	 */
	BulletSprite(Bullet bullet)
	{
		super(DrawLayer.FOURTH, bullet);

		image = Graphics.getTexture(Graphics.TEXTURE_PATH + TEXTURE_FILE);
	}

	@Override
	public void draw(Graphics graphics)
	{
		if (isDisposed())
		{
			SpriteSystem spriteSystem = graphics.sprites();
			spriteSystem.addSprite(new BulletExplosionSprite(Math.round(getEntity().getX()), Math.round(getEntity().getY())));
			spriteSystem.removeSprite(this);
		}
		else
		{
			drawTexture(graphics, image);
		}
	}
}