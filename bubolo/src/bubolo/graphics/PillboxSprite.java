package bubolo.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.world.Pillbox;
import bubolo.world.Pillbox.BuildStatus;
import bubolo.world.Tank;

/**
 * The graphical representation of a Pillbox
 *
 * @author BU673 - Clone Industries
 * @author Christopher D. Canfield
 */
class PillboxSprite extends AbstractEntitySprite<Pillbox> implements UiDrawable {
	private TextureRegion[][] frames;

	private Color lightColor;
	private boolean initialized;

	/** The file name of the texture. */
	private static final String textureFileName = "pillbox.png";

	private static final int pillboxNoTargetColumn = 0;
	private static final int pillboxHasTargetColumn = 1;

	private static final int colorColumn = 0;
	private static final int disabledColorColumn = 1;
	private static final int damageColumn = 2;

	private static final Color buildingColor = new Color(1, 1, 1, 0.5f);
	private static final Color hiddenColor = Color.WHITE; // TankSprite.HiddenByTreeColor;

	private static final DrawLayer defaultDrawLayer = DrawLayer.TerrainImprovements;
	private static final DrawLayer carriedOrBuildingDrawLayer = DrawLayer.Effects;

	/**
	 * Constructor for the PillboxSprite. This is Package-private because sprites should not be directly created outside of the
	 * graphics system.
	 *
	 * @param pillbox Reference to the pillbox that this PillboxSprite represents.
	 */
	PillboxSprite(Pillbox pillbox) {
		super(defaultDrawLayer, pillbox);

		frames = Graphics.getTextureRegion2d(textureFileName, 32, 32, 1, 0);
	}

	private void updateColor() {
		var pillbox = getEntity();
		var owner = pillbox.owner();
		if (pillbox.hasOwner() && owner instanceof Tank tank) {
			lightColor = tank.teamColor().color;
		} else {
			lightColor = Color.LIGHT_GRAY;
		}
	}

	@Override
	public void draw(Graphics graphics) {
		if (!initialized) {
			initialized = true;
		}

		if (!isDisposed()) {
			updateColor();

			var pillbox = getEntity();
			setDrawLayerFromBuildStatus(pillbox.buildStatus());

			if (pillbox.buildStatus() != BuildStatus.Carried) {
				boolean isBuilt = pillbox.buildStatus() == BuildStatus.Built;
				if (!isBuilt) {
					buildingColor.a = Math.min(0.9f, pillbox.builtPct() + 0.1f);
				}

				DamageState damageState = DamageState.getDamageState(getEntity());

				// Draw the pillbox.
				setColor(isBuilt ? Color.WHITE : buildingColor);
				if (pillbox.hasTarget() && damageState != DamageState.OutOfService) {
					drawTexture(graphics, frames[pillboxHasTargetColumn][0]);
				} else {
					drawTexture(graphics, frames[pillboxNoTargetColumn][0]);
				}

				// Draw the lights if the pillbox isn't out of service.
				setColor(isBuilt ? lightColor : buildingColor);
				if (damageState != DamageState.OutOfService) {
					drawTexture(graphics, frames[colorColumn][3]);
				} else {
					drawTexture(graphics, frames[disabledColorColumn][3]);
				}

				// Draw damage, if any.
				setColor(Color.WHITE);
				if (damageState != DamageState.Undamaged) {
					drawTexture(graphics, frames[damageColumn][damageState.damageFrameIndex]);
				}
			// If Tank is being carried.
			} else {
				var tank = (Tank) getEntity().owner();
				if (!tank.isHidden() || tank.isOwnedByLocalPlayer()) {
					if (!tank.isHidden()) {
						setColor(lightColor);
					} else {
						setColor(hiddenColor);
					}
					// Draw the pillbox.
					drawTexture(graphics, frames[colorColumn][0], 0.5f, tank.x(), tank.y(), tank.width() / 2 + 5, 35, tank.rotation());
				}
			}
		}
	}

	private boolean setDrawLayerFromBuildStatus(BuildStatus buildStatus) {
		var drawLayer = getDrawLayer();
		switch (buildStatus) {
		case Built:
		case Unbuilding:
			if (drawLayer == DrawLayer.Effects) {
				setDrawLayer(defaultDrawLayer);
			}
			break;
		case Carried:
		case Building:
			if (drawLayer != DrawLayer.Effects) {
				setDrawLayer(carriedOrBuildingDrawLayer);
			}
			break;
		}
		return false;
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
			} else if (damagePercent > 0.30f && damagePercent <= 0.60f) {
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
			if (e.buildStatus() == BuildStatus.Built) {
				StatusBarRenderer.drawHealthBar(e, graphics.shapeRenderer(), graphics.camera());
			} else if (e.buildStatus() == BuildStatus.Building || e.buildStatus() == BuildStatus.Unbuilding) {
				// Bar to show progress of building or unbuilding.
				StatusBarRenderer.drawHorizontalStatusBar(e, e.builtPct(), Color.CYAN, graphics.shapeRenderer(), graphics.camera());
			}
		}
	}
}
