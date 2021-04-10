package bubolo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import bubolo.Config;

/**
 * @author Christopher D. Canfield
 */
class TimeTest {

	@Test
	public void secondsToTicks() {
		assertEquals(Config.FPS, Time.secondsToTicks(1));
		assertEquals(0, Time.secondsToTicks(0));
		assertEquals(Config.FPS / 2, Time.secondsToTicks(0.5f));
		assertEquals(Config.FPS * 100, Time.secondsToTicks(100));
	}

}
