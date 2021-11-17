/**
 *
 */

package bubolo.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bubolo.Systems;
import bubolo.Systems.NetworkType;

/**
 * @author BU CS673 - Clone Productions
 */
public class NetworkTest
{
	private Network net;

	@Before
	public void setup()
	{
		Systems.initializeNetwork(NetworkType.Null);
		net = Systems.network();
	}

	@After
	public void teardown()
	{
		net.dispose();
		assertFalse(net.isServer());
		assertNull(net.getPlayerName());
	}

	@Test
	public void setGetPlayerName()
	{
		final String name = "Test";
		net.startServer(name);

		assertEquals(name, net.getPlayerName());
	}

	@Test
	public void isServer()
	{
		assertFalse(net.isServer());

		net.startServer("Test");
		assertTrue(net.isServer());
	}
}
