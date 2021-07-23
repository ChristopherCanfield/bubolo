package bubolo.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import bubolo.Systems;
import bubolo.net.command.ClientConnected;

/**
 * A game client. A network game can have multiple clients, but only one server.
 *
 * @author Christopher D. Canfield
 */
class Client implements NetworkSubsystem, Runnable {
	private Socket server;

	// Specifies whether the network system has shut down.
	private final AtomicBoolean shutdown = new AtomicBoolean(false);

	private final Executor sender;

	// Reference to the network system.
	private final Network network;

	// The name of this player.
	private String playerName;

	private ObjectOutputStream serverStream;

	/**
	 * Constructs a Client object.
	 *
	 * @param network reference to the network.
	 */
	Client(Network network) {
		this.network = network;
		this.sender = Executors.newSingleThreadExecutor();
	}

	/**
	 * Attempts to connect to the specified IP address.
	 *
	 * @param serverIpAddress the IP address of a server. Note that this isn't necessarily the <i>game</i> server, since clients
	 *     also connect directly to each other.
	 * @param clientName the name of this client.
	 * @throws NetworkException if a network error occurs.
	 */
	void connect(InetAddress serverIpAddress, String clientName) throws NetworkException {
		try {
			playerName = clientName;

			server = new Socket(serverIpAddress, NetworkInformation.GAME_PORT);
			server.setTcpNoDelay(true);

			serverStream = new ObjectOutputStream(server.getOutputStream());
			send(new ClientConnected(playerName));

			// Start the network reader thread.
			Thread thread = new Thread(this, "net-client");
			thread.setDaemon(true);
			thread.start();
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	@Override
	public void send(NetworkCommand command) {
		sender.execute(new NetworkSender(serverStream, command));
	}

	@Override
	public void dispose() {
		shutdown.set(true);
	}

	@Override
	public void run() {
		if (server == null) {
			throw new IllegalStateException("Unable to run client; the network system has not been started.");
		}

		try (ObjectInputStream inputStream = new ObjectInputStream(server.getInputStream())) {
			while (!shutdown.get()) {
				NetworkCommand command = (NetworkCommand) inputStream.readObject();
				network.postToGameThread(command);
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				network.postToGameThread(new NetworkCommand() {
					private static final long serialVersionUID = 1346400800725660320L;

					@Override
					public void execute() {
						Systems.messenger().notifyPlayerDisconnected("You have");
					}
				});

				server.close();
			} catch (IOException e) {
			}
		}
	}
}
