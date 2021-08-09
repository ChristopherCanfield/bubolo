package bubolo.input;

import static com.badlogic.gdx.Gdx.input;

import com.badlogic.gdx.Input.Keys;

import bubolo.input.InputManager.Action;

class KeyboardInputManager {

	/**
	 * Updates the actions array with keyboard input. Must be called once per frame.
	 *
	 * @param actions reference to the actions array.
	 */
	void update(boolean[] actions) {
		processMovementActions(actions);
		processCannonAction(actions);
		processMineLayingAction(actions);
		processBuildActions(actions);
		processNextMenuGroupAction(actions);
		processActivateAction(actions);
		processCancelAction(actions);
	}

	private static void processMovementActions(boolean[] actions) {
		if (input.isKeyPressed(Keys.W) || input.isKeyPressed(Keys.UP) || input.isKeyPressed(Keys.NUMPAD_8)) {
			actions[Action.Accelerate.ordinal()] = true;
			actions[Action.MenuUp.ordinal()] = true;
		} else if (input.isKeyPressed(Keys.S) || input.isKeyPressed(Keys.DOWN) || input.isKeyPressed(Keys.NUMPAD_5) || input.isKeyPressed(Keys.NUMPAD_2)) {
			actions[Action.Decelerate.ordinal()] = true;
			actions[Action.MenuDown.ordinal()] = true;
		}

		if (input.isKeyPressed(Keys.A) || input.isKeyPressed(Keys.LEFT) || input.isKeyPressed(Keys.NUMPAD_4)) {
			actions[Action.RotateClockwise.ordinal()] = true;
			actions[Action.MenuRight.ordinal()] = true;
		} else if (input.isKeyPressed(Keys.D) || input.isKeyPressed(Keys.RIGHT) || input.isKeyPressed(Keys.NUMPAD_6)) {
			actions[Action.RotateCounterClockwise.ordinal()] = true;
			actions[Action.MenuLeft.ordinal()] = true;
		}
	}

	private static void processCannonAction(boolean[] actions) {
		if (input.isKeyPressed(Keys.SPACE)) {
			actions[Action.FireCannon.ordinal()] = true;
		}
	}

	private static void processMineLayingAction(boolean[] actions) {
		if (input.isKeyPressed(Keys.CONTROL_LEFT)) {
			actions[Action.LayMine.ordinal()] = true;
		}
	}

	private static void processBuildActions(boolean[] actions) {
		if (input.isKeyPressed(Keys.E)) {
			actions[Action.Build.ordinal()] = true;
		}
	}

	private static void processNextMenuGroupAction(boolean[] actions) {
		if (input.isKeyPressed(Keys.TAB)) {
			if (input.isKeyPressed(Keys.SHIFT_LEFT) || input.isKeyPressed(Keys.SHIFT_RIGHT)) {
				actions[Action.MenuMoveToPreviousGroup.ordinal()] = true;
			} else {
				actions[Action.MenuMoveToNextGroup.ordinal()] = true;
			}
		}
	}

	private static void processActivateAction(boolean[] actions) {
		if (input.isKeyPressed(Keys.ENTER) || input.isKeyPressed(Keys.NUMPAD_ENTER)) {
			actions[Action.Activate.ordinal()] = true;
		}
	}

	private static void processCancelAction(boolean[] actions) {
		if (input.isKeyPressed(Keys.ESCAPE)) {
			actions[Action.Cancel.ordinal()] = true;
		}
	}
}
