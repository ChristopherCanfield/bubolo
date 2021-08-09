package bubolo.input;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_A;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_B;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_BACK;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_START;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_X;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.glfwGetGamepadState;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;

import org.lwjgl.glfw.GLFWGamepadState;

import bubolo.input.InputManager.Action;
import bubolo.util.Time;

class GamepadInputManager {
	private static final int menuActionRateLimit = Time.secondsToTicks(0.11f);
	private int ticksUntilNextMenuActionUpdate;

	private final GLFWGamepadState gamepadState = GLFWGamepadState.create();

	void update(boolean[] actions) {
		if (glfwJoystickPresent(GLFW_JOYSTICK_1)) {

			glfwGetGamepadState(GLFW_JOYSTICK_1, gamepadState);

			processMovementActions(actions);
			processCannonAction(actions);
			processMineLayingAction(actions);
			processBuildActions(actions);

			if (includeMenuActions()) {
				processMenuMovementActions(actions);
				processNextMenuGroupAction(actions);
				processShowDiplomacyMenu(actions);
				processActivateAction(actions);
				processCancelAction(actions);
				ticksUntilNextMenuActionUpdate = menuActionRateLimit;
			} else {
				ticksUntilNextMenuActionUpdate--;
			}
		}
	}

	private boolean includeMenuActions() {
		return ticksUntilNextMenuActionUpdate == 0;
	}

	private void processMovementActions(boolean[] actions) {
		if (gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y) < -0.15f) {
			actions[Action.Accelerate.ordinal()] = true;
		} else if (gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y) > 0.15f) {
			actions[Action.Decelerate.ordinal()] = true;
		}

		if (gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X) < -0.5f) {
			actions[Action.RotateClockwise.ordinal()] = true;
		} else if (gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X) > 0.5f) {
			actions[Action.RotateCounterclockwise.ordinal()] = true;
		}
	}

	private void processCannonAction(boolean[] actions) {
		if (gamepadState.axes(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER) > 0.05
				|| gamepadState.buttons(GLFW_GAMEPAD_BUTTON_A) != 0) {
			actions[Action.FireCannon.ordinal()] = true;
		}
	}

	private void processMineLayingAction(boolean[] actions) {
		if (gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER) > 0.05
				|| gamepadState.buttons(GLFW_GAMEPAD_BUTTON_B) != 0) {
			actions[Action.LayMine.ordinal()] = true;
		}
	}

	private void processBuildActions(boolean[] actions) {
		if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_X) != 0) {
			actions[Action.Build.ordinal()] = true;
		}
	}

	private void processMenuMovementActions(boolean[] actions) {
		if (gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y) < -0.5f) {
			actions[Action.MenuUp.ordinal()] = true;
		} else if (gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y) > 0.5f) {
			actions[Action.MenuDown.ordinal()] = true;
		}

		if (gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X) < -0.5f) {
			actions[Action.MenuRight.ordinal()] = true;
		} else if (gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X) > 0.5f) {
			actions[Action.MenuLeft.ordinal()] = true;
		}

		if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP) != 0) {
			actions[Action.MenuUp.ordinal()] = true;
		} else if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_DOWN) != 0) {
			actions[Action.MenuDown.ordinal()] = true;
		} else if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_LEFT) != 0) {
			actions[Action.MenuLeft.ordinal()] = true;
		} else if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT) != 0) {
			actions[Action.MenuRight.ordinal()] = true;
		}
	}

	private void processNextMenuGroupAction(boolean[] actions) {
		if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER) != 0) {
			actions[Action.MenuMoveToPreviousGroup.ordinal()] = true;
		} else if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) != 0) {
			actions[Action.MenuMoveToNextGroup.ordinal()] = true;
		}
	}

	private void processShowDiplomacyMenu(boolean[] actions) {
		if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP) != 0) {
			actions[Action.ShowDiplomacyMenu.ordinal()] = true;
		}
	}

	private void processActivateAction(boolean[] actions) {
		if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_START) != 0 || gamepadState.buttons(GLFW_GAMEPAD_BUTTON_A) != 0) {
			actions[Action.Activate.ordinal()] = true;
		}
	}

	private void processCancelAction(boolean[] actions) {
		if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_BACK) != 0) {
			actions[Action.Cancel.ordinal()] = true;
		}
	}
}
