package bubolo.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bubolo.Config;
import bubolo.world.GameWorld;
import bubolo.world.World;

/**
 * @author Christopher D. Canfield
 */
public class TimerTest {
	private Timer timer;
	private World world;

	@BeforeEach
	public void beforeEach() {
		timer = new Timer(10);
		world = new GameWorld(1, 1);
	}

	public void updateEmptyTimer() {
		timer.update(world);
		timer.update(world);
		timer.update(world);
	}

	/**
	 * Schedules an action, and ensures that it is executed the timer expires.
	 */
	@Test
	public void scheduleAndExpireTicks() {
		TestAction action = new TestAction();
		timer.scheduleTicks(1, action);
		assertFalse(action.wasActionExecuted);

		timer.update(world);
		assertTrue(action.wasActionExecuted);
	}

	/**
	 * Schedules an action in seconds, and ensures that it is executed the timer expires.
	 */
	@Test
	public void scheduleAndExpireSeconds() {
		TestAction action = new TestAction();
		timer.scheduleSeconds(1, action);
		assertFalse(action.wasActionExecuted);

		// Simulate one second-worth of updates.
		for (int i = 0; i < Config.FPS; i++) {
			timer.update(world);
		}

		assertTrue(action.wasActionExecuted);
	}

	/**
	 * Schedules multiple actions, and ensure that they are all executed.
	 */
	@Test
	public void scheduleAndExpireMultiple() {
		var actions = new ArrayList<TestAction>();
		actions.add(new TestAction());
		actions.add(new TestAction());
		actions.add(new TestAction());
		actions.add(new TestAction());
		actions.add(new TestAction());

		// Schedule the actions.
		int id = timer.scheduleTicks(1, actions.get(0));
		assertNotEquals(id, timer.scheduleTicks(2, actions.get(1)));
		assertNotEquals(id, timer.scheduleTicks(3, actions.get(2)));
		assertNotEquals(id, timer.scheduleTicks(4, actions.get(3)));
		assertNotEquals(id, timer.scheduleTicks(5, actions.get(4)));

		// Confirm the actions haven't been executed.
		for (var action : actions) {
			assertFalse(action.wasActionExecuted);
		}

		//
		for (int ticks = 0; ticks < actions.size(); ticks++) {
			timer.update(world);
			assertTrue(actions.get(ticks).wasActionExecuted);
		}
	}

	/**
	 * Schedules an action, and then reschedules it.
	 */
	@Test
	public void reschedule() {
		TestAction action = new TestAction();
		int id = timer.scheduleTicks(1, action);
		assertFalse(action.wasActionExecuted);

		timer.rescheduleTicks(id, 2);
		timer.update(world);
		assertFalse(action.wasActionExecuted);

		timer.update(world);
		assertTrue(action.wasActionExecuted);
	}

	/**
	 * Cancels a scheduled action.
	 */
	@Test
	public void cancel() {
		TestAction action = new TestAction();
		int id = timer.scheduleTicks(1, action);
		timer.cancel(id);

		world.update();
		assertFalse(action.wasActionExecuted);
	}

	private static class TestAction implements Consumer<World> {
		boolean wasActionExecuted = false;

		@Override
		public void accept(World t) {
			wasActionExecuted = true;
		}
	}
}
