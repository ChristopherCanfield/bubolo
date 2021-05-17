package bubolo.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.Pillbox;
import bubolo.world.Tank;

/**
 * The graphical representation of a Pillbox
 *
 * @author BU673 - Clone Industries
 * @author Christopher D. Canfield
 */
class PillboxSprite extends AbstractEntitySprite<Pillbox> implements UiDrawable {
	private TextureRegion[][] frames;

	private int colorIndex = ColorSets.NEUTRAL;

	/** The file name of the texture. */
	private static final String textureFileName = "pillbox.png";

	private static final int colorColumn = 0;
	private static final int damageColumn = 1;

	private static final Color defaultColor = new Color(Color.WHITE);
	private static final Color moveColor = new Color(1, 1, 1, 0.5f);


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
			colorIndex = ColorSets.NEUTRAL + 1;
		} else if (getEntity().isOwnedByLocalPlayer()) {
			colorIndex = ColorSets.BLUE + 1;
		} else {
			colorIndex = ColorSets.RED + 1;
		}
	}

	@Override
	public void draw(Graphics graphics) {
		if (!isDisposed()) {
			updateColorSet();

			if (!getEntity().isBeingMoved()) {
				// Draw the pillbox.
				drawTexture(graphics, frames[colorColumn][0]);

				DamageState damageState = DamageState.getDamageState(getEntity());
				// Draw the lights if the pillbox isn't out of service.
				if (damageState != DamageState.OutOfService) {
					drawTexture(graphics, frames[colorColumn][colorIndex]);
				}

				// Draw damage, if any.
				if (damageState != DamageState.Undamaged) {
					drawTexture(graphics, frames[damageColumn][damageState.damageFrameIndex]);
				}
			} else {
				var tank = (Tank) getEntity().owner();

				// Draw the pillbox.
				drawTexture(graphics, frames[colorColumn][0], 0.5f, tank.centerX(), tank.centerY(), 0, 0);
			}
		}
	}

	private enum DamageState {
		Undamaged(-1), LightlyDamaged(0), ModeratelyDamaged(1), SeverelyDamaged(2), OutOfService(3);

		final int damageFrameIndex;

		DamageState(int damageFrameIndex) {
			this.damageFrameIndex = damageFrameIndex;
		}

		static DamageState getDamageState(Pillbox pillbox) {
			var damagePercent = pillbox.hitPoints() / pillbox.maxHitPoints();
			if (damagePercent >= 0.9F) {
				return Undamaged;
			} else if (damagePercent > 0.60f && damagePercent < 0.85f) {
				return LightlyDamaged;
			} else if (damagePercent > 0.30f && damagePercent < 0.60f) {
				return ModeratelyDamaged;
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
