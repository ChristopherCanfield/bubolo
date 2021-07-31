package bubolo.net;

import static com.google.common.base.Preconditions.checkState;

import java.net.InetAddress;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import bubolo.GameApplication;
import bubolo.world.Spawn;

/**
 * The network system implementation.
 *
 * @author Christopher D. Canfield
 */
public class NetworkSystem implements Network {
	private NetworkSubsystem subsystem;

	// Queue of commands that should be run in the game thread.
	private final Queue<NetworkCommand> postedCommands = new ConcurrentLinkedQueue<>();

	private final NetworkObserverNotifier observerNotifier = new NetworkObserverNotifier();

	// The name of the player, which is used when sending messages.
	private String name;

	// Specifies whether this is a server player.
	private boolean isServer;

	public NetworkSystem() {
	}

	@Override
	public boolean isServer() {
		return isServer;
	}

	@Override
	public String getPlayerName() {
		return name;
	}

	@Override
	public void startServer(String serverName) throws NetworkException, IllegalStateException {
		checkState(subsystem == null,
				"The network system has already been started. " + "Do not call startServer or connect more than once.");

		name = serverName;
		isServer = true;

		Server server = new Server(this, observerNotifier);
		subsystem = server;
		server.startServer(serverName);
	}

	@Override
	public void connect(InetAddress serverIpAddress, String clientName) throws NetworkException, IllegalStateException {
		checkState(subsystem == null,
				"The network system has already been started. " + "Do not call startServer or connect more than once.");

		name = clientName;
		isServer = false;

		Client client = new Client(this);
		subsystem = client;
		client.connect(serverIpAddress, clientName);
	}

	/**
	 * Notifies clients that the game is ready to start. This should not be called until the map data has been sent.
	 *
	 * @param initialSpawnPositions the list of initial spawn positions. Must be the same size as the player count.
	 */
	@Override
	public void startGame(List<Spawn> initialSpawnPositions) {
		if (subsystem instanceof Server server) {
			server.startGame(initialSpawnPositions);
		}
	}

	@Override
	public void send(NetworkCommand command) {
		subsystem.send(command);
	}

	@Override
	public void sendToClient(int playerIndex, NetworkCommand command) {
		checkState(subsystem != null);
		checkState(subsystem instanceof Server);

		Server server = (Server) subsystem;
		server.sendToClient(playerIndex, command);
	}

	@Override
	public void update(GameApplication app) {
		// Execute all posted commands in the game logic thread.
		NetworkCommand c = null;
		while ((c = postedCommands.poll()) != null) {
			if (c instanceof NetworkGameCommand gameCommand) {
				gameCommand.execute(app.world());
			} else if (c instanceof NetworkApplicationCommand appCommand) {
				appCommand.execute(app, observerNotifier);
			} else {
				throw new NetworkException("Unknown NetworkCommand received: " + c.getClass().toString());
			}
		}
	}

	@Override
	public void addObserver(NetworkObserver o) {
		observerNotifier.addObserver(o);
	}

	@Override
	public void removeObserver(NetworkObserver o) {
		observerNotifier.removeObserver(o);
	}

	@Override
	public void postToGameThread(NetworkCommand command) {
		postedCommands.add(command);
	}

	@Override
	public void dispose() {
		if (subsystem != null) {
			subsystem.dispose();
		}
		subsystem = null;
		isServer = false;
		name = null;
		postedCommands.clear();
	}
}
