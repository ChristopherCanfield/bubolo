package bubolo.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Logger;

import bubolo.Config;

/**
 * Sends NetworkCommand or NetworkGameCommand objects to connected players.
 *
 * @author Christopher D. Canfield
 */
class NetworkSender implements Runnable {
	private final ObjectOutputStream stream;
	private final Serializable command;

	/**
	 * Constructs a NetworkSender.
	 *
	 * @param stream the object output stream to the client.
	 * @param command the command to send.
	 */
	NetworkSender(ObjectOutputStream stream, Serializable command) {
		assert command instanceof NetworkCommand || command instanceof NetworkGameCommand;
		this.stream = stream;
		this.command = command;
	}

	@Override
	public void run() {
		try {
			synchronized (stream) {
				stream.writeObject(command);
			}
		} catch (IOException e) {
			Logger.getLogger(Config.AppProgramaticTitle)
					.severe("Exception in " + NetworkSender.class.getName() + ": " + e.toString());
			e.printStackTrace();
			throw new NetworkException(e);
		}
	}
}
