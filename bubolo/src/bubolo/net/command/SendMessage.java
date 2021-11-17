package bubolo.net.command;

import bubolo.GameApplication;
import bubolo.Systems;
import bubolo.net.NetworkApplicationCommand;
import bubolo.net.NetworkObserverNotifier;

/**
 * Used to send a message across the network.
 *
 * @author Christopher D. Canfield
 */
public class SendMessage implements NetworkApplicationCommand {
	private static final long serialVersionUID = 1L;

	public enum MessageType {
		LobbyMessageHistory, Message;
	}

	private final MessageType messageType;
	// The message that will be sent.
	private final String message;

	/**
	 * Constructs a SendMessage network command.
	 *
	 * @param messageType the type of message to send.
	 * @param message the message to send.
	 */
	public SendMessage(MessageType messageType, String message) {
		this.messageType = messageType;
		if (messageType == MessageType.Message) {
			this.message = Systems.network().getPlayerName() + ": " + message;
		} else {
			this.message = message;
		}
	}

	/**
	 * Constructs a SendMessage network command with the message type set to Message.
	 *
	 * @param message the message to send.
	 */
	public SendMessage(String message) {
		this(MessageType.Message, message);
	}

	@Override
	public void execute(GameApplication app, NetworkObserverNotifier notifier) {
		notifier.notifyMessageReceived(messageType, message);
	}

	/**
	 * Gets the message.
	 *
	 * @return the message.
	 */
	String getMessage() {
		return message;
	}
}
