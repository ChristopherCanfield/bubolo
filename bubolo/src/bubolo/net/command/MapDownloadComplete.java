package bubolo.net.command;

import bubolo.GameApplication;
import bubolo.net.NetworkApplicationCommand;
import bubolo.net.NetworkObserverNotifier;

/**
 * Notifies the server that a client has completed downloaded the map.
 *
 * @author Christopher D. Canfield
 */
public class MapDownloadComplete implements NetworkApplicationCommand {
	private static final long serialVersionUID = 1L;

	private final String playerName;

	public MapDownloadComplete(String playerName) {
		this.playerName = playerName;
	}

	@Override
	public void execute(GameApplication app, NetworkObserverNotifier notifier) {
		notifier.notifyClientReady(playerName);
	}
}
