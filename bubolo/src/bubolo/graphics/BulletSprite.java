package bubolo.graphics;

import com.badlogic.gdx.graphics.Texture;

import bubolo.world.Bullet;

/**
 * The graphical representation of a bullet entity.
 *
 * @author BU673 - Clone Industries
 * @author Christopher D. Canfield
 */
class BulletSprite extends AbstractEntitySprite<Bullet> implements Bullet.BulletHitObjectObserver {
	private final Texture image;
	private boolean bulletHitObject;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "bullet.png";
	private static final int textureFileHashCode = TEXTURE_FILE.hashCode();

	/**
	 * Constructor for the BulletSprite. This is Package-private because sprites should not be directly created outside of the
	 * graphics system.
	 *
	 * @param bullet Reference to the Bullet that this BulletSprite represents.
	 */
	BulletSprite(Bullet bullet) {
		super(DrawLayer.Effects, bullet);

		image = Graphics.getTexture(TEXTURE_FILE);
		bullet.setBulletHitObjectObserver(this);
	}

	@Override
	protected int getTextureId() {
		return textureFileHashCode;
	}

	@Override
	public void draw(Graphics graphics) {
		if (isDisposed()) {
			SpriteSystem spriteSystem = graphics.sprites();
			spriteSystem.addSprite(new BulletExplosionSprite(Math.round(getEntity().x()), Math.round(getEntity().y()), bulletHitObject));
			spriteSystem.removeSprite(this);
		} else {
			drawTexture(graphics, image);
		}
	}

	@Override
	public void onBulletHitObject() {
		bulletHitObject = true;
	}
}