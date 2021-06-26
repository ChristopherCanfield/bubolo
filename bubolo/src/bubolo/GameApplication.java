package bubolo;

import com.badlogic.gdx.ApplicationListener;

import bubolo.net.WorldOwner;

/**
 * Defines the interface for the main game class.
 *
 * @author Christopher D. Canfield
 */
public interface GameApplication extends ApplicationListener, WorldOwner {
	/**
	 * The application's state.
	 *
	 * @author BU CS673 - Clone Productions
	 */
	public enum State {
		/** The main menu state. **/
		MainMenu,

		Settings,

		MultiplayerMapSelection,
		MultiplayerSetupServer,

		/** The enter player information state. **/
		MultiplayerSetupClient,

		/** The game lobby state. **/
		MultiplayerLobby,

		/** The game is starting. **/
		MultiplayerStarting,

		/** The multiplayer game state. **/
		MultiplayerGame,

		SinglePlayerSetup,
		SinglePlayerLoading,

		/** The single player game state. **/
		SinglePlayerGame,
	}

	/**
	 * Returns true if the game's subsystems have been set up, or false otherwise.
	 *
	 * @return true if the game's subsystems have been set up.
	 */
	boolean isReady();

	/**
	 * Specifies whether the game has started.
	 *
	 * @return true if the game has started.
	 */
	boolean isGameStarted();

	/**
	 * Sets the application's state.
	 *
	 * @param state the application's state.
	 */
	void setState(State state);

	/**
	 * Gets the application's state.
	 *
	 * @return the application's state.
	 */
	State getState();
}
