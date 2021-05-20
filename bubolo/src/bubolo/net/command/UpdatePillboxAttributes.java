/**
 *
 */

package bubolo.net.command;

import java.util.UUID;

import bubolo.net.NetworkCommand;
import bubolo.util.Nullable;
import bubolo.world.Pillbox;
import bubolo.world.Pillbox.BuildStatus;
import bubolo.world.Tank;
import bubolo.world.World;

/**
 * Updates a pillbox's attributes, including its owner.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class UpdatePillboxAttributes extends NetworkCommand {
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final float x;
	private final float y;
	private final float builtPct;
	private final BuildStatus buildStatus;
	private final @Nullable UUID ownerId;

	/**
	 * Constructs an Update Pillbox Attributes network command.
	 *
	 * @param pillbox the pillbox to update.
	 */
	public UpdatePillboxAttributes(Pillbox pillbox) {
		this.id = pillbox.id();
		this.x = pillbox.x();
		this.y = pillbox.y();
		this.builtPct = pillbox.builtPct();
		this.buildStatus = pillbox.buildStatus();
		this.ownerId = pillbox.hasOwner() ? pillbox.owner().id() : null;
	}

	@Override
	protected void execute(World world) {
		Pillbox pillbox = (Pillbox) world.getEntity(id);
		pillbox.setPosition(x, y);
		pillbox.setNetPillboxAttributes(buildStatus, builtPct);

		if (ownerId != null) {
			var owner = (Tank) world.getEntity(id);
			pillbox.setOwner(owner);
		}
	}
}
