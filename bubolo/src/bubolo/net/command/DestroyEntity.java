package bubolo.net.command;

import java.util.UUID;

import bubolo.net.NetworkCommand;
import bubolo.world.Damageable;
import bubolo.world.World;

/**
 * Notifies other players that an entity should be destroyed. This is to ensure that the world state is synchronized between all
 * players. Without this, in rare instances objects could be destroyed on one machine, but not on another. This was only observed
 * when mine explosions damaged walls.
 *
 * @author Christopher D. Canfield
 */
public class DestroyEntity extends NetworkCommand {
	private static final long serialVersionUID = 1L;

	private final UUID id;

	public DestroyEntity(UUID id) {
		this.id = id;
	}

	@Override
	protected void execute(World world) {
		var entityToDestroy = world.getEntityOrNull(id);
		if (entityToDestroy != null) {
			if (entityToDestroy instanceof Damageable damageable) {
				damageable.receiveDamage(world, damageable.maxHitPoints(), null);
			}

			entityToDestroy.dispose();
		}
	}
}
