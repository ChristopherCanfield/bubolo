package bubolo.net.command;

import bubolo.Systems;
import bubolo.net.NetworkCommand;
import bubolo.util.Nullable;

/**
 * Used when a client has disconnected.
 *
 * @author Christopher D. Canfield
 */
public class ClientDisconnected extends NetworkCommand {
	private static final long serialVersionUID = 1L;

	private final String clientName;

	/**
	 * @param clientName the client's player name. May be null.
	 */
	public ClientDisconnected(@Nullable String clientName) {
		this.clientName = clientName;
	}

	@Override
	public void execute() {
		Systems.messenger().notifyPlayerDisconnected(clientName);
		Systems.network().getNotifier().notifyClientDisconnected(clientName);
	}
}
