package bubolo.util;

import java.util.Arrays;
import java.util.function.Consumer;

import bubolo.world.World;

/**
 * An efficient mechanism for scheduling actions, which are executed when their scheduled time is reached.
 * All times are internally converted to game ticks, to better support pausing and testing.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public class Timer {
	private int[] alarms;
	private Consumer<World>[] actions;
	private int size;

	private int nextAlarmIndex = 0;

	@SuppressWarnings("unchecked")
	public Timer(int initialSize) {
		alarms = new int[initialSize];
		actions = new Consumer[initialSize];
	}

	/**
	 * Schedules an action to occur after the specified number of seconds.
	 *
	 * @param seconds the number of seconds until the action is fired.
	 * @param action the action to fire once the specified time has been reached.
	 * @return the scheduled action's id.
	 */
	public int scheduleSeconds(float seconds, Consumer<World> action) {
		return scheduleTicks(Time.secondsToTicks(seconds), action);
	}

	/**
	 * Schedules an action to occur after the specified number of ticks.
	 *
	 * @param ticks the number of game ticks until the action is fired.
	 * @param action the action to fire once the specified time has been reached.
	 * @return the scheduled action's id.
	 */
	public int scheduleTicks(int ticks, Consumer<World> action) {
		int scheduledTicks = (ticks > 0) ? ticks : 1;

		int id = nextAlarmIndex;
		alarms[id] = scheduledTicks;
		actions[id] = action;

		if (id >= size) {
			size = id + 1;
			System.out.printf("Timer size is %d, capacity is %d%n", size, alarms.length);
		}

		nextAlarmIndex++;
		if (alarms[nextAlarmIndex] > 0) {
			nextAlarmIndex = findNextAlarmIndex();
		}

		return id;
	}

	/**
	 * Changes the time that a scheduled action will be fired.
	 *
	 * @param id the scheduled action's id.
	 * @param seconds the new number of seconds until the action is fired.
	 */
	public void rescheduleSeconds(int id, float seconds) {
		rescheduleTicks(id, Time.secondsToTicks(seconds));
	}

	/**
	 * Changes the time that a scheduled action will be fired.
	 *
	 * @param id the scheduled action's id.
	 * @param ticks the new number of ticks until the action is fired.
	 */
	public void rescheduleTicks(int id, int ticks) {
		assert actions[id] != null;

		alarms[id] = ticks;
	}

	/**
	 * Cancels a scheduled action.
	 *
	 * @param id the scheduled action's id.
	 */
	public void cancel(int id) {
		assert actions[id] != null;

		alarms[id] = 0;
		actions[id] = null;
	}

	/**
	 * Grows the two arrays.
	 *
	 * @return the length of the previous array + 1.
	 */
	private int resize() {
		int previousSize = alarms.length;

		final int growthFactor = 2;
		int newSize = previousSize * growthFactor;
		alarms = Arrays.copyOf(alarms, newSize);
		actions = Arrays.copyOf(actions, newSize);

		return previousSize + 1;
	}

	public void update(World world) {
		// Decrement all scheduled alarms.
		for (int i = 0; i < size; i++) {
			alarms[i]--;
		}

		// If an alarm has reached zero, execute the action, then destroy it.
		for (int i = 0; i < size; i++) {
			if (alarms[i] == 0) {
				actions[i].accept(world);
				actions[i] = null;
			} else if (alarms[i] < 0) {
				alarms[i] = 0;
			}
		}

		nextAlarmIndex = findNextAlarmIndex();
	}

	private int findNextAlarmIndex() {
		for (int i = 0; i < alarms.length; i++) {
			if (alarms[i] <= 0) {
				assert(actions[i] == null);

				return i;
			}
		}

		return resize();
	}
}
