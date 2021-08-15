package bubolo.net.command;

import java.util.UUID;

import bubolo.Systems;
import bubolo.world.Player;
import bubolo.world.Tank;
import bubolo.world.World;

public class RejectAllianceRequest extends ToPlayerNetworkGameCommand {
	private static final long serialVersionUID = 1L;

	private final UUID rejecterId;

	public RejectAllianceRequest(UUID requesterId, UUID rejecterId) {
		super(requesterId);

		assert rejecterId != null;
		this.rejecterId = rejecterId;
	}

	@Override
	protected void onExecute(World world, Tank localTank) {
		Player rejectingPlayer = ((Tank) world.getEntity(rejecterId)).getPlayer();
		localTank.getPlayer().removeAllianceRequest(null);

		Systems.messenger().notifyAllianceRequestRejected(localTank.getPlayer(), rejectingPlayer);
	}
}
