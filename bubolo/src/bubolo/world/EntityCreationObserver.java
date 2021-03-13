package bubolo.world;

import bubolo.world.entity.Entity;

/**
 * An observer that is notified by the world when an entity is created.
 *
 * @author Christopher D. Canfield
 */
public interface EntityCreationObserver {
	void onEntityCreated(Entity entity);
}
