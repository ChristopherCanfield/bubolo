package bubolo.util;

import bubolo.Config;

public final class Time {
	private Time() {}

	public static int secondsToTicks(float seconds) {
		return Math.round(seconds * Config.FPS);
	}
}
