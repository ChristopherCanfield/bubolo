/**
 *
 */

package bubolo;

import bubolo.world.World;

/**
 * Abstract base class for Game Applications.
 *
 * @author BU CS673 - Clone Productions
 */
public abstract class AbstractGameApplication implements GameApplication
{
	private boolean ready;

	/** The game world. **/
	private World world;

	private State state;

	/**
	 * Constructs an AbstractGameApplication;
	 */
	protected AbstractGameApplication()
	{
		this.state = State.MAIN_MENU;
	}

	@Override
	public final boolean isReady()
	{
		return ready;
	}

	/**
	 * Sets whether the game is ready.
	 *
	 * @param value
	 *            true if the game is ready.
	 */
	protected void setReady(boolean value)
	{
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
	public void setState(State state)
	{
		this.state = state;
		onStateChanged();
	}

	@Override
	public State getState()
	{
		return state;
	}

	/**
	 * Called when the application's state is changed.
	 */
	protected void onStateChanged()
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}

	@Override
	public boolean isGameStarted()
	{
		return (isReady() && world != null);
	}
}
