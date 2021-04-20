package bubolo.controllers.input;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickAxes;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

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
			processMovement(tank);
			processCannon(tank, world);
		}


//		processCannon(tank, world);
//		processMineLaying(tank, world);
	}

	private static void processMovement(Tank tank) {
		var axes = glfwGetJoystickAxes(GLFW_JOYSTICK_1);

		if (axes.get(GLFW_GAMEPAD_AXIS_LEFT_Y) < -0.15f) {
			tank.accelerate();
		} else if (axes.get(GLFW_GAMEPAD_AXIS_LEFT_Y) > 0.15f) {
			tank.decelerate();
		}

		if (axes.get(GLFW_GAMEPAD_AXIS_LEFT_X) < -0.15f) {
			tank.rotateRight();
		} else if (axes.get(GLFW_GAMEPAD_AXIS_LEFT_X) > 0.15f) {
			tank.rotateLeft();
		}
	}

	private void processCannon(Tank tank, World world) {
		GLFW.glfwGetGamepadState(GLFW_JOYSTICK_1, gamepadState);
		if (gamepadState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_A) != 0) {
			float tankCenterX = tank.x();
			float tankCenterY = tank.y();

			tank.fireCannon(world, tankCenterX + 18 * (float) Math.cos(tank.rotation()),
					tankCenterY + 18 * (float) Math.sin(tank.rotation()));
		}
	}

	private static void processMineLaying(Tank tank, World world) {
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
			tank.placeMine(world);
		}
	}
}
