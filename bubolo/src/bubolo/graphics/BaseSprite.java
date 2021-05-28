package bubolo.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.Base;

/**
 * The graphical representation of a Base Entity.
 *
 * @author BU673 - Clone Industries
 * @author Christopher D. Canfield
 */
class BaseSprite extends AbstractEntitySprite<Base> implements UiDrawable {
	private static final int structureRow = 0;
	private static final int structureColumn = 0;

	private static final int lightRow = 1;
	private static final int lightEnabledColumn = 0;
	private static final int lightDisabledColumn = 1;

	private static final int refuelingRow = 0;
	private static final int firstRefuelingColumn = 1;
	private static final int lastRefuelingColumn = 6;

	private int refuelingFrame = firstRefuelingColumn;
	private boolean playingRefuelingAnimation;
	private final float refuelingSecondsPerFrame = 0.15f;

	private static final int damageRow = 1;
	private static final int firstDamageColumn = 2;
	private static final int lastDamageColumn = 6;

	// The sprite's frames, arranged in column-row order.
	private final TextureRegion[][] frames;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "repair_bay.png";

	/**
	 * Constructor for the BaseSprite. This is Package-private because sprites should not be directly created outside of the
	 * graphics system (instead, call the Sprite.create(entity) static method).
	 *
	 * @param base reference to the Base that this BaseSprite represents.
	 */
	BaseSprite(Base base) {
		super(DrawLayer.TerrainImprovements, base);

		frames = Graphics.getTextureRegion2d(TEXTURE_FILE, 32, 32, 1, 1);
	}

	@Override
	public void draw(Graphics graphics) {
		drawTexture(graphics, frames[structureColumn][structureRow]);

		var base = getEntity();
		int damageFrame = getDamageFrame(base);
		if (damageFrame != -1) {
			drawTexture(graphics, frames[damageFrame][damageRow]);
		}

		int lightFrame = (damageFrame != lastDamageColumn) ? lightEnabledColumn : lightDisabledColumn;

		Color tintColor = getTintColor(base);
		setColor(tintColor);

		drawTexture(graphics, frames[lightFrame][lightRow]);

		if (base.isRefueling()) {
			drawTexture(graphics, frames[refuelingFrame][refuelingRow]);
			if (!playingRefuelingAnimation) {
				graphics.timer().scheduleSeconds(refuelingSecondsPerFrame, this::nextRefuelingFrame);
				playingRefuelingAnimation = true;
			}
		}

		setColor(Color.WHITE);
	}

	private static Color getTintColor(Base base) {
		if (!base.hasOwner()) {
			return SpriteColorSet.Neutral.color;
		} else if (base.isOwnedByLocalPlayer()) {
			return SpriteColorSet.Blue.color;
		} else {
			return SpriteColorSet.Red.color;
		}
	}

	/**
	 * Gets the damage frame to draw, based on the repair bay's damage %. Returns -1 if the repair bay is undamaged.
	 *
	 * @param base the repair bay.
	 * @return the damage frame to draw, or -1 if the repair bay is undamaged.
	 */
	private static int getDamageFrame(Base base) {
		var healthPct = base.hitPoints() / base.maxHitPoints();
		if (healthPct >= 1) {
			return -1;
		} else if (healthPct >= 0.8f) {
			return firstDamageColumn;
		} else if (healthPct >= 0.5f) {
			return firstDamageColumn + 1;
		} else if (healthPct >= 0.3f) {
			return firstDamageColumn + 2;
		} else if (healthPct > 0) {
			return firstDamageColumn + 3;
		} else {
			return lastDamageColumn;
		}
	}

	private void nextRefuelingFrame(Graphics graphics) {
		Base base = getEntity();
		if (base.isRefueling()) {
			refuelingFrame++;
			if (refuelingFrame > lastRefuelingColumn) {
				refuelingFrame = firstRefuelingColumn;
			}
			graphics.timer().scheduleSeconds(refuelingSecondsPerFrame, this::nextRefuelingFrame);
		} else {
			refuelingFrame = firstRefuelingColumn;
			playingRefuelingAnimation = false;
		}
	}

	@Override
	public void drawUiElements(Graphics graphics) {
		var e = getEntity();
		if (e.isOwnedByLocalPlayer()) {
			StatusBarRenderer.drawHealthBar(getEntity(), graphics.shapeRenderer(), graphics.camera());
		}
	}
}
