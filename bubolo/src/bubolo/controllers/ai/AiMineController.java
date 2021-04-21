package bubolo.controllers.ai;

import java.util.UUID;

import com.badlogic.gdx.math.Intersector;

import bubolo.controllers.ActorEntityController;
import bubolo.world.Crater;
import bubolo.world.DeepWater;
import bubolo.world.Entity;
import bubolo.world.Mine;
import bubolo.world.MineExplosion;
import bubolo.world.Tank;
import bubolo.world.Water;
import bubolo.world.World;

/**
 * A controller for Mines. Checks for collisions with tanks, and explodes the mine on detection
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class AiMineController extends ActorEntityController<Mine> {
	/**
	 * Constructs an AI Mine controller.
	 *
	 * @param mine the mine this controller controls.
	 */
	public AiMineController(Mine mine) {
		super(mine);
	}

	@Override
	public void update(World world) {
		var mine = parent();

		if (mine.isArmed()) {
			// Check if any tanks are touching this mine. If they are, explode the mine.
			for (Tank tank : world.getTanks()) {
				if (Intersector.overlapConvexPolygons(tank.bounds(), mine.bounds())) {
					mine.dispose();

					var x = mine.x();
					var y = mine.y();

					addExplosion(world, x, y);
					removeTerrainImprovement(world, mine.tileColumn(), mine.tileRow());
					var crater = addCrater(world, x, y);
					floodCraterIfAdjacentToWater(world, crater);

					return;
				}
			}
		}
	}

	private static void addExplosion(World world, float x, float y) {
		var args = new Entity.ConstructionArgs(UUID.randomUUID(), x, y, 0);
		world.addEntity(MineExplosion.class, args);
	}

	private static void removeTerrainImprovement(World world, int tileColumn, int tileRow) {
		var terrainImprovement = world.getTerrainImprovement(tileColumn, tileRow);
		if (terrainImprovement != null) {
			terrainImprovement.dispose();
		}
	}

	private static Crater addCrater(World world, float x, float y) {
		var args = new Entity.ConstructionArgs(UUID.randomUUID(), x, y, 0);
		return world.addEntity(Crater.class, args);
	}

	private static void floodCraterIfAdjacentToWater(World world, Crater crater) {
		if (isAdjacentToWater(world, crater.tileColumn(), crater.tileRow())) {
			world.timer().scheduleSeconds(4, w -> {
				crater.replaceWithWater(world);
			});
		}
	}

	private static boolean isAdjacentToWater(World world, int tileX, int tileY) {
		boolean adjacentToWater = false;
		if (world.isValidTile(tileX - 1, tileY)) {
			adjacentToWater = adjacentToWater || isWater(world, tileX - 1, tileY);
		}
		if (world.isValidTile(tileX + 1, tileY)) {
			adjacentToWater = adjacentToWater || isWater(world, tileX + 1, tileY);
		}

		if (world.isValidTile(tileY - 1, tileY)) {
			adjacentToWater = adjacentToWater || isWater(world, tileX, tileY - 1);
		}
		if (world.isValidTile(tileY + 1, tileY)) {
			adjacentToWater = adjacentToWater || isWater(world, tileX, tileY + 1);
		}

		return adjacentToWater;
	}

	private static boolean isWater(World world, int tileX, int tileY) {
		var terrain = world.getTerrain(tileX, tileY);
		return terrain instanceof Water || terrain instanceof DeepWater;
	}
}
