package bubolo.controllers.ai;

import java.util.UUID;

import com.badlogic.gdx.math.Intersector;

import bubolo.controllers.ActorEntityController;
import bubolo.world.Collidable;
import bubolo.world.Crater;
import bubolo.world.Entity;
import bubolo.world.Mine;
import bubolo.world.MineExplosion;
import bubolo.world.World;


/**
 * A controller for Mines. This controller automatically checks for collisions with tanks or bullets
 * and explodes the mine on detection
 *
 * @author BU CS673 - Clone Productions
 */
public class AiMineController extends ActorEntityController<Mine>
{
	/**
	 * constructs an AI Mine controller
	 *
	 * @param mine
	 *            the mine this controller will correspond to.
	 */
	public AiMineController(Mine mine) {
		super(mine);
	}

	@Override
	public void update(World world)
	{
		var mine = parent();

		if (mine.isActive()) {
			for(Collidable collider : world.getNearbyCollidables(mine, true)) {
				if (Intersector.overlapConvexPolygons(collider.bounds(), mine.bounds())) {
					{
						var args = new Entity.ConstructionArgs(UUID.randomUUID(), mine.x(), mine.y(), 0);
						world.addEntity(MineExplosion.class, args);
					}
					{
						var args = new Entity.ConstructionArgs(UUID.randomUUID(), mine.x(), mine.y(), 0);
						world.addEntity(Crater.class, args);
					}

					mine.dispose();
					return;
				}
			}
		}
	}
}