package bubolo.net;

import java.util.ArrayList;
import java.util.List;

import bubolo.net.command.SendMessage.MessageType;

/**
 * @author Christopher D. Canfield
 */
public class NetworkObserverNotifier {
	// The list of network observers.
	private final List<NetworkObserver> observers;

	/**
	 * Constructs a NetworkObserverNotifier.
	 */
	NetworkObserverNotifier() {
		this.observers = new ArrayList<NetworkObserver>();
	}

	/**
	 * Adds an observer to the network observer list.
	 *
	 * @param o the observer to add.
	 */
	void addObserver(NetworkObserver o) {
		if (!observers.contains(o)) {
			observers.add(o);
		}
	}

	/**
	 * Removes an observer from the network observer list.
	 *
	 * @param o the observer to remove.
	 */
	void removeObserver(NetworkObserver o) {
		observers.remove(o);
	}

	/**
	 * Gets the observer count.
	 *
	 * @return the observer count.
	 */
	int getObserverCount() {
		return observers.size();
	}

	/**
	 * Notifies observers that this client has connected to a server.
	 *
	 * @param clientName the name of the client that connected.
	 * @param serverName the name of the server.
	 */
	public void notifyConnect(String clientName, String serverName) {
		for (final NetworkObserver o : observers) {
			o.onConnect(clientName, serverName);
		}
	}

	/**
	 * Notifies observers that a client has connected to this server.
	 *
	 * @param clientName the name of the client that connected.
	 */
	public void notifyClientConnected(String clientName) {
		for (final NetworkObserver o : observers) {
			o.onClientConnected(clientName);
		}
	}

	/**
	 * Notifies observers that a client has disconnected.
	 *
	 * @param clientName the name of the client that disconnected.
	 */
	public void notifyClientDisconnected(String clientName) {
		for (final NetworkObserver o : observers) {
			o.onClientDisconnected(clientName);
		}
	}

	public void notifyClientReady(String clientName) {
		for (final NetworkObserver o : observers) {
			o.onClientReady(clientName);
		}
	}

	/**
	 * Notifies observers that the game is starting.
	 *
	 * @param secondsUntilStart the number of seconds until the game begins.
	 */
	public void notifyGameStart(final int secondsUntilStart) {
		for (final NetworkObserver o : observers) {
			o.onGameStart(secondsUntilStart);
		}
	}

	/**
	 * Notifies observers that a message has been received.
	 *
	 * @param messageType the type of the received message.
	 * @param message the message text.
	 */
	public void notifyMessageReceived(MessageType messageType, String message) {
		for (final NetworkObserver o : observers) {
			o.onMessageReceived(messageType, message);
		}
	}
}
