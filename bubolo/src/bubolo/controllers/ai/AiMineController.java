package bubolo.controllers.ai;

import com.badlogic.gdx.math.Intersector;

import bubolo.controllers.Controller;
import bubolo.util.TileUtil;
import bubolo.world.Tile;
import bubolo.world.World;
import bubolo.world.entity.Entity;
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
	private Mine mine;

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
		for(Entity collider:TileUtil.getLocalEntities(mine.getX(),mine.getY(), world))
		{
			if (mine.isActive() &&
					collider.isSolid() && collider != mine
					&& Intersector.overlapConvexPolygons(collider.getBounds(), mine.getBounds())) {

				MineExplosion mineExplosion = world.addEntity(MineExplosion.class);
				mineExplosion.setTransform(mine.getX(), mine.getY(), 0);

				Tile tile = mine.getTile();
				tile.clearElement();

				Crater crater = world.addEntity(Crater.class);
				crater.setX(mine.getX()).setY(mine.getY());
				tile.setElement(crater);

				world.removeEntity(mine);
				return;
			}
		}
	}
}