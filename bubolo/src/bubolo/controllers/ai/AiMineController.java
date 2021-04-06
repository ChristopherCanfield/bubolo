package bubolo.controllers.ai;

import java.util.UUID;

import com.badlogic.gdx.math.Intersector;

import bubolo.controllers.ActorEntityController;
import bubolo.world.Crater;
import bubolo.world.Entity;
import bubolo.world.Mine;
import bubolo.world.MineExplosion;
import bubolo.world.Tank;
import bubolo.world.World;


/**
 * A controller for Mines. Checks for collisions with tanks,  and explodes the mine on detection
 *
 * @author BU CS673 - Clone Productions
 */
public class AiMineController extends ActorEntityController<Mine>
{
	/**
	 * Constructs an AI Mine controller.
	 *
	 * @param mine
	 *            the mine this controller controls.
	 */
	public AiMineController(Mine mine) {
		super(mine);
	}

	@Override
	public void update(World world)
	{
		var mine = parent();

		if (mine.isArmed()) {
			// Check if any tanks are touching this mine. If they are, explode the mine.
			for(Tank tank : world.getTanks()) {
				if (Intersector.overlapConvexPolygons(tank.bounds(), mine.bounds())) {
					mine.dispose();

					{
						// Add the explosion.
						var args = new Entity.ConstructionArgs(UUID.randomUUID(), mine.x(), mine.y(), 0);
						world.addEntity(MineExplosion.class, args);
					}
					{
						var terrainImprovement = world.getTerrainImprovement(mine.tileColumn(), mine.tileRow());
						if (terrainImprovement != null) {
							terrainImprovement.dispose();
						}

						// Add the crater.
						var args = new Entity.ConstructionArgs(UUID.randomUUID(), mine.x(), mine.y(), 0);
						world.addEntity(Crater.class, args);
					}

					return;
				}
			}
		}
	}
}