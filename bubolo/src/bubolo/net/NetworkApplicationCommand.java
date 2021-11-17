package bubolo.net;

import bubolo.GameApplication;

/**
 * A command that will be sent across the network to other users. {@code NetworkCommands} are intended for actions that don't
 * interact with the game world. Actions that do interact with the game world should instead implement the {@code NetworkGameCommand}
 * interface.
 *
 * @author Christopher D. Canfield
 */
public interface NetworkApplicationCommand extends NetworkCommand {

	/**
	 * Called when this NetworkCommand reaches another player. This method is called on the main thread.
	 *
	 * @param app reference to the application.
	 * @param notifier reference to the network observer notifier.
	 */
	void execute(GameApplication app, NetworkObserverNotifier notifier);
}
