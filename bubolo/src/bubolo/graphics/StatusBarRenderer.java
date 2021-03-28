package bubolo.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import bubolo.util.Coords;
import bubolo.world.Damageable;

/**
 * Methods to render in-game status bars, such as health bars.
 *
 * @author Christopher D. Canfield
 */
final class StatusBarRenderer {
	private StatusBarRenderer() {}

	/**
	 * Draws a health bar above an entity. Only draws the health bar if the entity is alive and health is not at 100%.
	 *
	 * @param entity the damageable entity that will gain a health bar.
	 * @param shapeRenderer
	 * @param camera
	 */
	static void drawHealthBar(Damageable entity, ShapeRenderer shapeRenderer, Camera camera) {
		if (entity.isAlive() && entity.getHitPoints() < entity.getMaxHitPoints()) {
			shapeRenderer.begin(ShapeType.Filled);

			float healthPct = entity.getHitPoints() / entity.getMaxHitPoints();
			float healthBarInteriorBackgroundWidth = entity.getWidth() + 10;
			float healthBarInteriorWidth = healthBarInteriorBackgroundWidth * healthPct;

			var tankCameraCoords = Coords.worldToCamera(camera, new Vector2(entity.getX(), entity.getY()));

			// Health bar's exterior.
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(tankCameraCoords.x - 19, tankCameraCoords.y + 18, healthBarInteriorBackgroundWidth + 4, 8);

			// Health bar's interior background.
			shapeRenderer.setColor(Color.GRAY);
			shapeRenderer.rect(tankCameraCoords.x - 17, tankCameraCoords.y + 20, healthBarInteriorBackgroundWidth, 4);

			// Health bar's interior.
			shapeRenderer.setColor(healthBarColor(healthPct));
			shapeRenderer.rect(tankCameraCoords.x - 17, tankCameraCoords.y + 20, healthBarInteriorWidth, 4);

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
}
