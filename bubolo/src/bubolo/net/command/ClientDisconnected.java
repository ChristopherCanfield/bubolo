package bubolo.net.command;

import bubolo.GameApplication;
import bubolo.Systems;
import bubolo.net.NetworkApplicationCommand;
import bubolo.net.NetworkObserverNotifier;
import bubolo.util.Nullable;

/**
 * Used when a client has disconnected.
 *
 * @author Christopher D. Canfield
 */
public class ClientDisconnected implements NetworkApplicationCommand {
	private static final long serialVersionUID = 1L;

	private final String clientName;

	/**
	 * @param clientName the client's player name. May be null.
	 */
	public ClientDisconnected(@Nullable String clientName) {
		this.clientName = clientName;
	}

	@Override
	public void execute(GameApplication app, NetworkObserverNotifier notifier) {
		Systems.messenger().notifyPlayerDisconnected(clientName);
		notifier.notifyClientDisconnected(clientName);
	}
}
