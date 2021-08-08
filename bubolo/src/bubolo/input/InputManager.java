package bubolo.input;

import java.util.Arrays;

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

		Diplomacy,

		Activate,
		Cancel,
	}

	// Whether an action was pressed or not.
	private final boolean[] actions = new boolean[Action.values().length];
	// If false, only menu-related actions are processed. Intended for in-game overlay menus.
	private boolean gameActionsEnabled = true;

	private final KeyboardInputManager keyboardInputManager = new KeyboardInputManager();
	private final GamepadInputManager gamepadInputManager = new GamepadInputManager();

	/**
	 * Processes input. Must be called each frame.
	 */
	public void update() {
		// Clear the actions array each frame.
		Arrays.fill(actions, false);

		keyboardInputManager.update(actions);
		gamepadInputManager.update(actions);

		if (!gameActionsEnabled) {
			Arrays.fill(actions, 0, Action.LastGameActionIndex.ordinal(), false);
		}
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
}
