package bubolo.net;

import java.net.InetAddress;
import java.util.List;

import bubolo.GameApplication;
import bubolo.world.Spawn;

public class NullNetwork implements Network {
	private String playerName;

	@Override
	public boolean isServer() {
		return true;
	}

	@Override
	public String getPlayerName() {
		return playerName;
	}

	@Override
	public void startServer(String serverName) throws NetworkException, IllegalStateException {
		this.playerName = serverName;
	}

	@Override
	public void connect(InetAddress serverIpAddress, String clientName) throws NetworkException, IllegalStateException {
	}

	@Override
	public void send(NetworkCommand command) {
	}

	@Override
	public void sendToClient(int playerIndex, NetworkCommand command) {
	}

	@Override
	public void update(GameApplication app) {
	}

	@Override
	public void postToGameThread(NetworkCommand command) {
	}

	@Override
	public void startGame(List<Spawn> initialSpawnPositions) {
	}

	@Override
	public void addObserver(NetworkObserver observer) {
	}

	@Override
	public void removeObserver(NetworkObserver observer) {
	}

	@Override
	public void dispose() {
	}
}
