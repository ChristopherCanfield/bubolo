package bubolo.net.command;

import bubolo.GameApplication;
import bubolo.net.NetworkApplicationCommand;
import bubolo.net.NetworkObserverNotifier;

/**
 * Notifies players that a new client has connected.
 *
 * @author Christopher D. Canfield
 */
public class ClientConnected implements NetworkApplicationCommand {
	private static final long serialVersionUID = 2L;

	private final String playerName;

	/**
	 * Constructs a ClientConnected object.
	 *
	 * @param playerName the name of the player that connected.
	 */
	public ClientConnected(String playerName) {
		this.playerName = playerName;
	}

	/**
	 * Returns the name of the client.
	 *
	 * @return the name of the client.
	 */
	public String getClientName() {
		return playerName;
	}

	@Override
	public void execute(GameApplication app, NetworkObserverNotifier notifier) {
		notifier.notifyClientConnected(playerName);
	}
}
