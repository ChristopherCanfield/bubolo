/**
 *
 */

package bubolo;

import bubolo.util.Nullable;
import bubolo.world.World;

/**
 * Abstract base class for Game Applications.
 *
 * @author BU CS673 - Clone Productions
 */
public abstract class AbstractGameApplication implements GameApplication {
	private boolean ready;

	/** The game world. **/
	private World world;

	private State state;

	/**
	 * Constructs an AbstractGameApplication;
	 */
	protected AbstractGameApplication() {
		this.state = State.MainMenu;
	}

	@Override
	public final boolean isReady() {
		return ready;
	}

	/**
	 * Sets whether the game is ready.
	 *
	 * @param value true if the game is ready.
	 */
	protected void setReady(boolean value) {
		ready = value;
	}

	@Override
	public World world() {
		return world;
	}

	@Override
	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public void setState(State state) {
		setState(state, null);
	}

	@Override
	public void setState(State state, @Nullable Object arg) {
		var previousState = this.state;
		this.state = state;
		onStateChanged(previousState, state, arg);
	}

	@Override
	public State getState() {
		return state;
	}

	/**
	 * Called when the application's state is changed.
	 *
	 * @param previousState the previous application state.
	 * @param newState the new state that the application has entered.
	 * @param arg [optional] an optional additional argument. May be null.
	 */
	protected void onStateChanged(State previousState, State newState, @Nullable Object arg) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public boolean isGameStarted() {
		return (isReady() && world != null);
	}
}
