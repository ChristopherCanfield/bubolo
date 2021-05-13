package bubolo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.Pillbox;

/**
 * The graphical representation of a Pillbox
 *
 * @author BU673 - Clone Industries
 * @author Christopher D. Canfield
 */
class PillboxSprite extends AbstractEntitySprite<Pillbox> implements UiDrawable {
	private TextureRegion[][] frames;

	private int colorId = ColorSets.NEUTRAL;

	/** The file name of the texture. */
	private static final String textureFileName = "pillbox.png";

	private static final int colorColumn = 0;
	private static final int damageColumn = 1;

	/**
	 * Constructor for the PillboxSprite. This is Package-private because sprites should not be directly created outside of the
	 * graphics system.
	 *
	 * @param pillbox Reference to the pillbox that this PillboxSprite represents.
	 */
	PillboxSprite(Pillbox pillbox) {
		super(DrawLayer.TerrainImprovements, pillbox);

		frames = Graphics.getTextureRegion2d(textureFileName, 32, 32, 1, 0);
	}

	private void updateColorSet() {
		if (!getEntity().hasOwner()) {
			colorId = ColorSets.NEUTRAL;
		} else if (getEntity().isOwnedByLocalPlayer()) {
			colorId = ColorSets.BLUE;
		} else {
			colorId = ColorSets.RED;
		}
	}

	@Override
	public void draw(Graphics graphics) {
		updateColorSet();

		if (isDisposed()) {
			graphics.sprites().removeSprite(this);
			return;
		} else {
			drawTexture(graphics, frames[colorColumn][colorId]);

			DamageState damageState = DamageState.getDamageState(getEntity());
			if (damageState != DamageState.Undamaged) {
				drawTexture(graphics, frames[damageColumn][damageState.damageFrameIndex]);
			}
		}
	}

	private enum DamageState {
		Undamaged(-1), LightlyDamaged(0), SeverelyDamaged(1), OutOfService(2);

		final int damageFrameIndex;

		DamageState(int damageFrameIndex) {
			this.damageFrameIndex = damageFrameIndex;
		}

		static DamageState getDamageState(Pillbox pillbox) {
			var damagePercent = pillbox.hitPoints() / pillbox.maxHitPoints();
			if (damagePercent >= 0.75F) {
				return Undamaged;
			} else if (damagePercent > 0.30f && damagePercent < 0.75f) {
				return LightlyDamaged;
			} else if (damagePercent > 0 && damagePercent <= 0.30f) {
				return SeverelyDamaged;
			} else {
				return OutOfService;
			}
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
