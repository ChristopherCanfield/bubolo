package bubolo.net;

import java.io.Serializable;

/**
 * A command that will be sent across the network to other users. The NetworkCommand interface should not be used directly.
 * Instead, use {@code NetworkApplicationCommand} for commands that interact with the application, but not the game logic,
 * or {@code NetworkGameCommand} for commands that interact with the game world.
 *
 * @author Christopher D. Canfield
 */
public interface NetworkCommand extends Serializable {
}
