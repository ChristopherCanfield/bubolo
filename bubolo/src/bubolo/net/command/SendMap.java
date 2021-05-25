/**
 *
 */

package bubolo.net.command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bubolo.net.NetworkCommand;
import bubolo.net.WorldOwner;
import bubolo.world.Entity;
import bubolo.world.GameWorld;
import bubolo.world.World;

/**
 * Network command that is used to send basic information, including type, position, and rotation, of all entities in
 * the world to other players.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class SendMap extends NetworkCommand {
	private static final long serialVersionUID = 1L;

	private final List<EntitySerializationData> entities = new ArrayList<>();

	private final byte rows;
	private final byte columns;

	/**
	 * Constructs a Send Map network command.
	 *
	 * @param world the game world, after all map entities have been added.
	 */
	public SendMap(World world) {
		this.rows = (byte) world.getTileRows();
		this.columns = (byte) world.getTileColumns();

		var worldEntities = world.getEntities();
		assert !worldEntities.isEmpty() : "Empty world passed to SendMap network command.";

		for (Entity e : worldEntities) {
			var serializationData = new EntitySerializationData(e.getClass(), e.id(), e.x(), e.y(), e.rotation());
			entities.add(serializationData);
		}
	}

	@Override
	public void execute(WorldOwner worldOwner) {
		World world = new GameWorld(Byte.toUnsignedInt(columns), Byte.toUnsignedInt(rows));
		worldOwner.setWorld(world);

		for (var entityData : entities) {
			var args = new Entity.ConstructionArgs(entityData.id(), entityData.x(), entityData.y(),
					entityData.rotation());
			world.addEntity(entityData.type(), args);
		}

		// Process a game tick, which finalizes the addition of the new entities to the world.
		world.update();
	}

	// Minimal data record for sending map data to remote players.
	private static record EntitySerializationData(Class<? extends Entity> type, UUID id, float x, float y,
			float rotation) implements Serializable {
	}
}
