package bubolo.input;

import bubolo.input.InputManager.Action;

/**
 * Target for input action events.
 *
 * @author Christopher D. Canfield
 */
public interface InputActionObserver {

	/**
	 * Called when an input action is processed by the input manager.
	 *
	 * @param action the input action.
	 */
	void onInputAction(Action action);
}
