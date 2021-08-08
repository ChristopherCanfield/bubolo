package bubolo.input;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_A;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_B;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_BACK;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_START;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_X;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.glfwGetGamepadState;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;

import org.lwjgl.glfw.GLFWGamepadState;

import bubolo.input.InputManager.Action;

class GamepadInputManager {
	private final GLFWGamepadState gamepadState = GLFWGamepadState.create();

	void update(boolean[] actions) {
		if (glfwJoystickPresent(GLFW_JOYSTICK_1)) {

			glfwGetGamepadState(GLFW_JOYSTICK_1, gamepadState);

			processMovementActions(actions);
			processCannonAction(actions);
			processMineLayingAction(actions);
			processBuildActions(actions);
			processActivateAction(actions);
			processCancelAction(actions);
		}
	}

	private void processMovementActions(boolean[] actions) {
		if (gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y) < -0.15f) {
			actions[Action.Accelerate.ordinal()] = true;
			actions[Action.MenuUp.ordinal()] = true;
		} else if (gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y) > 0.15f) {
			actions[Action.Decelerate.ordinal()] = true;
			actions[Action.MenuDown.ordinal()] = true;
		}

		if (gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X) < -0.5f) {
			actions[Action.RotateClockwise.ordinal()] = true;
			actions[Action.MenuRight.ordinal()] = true;
		} else if (gamepadState.axes(GLFW_GAMEPAD_AXIS_LEFT_X) > 0.5f) {
			actions[Action.RotateCounterClockwise.ordinal()] = true;
			actions[Action.MenuLeft.ordinal()] = true;
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

	private void processActivateAction(boolean[] actions) {
		if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_START) != 0) {
			actions[Action.Activate.ordinal()] = true;
		}
	}

	private void processCancelAction(boolean[] actions) {
		if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_BACK) != 0) {
			actions[Action.Cancel.ordinal()] = true;
		}
	}
}
