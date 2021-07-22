package bubolo.net.command;

import bubolo.Systems;
import bubolo.net.NetworkCommand;

/**
 * Notifies a client that it has connected to the server.
 *
 * @author Christopher D. Canfield
 */
public class ConnectedToServer extends NetworkCommand {
	private static final long serialVersionUID = 4L;

	private final String clientName;
	private final String serverName;

	/**
	 * Constructs a ConnectedToServer object.
	 *
	 * @param clientName the name of the client that connected.
	 * @param serverName the name of the server player.
	 */
	public ConnectedToServer(String clientName, String serverName) {
		this.clientName = clientName;
		this.serverName = serverName;
	}

	@Override
	protected void execute() {
		Systems.network().getNotifier().notifyConnect(clientName, serverName);
	}

	/**
	 * Gets the name of the client.
	 *
	 * @return the name of the client.
	 */
	String getClientName() {
		return clientName;
	}

	/**
	 * Gets the name of the server.
	 *
	 * @return the name of the server.
	 */
	String getServerName() {
		return serverName;
	}
}
