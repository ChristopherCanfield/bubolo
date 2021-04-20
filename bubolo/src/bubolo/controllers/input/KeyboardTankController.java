package bubolo.controllers.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import bubolo.controllers.Controller;
import bubolo.world.Tank;
import bubolo.world.World;

/**
 * Controls the tank using the keyboard.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class KeyboardTankController implements Controller {
	private final Tank tank;

	/**
	 * Constructs a keyboard tank controller.
	 *
	 * @param tank reference to the local tank.
	 */
	public KeyboardTankController(Tank tank) {
		this.tank = tank;
	}

	@Override
	public void update(World world) {
		processMovement(tank);
		processCannon(tank, world);
		processMineLaying(tank, world);
	}

	private static void processMovement(Tank tank) {
		// TODO (cdc - 3/14/2014): allow the key mappings to be changed.

		if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
			tank.accelerate();
		} else if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
			tank.decelerate();
		}

		if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
			tank.rotateRight();
		} else if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
			tank.rotateLeft();
		}
	}

	private static void processCannon(Tank tank, World world) {
		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			tank.fireCannon(world);
		}
	}

	private static void processMineLaying(Tank tank, World world) {
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
			tank.placeMine(world);
		}
	}
}
