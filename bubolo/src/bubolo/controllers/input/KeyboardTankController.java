package bubolo.controllers.input;

import static com.badlogic.gdx.Gdx.input;

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

	private boolean pillboxBeingPacked;
	private boolean pillboxBeingUnpacked;

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
		processPillboxPacking(tank, world);
	}

	private static void processMovement(Tank tank) {
		// TODO (cdc - 3/14/2014): allow the key mappings to be changed.

		if (input.isKeyPressed(Keys.W) || input.isKeyPressed(Keys.UP)) {
			tank.accelerate();
		} else if (input.isKeyPressed(Keys.S) || input.isKeyPressed(Keys.DOWN)) {
			tank.decelerate();
		}

		if (input.isKeyPressed(Keys.A) || input.isKeyPressed(Keys.LEFT)) {
			tank.rotateRight();
		} else if (input.isKeyPressed(Keys.D) || input.isKeyPressed(Keys.RIGHT)) {
			tank.rotateLeft();
		}
	}

	private static void processCannon(Tank tank, World world) {
		if (input.isKeyPressed(Keys.SPACE)) {
			tank.fireCannon(world);
		}
	}

	private static void processMineLaying(Tank tank, World world) {
		if (input.isKeyPressed(Keys.CONTROL_LEFT)) {
			tank.placeMine(world);
		}
	}

	private void processPillboxPacking(Tank tank, World world) {
		if (input.isKeyPressed(Keys.E)) {
			pillboxBeingPacked = true;
			tank.packNearestPillbox(world);
		} else if (pillboxBeingPacked) {
			pillboxBeingPacked = false;
			tank.cancelPillboxPackingIfNotPacked();
		}
	}
}
