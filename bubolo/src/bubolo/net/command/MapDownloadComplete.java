package bubolo.net.command;

import bubolo.Systems;
import bubolo.net.NetworkCommand;

/**
 * Notifies the server that a client has completed downloaded the map.
 *
 * @author Christopher D. Canfield
 */
public class MapDownloadComplete extends NetworkCommand {
	private static final long serialVersionUID = 1L;

	private final String playerName;

	public MapDownloadComplete(String playerName) {
		this.playerName = playerName;
	}

	@Override
	protected void execute() {
		Systems.network().getNotifier().notifyClientReady(playerName);
	}
}
