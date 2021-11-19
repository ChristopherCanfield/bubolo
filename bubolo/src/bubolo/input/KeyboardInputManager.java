package bubolo.input;

import static com.badlogic.gdx.Gdx.input;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

import bubolo.Systems;
import bubolo.input.InputManager.Action;

class KeyboardInputManager implements InputProcessor {

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
	}

	private static void processMovementActions(boolean[] actions) {
		if (input.isKeyPressed(Keys.W) || input.isKeyPressed(Keys.UP) || input.isKeyPressed(Keys.NUMPAD_8)) {
			actions[Action.Accelerate.ordinal()] = true;
		} else if (input.isKeyPressed(Keys.S) || input.isKeyPressed(Keys.DOWN) || input.isKeyPressed(Keys.NUMPAD_5) || input.isKeyPressed(Keys.NUMPAD_2)) {
			actions[Action.Decelerate.ordinal()] = true;
		}

		if (input.isKeyPressed(Keys.A) || input.isKeyPressed(Keys.LEFT) || input.isKeyPressed(Keys.NUMPAD_4)) {
			actions[Action.RotateClockwise.ordinal()] = true;
		} else if (input.isKeyPressed(Keys.D) || input.isKeyPressed(Keys.RIGHT) || input.isKeyPressed(Keys.NUMPAD_6)) {
			actions[Action.RotateCounterclockwise.ordinal()] = true;
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

	@Override
	public boolean keyDown(int keycode) {
		boolean[] actionsBackBuffer = Systems.input().actionsBackBuffer();
		processMenuMovementActions(actionsBackBuffer, keycode);
		processNextMenuGroupAction(actionsBackBuffer, keycode);
		processShowDiplomacyMenu(actionsBackBuffer, keycode);
		processFullscreenStatusChangeAction(actionsBackBuffer, keycode);
		processActivateAction(actionsBackBuffer, keycode);
		processCancelAction(actionsBackBuffer, keycode);

		return false;
	}

	private static void processMenuMovementActions(boolean[] actions, int keycode) {
		if (keycode == Keys.W || keycode == Keys.UP || keycode == Keys.NUMPAD_8) {
			actions[Action.MenuUp.ordinal()] = true;
		} else if (keycode == Keys.S || keycode == Keys.DOWN || keycode == Keys.NUMPAD_5 || keycode == Keys.NUMPAD_2) {
			actions[Action.MenuDown.ordinal()] = true;
		}

		if (keycode == Keys.A || keycode == Keys.LEFT || keycode == Keys.NUMPAD_4) {
			actions[Action.MenuRight.ordinal()] = true;
		} else if (keycode == Keys.D || keycode == Keys.RIGHT || keycode == Keys.NUMPAD_6) {
			actions[Action.MenuLeft.ordinal()] = true;
		}
	}

	private static void processNextMenuGroupAction(boolean[] actions, int keycode) {
		if (keycode == Keys.TAB) {
			if (input.isKeyPressed(Keys.SHIFT_LEFT) || input.isKeyPressed(Keys.SHIFT_RIGHT)) {
				actions[Action.MenuMoveToPreviousGroup.ordinal()] = true;
			} else {
				actions[Action.MenuMoveToNextGroup.ordinal()] = true;
			}
		}
	}

	private static void processShowDiplomacyMenu(boolean[] actions, int keycode) {
		if (keycode == Keys.F1) {
			actions[Action.ShowDiplomacyMenu.ordinal()] = true;
		}
	}

	private static void processActivateAction(boolean[] actions, int keycode) {
		if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER) {
			actions[Action.Activate.ordinal()] = true;
		}
	}

	private static void processCancelAction(boolean[] actions, int keycode) {
		if (keycode == Keys.ESCAPE) {
			actions[Action.Cancel.ordinal()] = true;
		}
	}

	private static void processFullscreenStatusChangeAction(boolean[] actions, int keycode) {
		if (keycode == Keys.ENTER && (input.isKeyPressed(Keys.ALT_LEFT) || input.isKeyPressed(Keys.ALT_RIGHT))) {
			actions[Action.FullscreenStatusChangeRequested.ordinal()] = true;
		}
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
