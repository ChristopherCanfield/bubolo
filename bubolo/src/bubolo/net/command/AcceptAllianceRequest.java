package bubolo.net.command;

import java.util.UUID;

import bubolo.Systems;
import bubolo.net.NetworkGameCommand;
import bubolo.world.Tank;
import bubolo.world.World;

public class AcceptAllianceRequest implements NetworkGameCommand {
	private static final long serialVersionUID = 1L;

	private final UUID requesterId;
	private final UUID accepterId;

	public AcceptAllianceRequest(UUID requesterId, UUID accepterId) {
		assert requesterId != null;
		assert accepterId != null;

		this.requesterId = requesterId;
		this.accepterId = accepterId;
	}

	@Override
	public void execute(World world) {
		Tank requester = (Tank) world.getEntity(requesterId);
		Tank accepter = (Tank) world.getEntity(accepterId);

		requester.addAlly(accepter);
		accepter.addAlly(requester);

		if (!accepter.isOwnedByLocalPlayer()) {
			Systems.messenger().notifyAllianceRequestAccepted(requester.getPlayer(), accepter.getPlayer());
		}
	}
}
