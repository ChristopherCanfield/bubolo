package bubolo.input;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputManager {
	public enum Action {
		Accelerate,
		Decelerate,
		RotateClockwise,
		RotateCounterClockwise,
		FireCannon,
		LayMine,
		Build,

		LastGameActionIndex,

		MenuUp,
		MenuDown,
		MenuLeft,
		MenuRight,
		MenuMoveToNextGroup,
		MenuMoveToPreviousGroup,

		Diplomacy,

		Activate,
		Cancel,
	}

	// Whether an action was pressed or not.
	private final boolean[] actions = new boolean[Action.values().length];
	private final Action[] actionsEnumArray = Action.values();
	// If false, only menu-related actions are processed. Intended for in-game overlay menus.
	private boolean gameActionsEnabled = true;

	private final KeyboardInputManager keyboardInputManager = new KeyboardInputManager();
	private final GamepadInputManager gamepadInputManager = new GamepadInputManager();

	private final List<InputActionObserver> observers = new CopyOnWriteArrayList<>();

	/**
	 * Processes input. Must be called each frame.
	 */
	public void update() {
		// Clear the actions array each frame.
		Arrays.fill(actions, false);

		keyboardInputManager.update(actions);
		gamepadInputManager.update(actions);

		// If game actions are disabled, set all game actions to false.
		if (!gameActionsEnabled) {
			Arrays.fill(actions, 0, Action.LastGameActionIndex.ordinal(), false);
		}

		notifyObservers(actions);
	}

	public boolean isPressed(Action action) {
		return actions[action.ordinal()];
	}

	public void enableGameActions() {
		gameActionsEnabled = true;
	}

	public void disableGameActions() {
		gameActionsEnabled = false;
	}

	private void notifyObservers(boolean[] actions) {
		for (int observerIndex = 0; observerIndex < observers.size(); observerIndex++) {
			for (int actionIndex = 0; actionIndex < actions.length; actionIndex++) {
				if (actions[actionIndex]) {
					observers.get(observerIndex).onInputAction(actionsEnumArray[actionIndex]);
				}
			}
		}
	}

	public void addObserver(InputActionObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(InputActionObserver observer) {
		observers.remove(observer);
	}
}
