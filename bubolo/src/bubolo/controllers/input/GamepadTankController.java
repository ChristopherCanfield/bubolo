package bubolo.controllers.input;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_A;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_B;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_X;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.glfwGetGamepadState;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickAxes;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;

import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFWGamepadState;

import bubolo.controllers.ActorEntityController;
import bubolo.world.Tank;
import bubolo.world.World;

/**
 * Controls the tank using a gamepad (controller).
 *
 * @author Christopher D. Canfield
 */
public class GamepadTankController extends ActorEntityController<Tank> {
	private final GLFWGamepadState gamepadState = GLFWGamepadState.create();

	// Whether the pillbox build key was pressed.
	private boolean pillboxBuildKeyPressed;

	/**
	 * Constructs a gamepad tank controller.
	 *
	 * @param tank reference to the local tank.
	 */
	public GamepadTankController(Tank tank) {
		super(tank);
	}

	@Override
	public void update(World world) {
		if (glfwJoystickPresent(GLFW_JOYSTICK_1)) {
			var tank = parent();

			var axes = glfwGetJoystickAxes(GLFW_JOYSTICK_1);
			glfwGetGamepadState(GLFW_JOYSTICK_1, gamepadState);

			processMovement(tank, axes);
			processCannon(tank, axes, world);
			processMineLaying(tank, axes, world);
			processPillboxBuilding(tank, world);
		}
	}

	private static void processMovement(Tank tank, FloatBuffer axes) {
		if (axes.get(GLFW_GAMEPAD_AXIS_LEFT_Y) < -0.15f) {
			tank.accelerate();
		} else if (axes.get(GLFW_GAMEPAD_AXIS_LEFT_Y) > 0.15f) {
			tank.decelerate();
		}

		if (axes.get(GLFW_GAMEPAD_AXIS_LEFT_X) < -0.5f) {
			tank.rotateRight();
		} else if (axes.get(GLFW_GAMEPAD_AXIS_LEFT_X) > 0.5f) {
			tank.rotateLeft();
		}
	}

	private void processCannon(Tank tank, FloatBuffer axes, World world) {
		boolean rightTriggerActivated = false;
		if (axes.limit() > GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER) {
			rightTriggerActivated = axes.get(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER) > 0.05;
		}

		if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_A) != 0 || rightTriggerActivated) {
			tank.fireCannon(world);
		}
	}

	private void processMineLaying(Tank tank, FloatBuffer axes, World world) {
		boolean leftTriggerActivated = false;
		if (axes.limit() > GLFW_GAMEPAD_AXIS_LEFT_TRIGGER) {
			leftTriggerActivated = axes.get(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER) > 0.05;
		}

		if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_B) != 0 || leftTriggerActivated) {
			tank.placeMine(world);
		}
	}

	private void processPillboxBuilding(Tank tank, World world) {
		if (gamepadState.buttons(GLFW_GAMEPAD_BUTTON_X) != 0) {
			pillboxBuildKeyPressed = true;
			tank.buildPillbox(world);
		} else {
			if (pillboxBuildKeyPressed) {
				pillboxBuildKeyPressed = false;
				tank.cancelBuildingPillbox();
			}
		}
	}
}
