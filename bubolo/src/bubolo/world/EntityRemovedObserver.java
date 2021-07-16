package bubolo.world;

/**
 * An observer that is notified when the observed entity is removed from the world.
 *
 * @author Christopher D. Canfield
 */
public interface EntityRemovedObserver {

	/**
	 * Called when an entity is removed from the world.
	 *
	 * @param entity the entity that was removed.
	 */
	void onEntityRemoved(Entity entity);
}
