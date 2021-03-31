package bubolo.controllers.ai;

import java.util.UUID;

import com.badlogic.gdx.math.Intersector;

import bubolo.controllers.Controller;
import bubolo.world.Collidable;
import bubolo.world.Entity;
import bubolo.world.Tile;
import bubolo.world.World;
import bubolo.world.entity.concrete.Crater;
import bubolo.world.entity.concrete.Mine;
import bubolo.world.entity.concrete.MineExplosion;


/**
 * A controller for Mines. This controller automatically checks for collisions with tanks or bullets
 * and explodes the mine on detection
 *
 * @author BU CS673 - Clone Productions
 */
public class AiMineController implements Controller
{
	private final Mine mine;

	/**
	 * constructs an AI Mine controller
	 *
	 * @param mine
	 *            the mine this controller will correspond to.
	 */
	public AiMineController(Mine mine)
	{
		this.mine = mine;
	}

	@Override
	public void update(World world)
	{
		for(Collidable collider : world.getNearbyCollidables(mine.tileColumn(), mine.tileRow(), true)) {
			if (mine.isActive()
					&& collider != mine
					&& Intersector.overlapConvexPolygons(collider.bounds(), mine.bounds())) {

				{
					var args = new Entity.ConstructionArgs(UUID.randomUUID(), mine.x(), mine.y(), 0);
					MineExplosion mineExplosion = world.addEntity(MineExplosion.class, args);
				}
				{
					var args = new Entity.ConstructionArgs(UUID.randomUUID(), mine.x(), mine.y(), 0);
					Crater crater = world.addEntity(Crater.class, args);
				}

				Tile tile = mine.getTile();
				tile.clearElement(world);

				mine.dispose();
				return;
			}
		}
	}
}