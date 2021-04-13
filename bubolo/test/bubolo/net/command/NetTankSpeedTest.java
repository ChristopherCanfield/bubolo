/**
 *
 */

package bubolo.net.command;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author BU CS673 - Clone Productions
 */
public class NetTankSpeedTest
{
	/**
	 * Test method for {@link bubolo.net.command.NetTankAttributes#getSpeed()}.
	 */
	@Test
	public void testGetSpeed()
	{
		final float SPEED = 11.74f;
		NetTankAttributes speed = new NetTankAttributes(SPEED);
		assertEquals(SPEED, speed.getSpeed(), 0.01f);
	}
}
