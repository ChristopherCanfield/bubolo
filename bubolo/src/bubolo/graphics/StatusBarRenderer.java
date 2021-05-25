package bubolo.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import bubolo.util.Units;
import bubolo.world.Damageable;
import bubolo.world.Entity;

/**
 * Methods to render in-game status bars, such as health bars.
 *
 * @author Christopher D. Canfield
 */
final class StatusBarRenderer {
	private StatusBarRenderer() {}

	/**
	 * Draws a health bar above an entity. Only draws the health bar if the entity's health is not at 100%.
	 *
	 * @param entity the damageable entity that will gain a health bar.
	 * @param shapeRenderer a shape renderer object.
	 * @param camera the camera.
	 */
	static void drawHealthBar(Damageable entity, ShapeRenderer shapeRenderer, Camera camera) {
		if (entity.hitPoints() < entity.maxHitPoints()) {
			shapeRenderer.begin(ShapeType.Filled);

			float healthPct = entity.hitPoints() / entity.maxHitPoints();
			float healthBarInteriorBackgroundWidth = entity.width() + 10;
			float healthBarInteriorWidth = healthBarInteriorBackgroundWidth * healthPct;

			var cameraCoords = Units.worldToCamera(camera, entity.x(), entity.y());
			var entityHalfWidth = entity.width() / 2.0f;
			var entityHalfHeight = entity.height() / 2.0f;

			// Health bar's exterior.
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(cameraCoords.x - entityHalfWidth - 8, cameraCoords.y + entityHalfHeight + 6, healthBarInteriorBackgroundWidth + 4, 8);

			// Health bar's interior background.
			shapeRenderer.setColor(Color.GRAY);
			shapeRenderer.rect(cameraCoords.x - entityHalfWidth - 6, cameraCoords.y + entityHalfHeight + 8, healthBarInteriorBackgroundWidth, 4);

			// Health bar's interior.
			shapeRenderer.setColor(healthBarColor(healthPct));
			shapeRenderer.rect(cameraCoords.x - entityHalfWidth - 6, cameraCoords.y + entityHalfHeight + 8, healthBarInteriorWidth, 4);

			shapeRenderer.end();
		}
	}

	private static final Color RED_ORANGE = new Color(1.0f, 0.53f, 0.0f, 1.0f);

	private static Color healthBarColor(float healthPct) {
		if (healthPct > 0.85) {
			return Color.GREEN;
		} else if (healthPct > 0.65) {
			return Color.YELLOW;
		} else if (healthPct > 0.3) {
			return RED_ORANGE;
		} else {
			return Color.RED;
		}
	}

	/**
	 * Draws a horizontal status bar above an entity.
	 *
	 * @param entity the target entity.
	 * @param pctFilled how filled the bar is.
	 * @param fillColor the color to fill the bar with.
	 * @param shapeRenderer a shape renderer.
	 * @param camera the game's camera.
	 */
	static void drawHorizontalStatusBar(Entity entity, float pctFilled, Color fillColor, ShapeRenderer shapeRenderer, Camera camera) {
		shapeRenderer.begin(ShapeType.Filled);

		float barInteriorBackgroundWidth = entity.width() + 10;
		float barInteriorWidth = barInteriorBackgroundWidth * pctFilled;

		var cameraCoords = Units.worldToCamera(camera, entity.x(), entity.y());
		var entityHalfWidth = entity.width() / 2.0f;
		var entityHalfHeight = entity.height() / 2.0f;

		// The bar's exterior.
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(cameraCoords.x - entityHalfWidth - 8, cameraCoords.y + entityHalfHeight + 6, barInteriorBackgroundWidth + 4, 8);

		// The bar's interior background.
		shapeRenderer.setColor(Color.GRAY);
		shapeRenderer.rect(cameraCoords.x - entityHalfWidth - 6, cameraCoords.y + entityHalfHeight + 8, barInteriorBackgroundWidth, 4);

		// The bar's interior.
		shapeRenderer.setColor(fillColor);
		shapeRenderer.rect(cameraCoords.x - entityHalfWidth - 6, cameraCoords.y + entityHalfHeight + 8, barInteriorWidth, 4);

		shapeRenderer.end();
	}
}
