package bubolo.controllers.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import bubolo.Config;
import bubolo.Systems;
import bubolo.audio.Sfx;
import bubolo.controllers.Controller;
import bubolo.input.InputManager;
import bubolo.input.InputManager.Action;
import bubolo.world.Tank;
import bubolo.world.World;

/**
 * Controls the tank using player inputs.
 *
 * @author Christopher D. Canfield
 */
public class PlayerTankController implements Controller {
	private final Tank tank;

	// Whether the pillbox build key was pressed.
	private boolean pillboxBuildKeyPressed;

	/**
	 * Constructs a keyboard tank controller.
	 *
	 * @param tank reference to the local tank.
	 */
	public PlayerTankController(Tank tank) {
		this.tank = tank;
	}

	// @TODO (cdc 2021-07-29): For testing. Remove this after testing is complete.
	private boolean tankAllyButtonPressed;
	private int timer;

	@Override
	public void update(World world) {
		// @TODO (cdc 2021-07-29): For testing. Remove this after testing is complete.
		if (Gdx.input.isKeyPressed(Keys.NUM_1) && timer <= 0) {
			var tanks = world.getTanks();
			if (!tankAllyButtonPressed) {
				tanks.forEach(t -> tank.addAlly(t));
				tanks.forEach(t -> t.addAlly(tank));
				System.out.println("Allied with all tanks.");
			} else {
				tanks.forEach(t -> tank.removeAlly(t));
				tanks.forEach(t -> t.removeAlly(tank));
				System.out.println("All tanks are now your enemy!");
			}
			timer = Config.FPS;
			tankAllyButtonPressed = !tankAllyButtonPressed;
		}
		timer--;

		var input = Systems.input();

		processMovement(input, tank);
		processCannon(input, tank, world);
		processMineLaying(input, tank, world);
		processPillboxBuilding(input, tank, world);
	}

	private static void processMovement(InputManager input, Tank tank) {
		if (input.isPressed(Action.Accelerate)) {
			tank.accelerate();
		} else if (input.isPressed(Action.Decelerate)) {
			tank.decelerate();
		}

		if (input.isPressed(Action.RotateClockwise)) {
			tank.rotateRight();
		} else if (input.isPressed(Action.RotateCounterclockwise)) {
			tank.rotateLeft();
		}
	}

	private static void processCannon(InputManager input, Tank tank, World world) {
		if (input.isPressed(Action.FireCannon)) {
			tank.fireCannon(world);
		}
	}

	private static void processMineLaying(InputManager input, Tank tank, World world) {
		if (input.isPressed(Action.LayMine)) {
			tank.placeMine(world);
		}
	}

	private void processPillboxBuilding(InputManager input, Tank tank, World world) {
		if (input.isPressed(Action.Build)) {
			if (!pillboxBuildKeyPressed) {
				pillboxBuildKeyPressed = true;
				if (!tank.buildPillbox(world)) {
					// The pillbox build/unbuild process couldn't be started, so play a sound effect.
					Systems.audio().play(Sfx.BuildError, tank.x(), tank.y());
				}
			}
		} else {
			if (pillboxBuildKeyPressed) {
				pillboxBuildKeyPressed = false;
				tank.cancelBuildingPillbox();
			}
		}
	}
}
