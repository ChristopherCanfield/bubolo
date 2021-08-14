package bubolo.net.command;

import java.util.UUID;

import bubolo.Systems;
import bubolo.world.Tank;
import bubolo.world.World;

public class RequestAlliance extends ToPlayerNetworkGameCommand {

	private final UUID requesterId;
	private final String requesterName;

	public RequestAlliance(UUID targetPlayerId, UUID requesterId, String requesterName) {
		super(targetPlayerId);
		this.requesterId = requesterId;
		this.requesterName = requesterName;
	}

	@Override
	protected void onExecute(World world, Tank targetTank) {
		Systems.messenger().notifyAllianceRequestReceived(targetTank.getPlayer(), requesterId, requesterName);
	}
}
