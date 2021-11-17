package bubolo.net.command;

import java.util.UUID;

import bubolo.Systems;
import bubolo.world.Tank;
import bubolo.world.World;

public class RequestAlliance extends ToPlayerNetworkGameCommand {
	private static final long serialVersionUID = 1L;

	private final UUID requesterId;

	public RequestAlliance(UUID targetPlayerId, UUID requesterId) {
		super(targetPlayerId);
		this.requesterId = requesterId;
	}

	@Override
	protected void onExecute(World world, Tank targetTank) {
		var requestingPlayer = ((Tank) world.getEntity(requesterId)).getPlayer();
		targetTank.getPlayer().addAllianceRequest(requestingPlayer);

		Systems.messenger().notifyAllianceRequestReceived(requestingPlayer.name());
	}
}
