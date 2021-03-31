package bubolo.world;

/**
 * An observer that is notified by the world when an entity is created.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public interface EntityCreationObserver {
	void onEntityCreated(Entity entity);
}
