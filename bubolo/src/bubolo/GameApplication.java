package bubolo;

import com.badlogic.gdx.ApplicationListener;

import bubolo.net.WorldOwner;

/**
 * Defines the interface for the main game class.
 * @author BU CS673 - Clone Productions
 */
public interface GameApplication extends ApplicationListener, WorldOwner
{
	/**
	 * The application's state.
	 * @author BU CS673 - Clone Productions
	 */
	public enum State
	{
		/** The main menu state. **/
		MAIN_MENU,

		/** The enter player information state. **/
		NET_GAME_SETUP,

		/** The game lobby state. **/
		NET_GAME_LOBBY,

		/** The game is starting. **/
		NET_GAME_STARTING,

		/** The single player game state. **/
		LOCAL_GAME,

		/** The multiplayer game state. **/
		NET_GAME
	}

	/**
	 * Returns true if the game's subsystems have been set up, or false otherwise.
	 * @return true if the game's subsystems have been set up.
	 */
	boolean isReady();

	/**
	 * Specifies whether the game has started.
	 * @return true if the game has started.
	 */
	boolean isGameStarted();

	/**
	 * Sets the application's state.
	 * @param state the application's state.
	 */
	void setState(State state);

	/**
	 * Gets the application's state.
	 * @return the application's state.
	 */
	State getState();
}
