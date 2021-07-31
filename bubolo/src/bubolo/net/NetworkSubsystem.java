package bubolo.net;

/**
 * @author Christopher D. Canfield
 */
public interface NetworkSubsystem {
	/**
	 * Queues a network command to be sent to connected players.
	 *
	 * @param command the network command to send.
	 */
	void send(NetworkCommand command);

	/**
	 * Shuts down the system.
	 */
	void dispose();
}
