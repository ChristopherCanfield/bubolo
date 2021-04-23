package bubolo.world;

/**
 * An observer that is notified by the world when an entity is created or destroyed. EntityLifetimeObservers
 * are added to the world using the {@code world.addEntityLifetimeObserver} method.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public interface EntityLifetimeObserver {
	/**
	 * Called when an entity is added to the world.
	 *
	 * @param entity the entity that was added.
	 */
	void onEntityAdded(Entity entity);

	/**
	 * Called when an entity is removed from the world.
	 *
	 * @param entity the entity that was removed.
	 */
	void onEntityRemoved(Entity entity);
}
