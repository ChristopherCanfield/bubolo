package bubolo.net;

import java.net.InetAddress;

/**
 * A UDP multicast message from a game server that includes the server's local address, its name, and the map name
 * that it selected.
 *
 * @author Christopher D. Canfield
 */
public class ServerAddressMessage {
	private final InetAddress serverAddress;
	private final String serverName;
	private final String mapName;

	/**
	 * Constructs a ServerAddressMulticastMessage from a server address multicast message string, which has the following format:
	 * {@code serverAddress||serverName||mapName}
	 *
	 * @param message a server address multicast message string.
	 */
	ServerAddressMessage(String message) {
		// @TODO (cdc 2021-07-03): convert string in format serverAddress||serverName||mapName

		String[] messageComponents = message.split("||");
		assert messageComponents.length == 2;

		InetAddress address = InetAddress.getByName(messageComponents[0]);
	}

	public ServerAddressMessage(InetAddress serverAddress, String serverName, String mapName) {
		this.serverAddress = serverAddress;
		this.serverName = serverName;
		this.mapName = mapName;
	}

	public InetAddress serverAddress() {
		return serverAddress;
	}

	public String serverName() {
		return serverName;
	}

	public String mapName() {
		return mapName;
	}
}
