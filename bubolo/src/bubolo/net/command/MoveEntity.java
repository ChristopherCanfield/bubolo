package bubolo.net.command;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import bubolo.Config;
import bubolo.net.NetworkCommand;
import bubolo.util.GameLogicException;
import bubolo.world.ActorEntity;
import bubolo.world.World;

/**
 * Moves an entity in the world.
 *
 * @author Christopher D. Canfield
 */
public class MoveEntity extends NetworkCommand {
	private static final long serialVersionUID = 1L;

	private final UUID id;

	private final float x;
	private final float y;

	private final float rotation;

	/**
	 * Constructs a MoveEntity object.
	 *
	 * @param entity the entity to move.
	 */
	public MoveEntity(ActorEntity entity) {
		this.id = entity.id();
		this.x = entity.x();
		this.y = entity.y();
		this.rotation = entity.rotation();
	}

	@Override
	protected void execute(World world) {
		try {
			ActorEntity entity = (ActorEntity) world.getEntity(id);
			entity.setPosition(x, y).setRotation(rotation);
		} catch (GameLogicException e) {
			Logger.getLogger(Config.AppProgramaticTitle).log(Level.WARNING,
					"MoveEntity net command: Unable to find entity " + id);
		}
	}
}
