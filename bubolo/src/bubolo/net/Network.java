/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * The interface for the Network system.
 *
 * @author BU CS673 - Clone Productions
 */
public interface Network
{
	/**
	 * Returns true if this the game server.
	 * @return true if this the game server, or false otherwise.
	 */
	boolean isServer();

	/**
	 * Returns the name of the player.
	 * @return the name of the player.
	 */
	String getPlayerName();

	/**
	 * Identifies this player as the game server, and begins accepting connections from other
	 * players. There should only be one game server per game.
	 *
	 * @param serverName
	 *            the name of this server.
	 * @throws NetworkException
	 *             if a network error occurs.
	 * @throws IllegalStateException
	 *             if startServer or connect was already called.
	 */
	void startServer(String serverName) throws NetworkException, IllegalStateException;

	/**
	 * Attempts to connect to the specified IP address.
	 *
	 * @param serverIpAddress
	 *            the IP address of a server.
	 * @param clientName
	 *            the name of this client.
	 * @throws NetworkException
	 *             if a network error occurs.
	 * @throws IllegalStateException
	 *             if startServer or connect was already called.
	 */
	void connect(InetAddress serverIpAddress, String clientName)
			throws NetworkException, IllegalStateException;

	/**
	 * Starts the network system in debug mode. Use this to run unit tests and integration tests
	 * that don't rely on the network.
	 */
	void startDebug();

	/**
	 * Queues a network command to be sent to the other players.
	 *
	 * @param command
	 *            the network command to send.
	 */
	void send(NetworkCommand command);

	/**
	 * Performs all network system updates. This should be called once per game tick.
	 *
	 * @param worldOwner a game world owner.
	 */
	void update(WorldOwner worldOwner);

	/**
	 * Runs a NetworkCommand in the game logic thread.
	 *
	 * @param command the command to post to the game logic thread.
	 */
	void postToGameThread(NetworkCommand command);

	/**
	 * Notifies the clients that the game should start. This method is not needed by clients.
	 */
	void startGame();

	/**
	 * Adds an observer to the network observer list.
	 *
	 * @param observer
	 *            the observer to add.
	 */
	void addObserver(NetworkObserver observer);

	/**
	 * Removes an observer from the network observer list.
	 *
	 * @param observer
	 *            the observer to remove.
	 */
	void removeObserver(NetworkObserver observer);

	/**
	 * Returns a reference to the observer notifier.
	 *
	 * @return reference to the observer notifier.
	 */
	NetworkObserverNotifier getNotifier();

	/**
	 * Shuts down the network system.
	 */
	void dispose();

	record IpAddressInfo(InetAddress firstIpAddress, String ipAddresses) {
	}

	public static IpAddressInfo getIpAddresses() {
		InetAddress firstIpAddress = null;
		StringBuilder ipAddresses = new StringBuilder();
		var networkInterfaces = getNetworkInterfaces();
		for (NetworkInterface networkInterface : networkInterfaces) {
			var addresses = networkInterface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				var address = addresses.nextElement();
				if (address instanceof Inet4Address) {
					if (!ipAddresses.isEmpty()) {
						ipAddresses.append(", ");
					} else {
						firstIpAddress = address;
					}
					ipAddresses.append(address.getHostAddress());
				}
			}
		}
		return new IpAddressInfo(firstIpAddress, ipAddresses.toString());
	}

	/**
	 * Returns a list of network interfaces. Included interfaces have at least one IPv4 address associated with them,
	 * are not loopback or VirtualBox interfaces, and are up and running.
	 *
	 * @return a filtered list of network interfaces.
	 */
	public static List<NetworkInterface> getNetworkInterfaces() {
		List<NetworkInterface> validNetworkInterfaces = new ArrayList<>();

		try {
			var networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				var networkInterface = networkInterfaces.nextElement();
				// Filter out loopback and VirtualBox addresses.
				if (!networkInterface.isLoopback()
						&& networkInterface.isUp()
						&& !networkInterface.getDisplayName().contains("VirtualBox")) {
					var addresses = networkInterface.getInetAddresses();
					boolean hasIPv4Address = false;
					while (addresses.hasMoreElements() && !hasIPv4Address) {
						var address = addresses.nextElement();
						if (address instanceof Inet4Address) {
							// Only add network interfaces that have associated IPv4 addresses.
							validNetworkInterfaces.add(networkInterface);
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		}

		return validNetworkInterfaces;
	}
}
