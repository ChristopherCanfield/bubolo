package bubolo.controllers.ai;

import bubolo.controllers.Controller;
import bubolo.world.Entity;
import bubolo.world.EntityLifetimeObserver;
import bubolo.world.World;

/**
 * Slowly adds new forest tiles to the world.
 *
 * @author Christopher D. Canfield
 */
public class ForestGrowthController implements Controller, EntityLifetimeObserver {

	@Override
	public void onEntityAdded(Entity entity) {
	}

	@Override
	public void onEntityRemoved(Entity entity) {
	}

	@Override
	public void update(World world) {
	}

}
