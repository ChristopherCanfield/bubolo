package bubolo.net;

import static com.google.common.base.Preconditions.checkState;

import java.net.InetAddress;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import bubolo.world.Spawn;

/**
 * The network system implementation.
 *
 * @author Christopher D. Canfield
 */
public class NetworkSystem implements Network {
	private NetworkSubsystem subsystem;

	// Queue of commands that should be run in the game logic thread.
	private final Queue<NetworkCommand> postedCommands;

	private final NetworkObserverNotifier observerNotifier;

	// Specifies whether the network system is running in debug mode.
	private boolean debug = false;

	// The name of the player, which is used when sending messages.
	private String name;

	// Specifies whether this is a server player.
	private boolean isServer;

	private static volatile Network instance;

	/**
	 * Returns the network system instance.
	 *
	 * @return the network system instance.
	 */
	public static synchronized Network getInstance() {
		if (instance == null) {
			instance = new NetworkSystem();
		}
		return instance;
	}

	private NetworkSystem() {
		this.postedCommands = new ConcurrentLinkedQueue<NetworkCommand>();
		this.observerNotifier = new NetworkObserverNotifier();
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

		// Don't allow the server to run in debug mode, since it requires external resources.
		// Instead, test this properly in an integration test.
		if (debug) {
			return;
		}

		Server server = new Server(this);
		subsystem = server;
		server.startServer(serverName);
	}

	@Override
	public void connect(InetAddress serverIpAddress, String clientName) throws NetworkException, IllegalStateException {
		checkState(subsystem == null,
				"The network system has already been started. " + "Do not call startServer or connect more than once.");

		name = clientName;
		isServer = false;

		// Don't allow the client to run in debug mode, since it requires external resources.
		// Instead, test this properly in an integration test.
		if (debug) {
			return;
		}

		Client client = new Client(this);
		subsystem = client;
		client.connect(serverIpAddress, clientName);
	}

	@Override
	public void startDebug() {
		debug = true;
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
		// Return without sending the command if the system is running in debug mode.
		if (debug) {
			return;
		}

		checkState(subsystem != null);
		subsystem.send(command);
	}

	@Override
	public void sendToClient(int playerIndex, NetworkCommand command) {
		// Return without sending the command if the system is running in debug mode.
		if (debug) {
			return;
		}

		checkState(subsystem != null);
		checkState(subsystem instanceof Server);

		Server server = (Server) subsystem;
		server.sendToClient(playerIndex, command);
	}

	@Override
	public void update(WorldOwner worldOwner) {
		// Execute all posted commands in the game logic thread.
		NetworkCommand c = null;
		while ((c = postedCommands.poll()) != null) {
			c.execute(worldOwner);
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
	public NetworkObserverNotifier getNotifier() {
		return observerNotifier;
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
		debug = false;
		isServer = false;
		name = null;
		postedCommands.clear();
	}
}
