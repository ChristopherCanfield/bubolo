package bubolo.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;

/**
 * A UDP multicast message from a game server that includes the server's local address, its name, and the map name
 * that it selected.
 *
 * @author Christopher D. Canfield
 */
public class ServerAddressMessage {
	public static final int MaxSizeBytes = 256;

	private final InetAddress serverAddress;
	private final String serverName;
	private final String mapName;

	private static final String separator = "¦~¦";

	/**
	 * Constructs a ServerAddressMulticastMessage from a server address multicast message string, which has the following format:
	 * {@code serverAddress¦~¦serverName¦~¦mapName}
	 *
	 * @param message a server address multicast message string.
	 */
	ServerAddressMessage(String message) {
		String[] messageComponents = message.split(Pattern.quote(separator));
		assert messageComponents.length == 3;

		try {
			serverAddress = InetAddress.getByName(messageComponents[0]);
			serverName = messageComponents[1];
			mapName = messageComponents[2];
		} catch (UnknownHostException e) {
			throw new NetworkException(e);
		}
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

	public byte[] toBytes() {
		return new String(serverAddress.getHostAddress() + separator + serverName + separator + mapName).getBytes(Charsets.UTF_8);
	}

	@Override
	public int hashCode() {
		return serverAddress.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ServerAddressMessage message) {
			return serverAddress.equals(message.serverAddress);
		} else {
			return false;
		}
	}
}
