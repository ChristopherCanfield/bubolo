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

import bubolo.world.Spawn;

/**
 * The interface for the Network system.
 *
 * @author Christopher D. Canfield
 */
public interface Network {
	/**
	 * Returns true if this the game server.
	 *
	 * @return true if this the game server, or false otherwise.
	 */
	boolean isServer();

	/**
	 * Returns the name of the local player.
	 *
	 * @return the name of the local player.
	 */
	String getPlayerName();

	/**
	 * Identifies this player as the game server, and begins accepting connections from other players. There should only be one game
	 * server per game.
	 *
	 * @param serverName the name of this server.
	 * @throws NetworkException if a network error occurs.
	 * @throws IllegalStateException if startServer or connect was already called.
	 */
	void startServer(String serverName) throws NetworkException, IllegalStateException;

	/**
	 * Attempts to connect to the specified IP address.
	 *
	 * @param serverIpAddress the IP address of a server.
	 * @param clientName the name of this client.
	 * @throws NetworkException if a network error occurs.
	 * @throws IllegalStateException if startServer or connect was already called.
	 */
	void connect(InetAddress serverIpAddress, String clientName) throws NetworkException, IllegalStateException;

	/**
	 * Queues a network command to be sent to the other players.
	 *
	 * @param command the network command to send.
	 */
	void send(NetworkCommand command);

	/**
	 * Queues a network command to be sent to a specific player. Only the server should call this method.
	 *
	 * @param playerIndex the player's network index. Network indexes are assigned to players in the order that they joined,
	 * with the first player receiving index zero.
	 * @param command the command to send.
	 */
	void sendToClient(int playerIndex, NetworkCommand command);

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
	 * Notifies clients that the game is ready to start. This should not be called until the map data has been sent. This
	 * method is only used by the server (host).
	 *
	 * @param initialSpawnPositions the list of initial spawn positions. Must be the same size as the player count.
	 */
	void startGame(List<Spawn> initialSpawnPositions);

	/**
	 * Adds an observer to the network observer list.
	 *
	 * @param observer the observer to add.
	 */
	void addObserver(NetworkObserver observer);

	/**
	 * Removes an observer from the network observer list.
	 *
	 * @param observer the observer to remove.
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
	 * Returns a list of network interfaces. Included interfaces have at least one IPv4 address associated with them, are not loopback
	 * or VirtualBox interfaces, and are up and running.
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
				if (!networkInterface.isLoopback() && networkInterface.isUp()
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
