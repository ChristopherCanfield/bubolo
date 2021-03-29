package bubolo.world;

import bubolo.world.entity.OldEntity;

/**
 * An observer that is notified by the world when an entity is created.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public interface EntityCreationObserver {
	void onEntityCreated(OldEntity entity);
}
