/**
 *
 */

package bubolo.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bubolo.mock.MockWorld;
import bubolo.mock.MockWorldOwner;

/**
 * @author BU CS673 - Clone Productions
 */
public class NetworkTest
{
	private Network net;

	@Before
	public void setup()
	{
		net = NetworkSystem.getInstance();
		net.dispose();
		// Non-debug tests are performed in the integration tests, since these can only
		// be fully tested by connecting to the network.
		net.startDebug();
	}

	@After
	public void teardown()
	{
		net.dispose();
		assertFalse(net.isServer());
		assertNull(net.getPlayerName());
	}

	/**
	 * Test method for {@link bubolo.net.NetworkSystem#getInstance()}.
	 */
	@Test
	public void testGetInstance()
	{
		assertNotNull(NetworkSystem.getInstance());
	}

	/**
	 * Test method for {@link bubolo.net.NetworkSystem#startServer()}. Note that this does not
	 * perform a full server test, since a connection to the internet and other external resources
	 * would be needed. Instead, it tests that the method's invariant is true, and then returns. The
	 * full test should be performed in integration.
	 */
	@Test
	public void testStartServer()
	{
		net.startServer("Server player");
	}

	/**
	 * Test method for {@link bubolo.net.NetworkSystem#connect(java.net.InetAddress)}. Note that
	 * this does not perform a full server test, since a connection to the internet and other
	 * external resources would be needed. Instead, it tests that the method's invariant is true,
	 * and then returns. The full test should be performed in integration.
	 */
	@Test
	public void testConnect()
	{
		try
		{
			net.connect(InetAddress.getByName("127.0.0.1"), "Client player");
		}
		catch (NetworkException | IllegalStateException | UnknownHostException e)
		{
			fail("Exception thrown in Network.connect");
		}
	}

	/**
	 * Test method for {@link bubolo.net.NetworkSystem#send(bubolo.net.NetworkCommand)}.
	 */
	@Test
	public void testSend()
	{
		net.send(mock(NetworkCommand.class));
	}

	/**
	 * Test method for {@link bubolo.net.NetworkSystem#update(bubolo.world.World)}.
	 */
	@Test
	public void testUpdate()
	{
		net.update(new MockWorldOwner());
	}

	/**
	 * Test method for {@link bubolo.net.NetworkSystem#postToGameThread(bubolo.net.NetworkCommand)}.
	 */
	@Test
	public void testPostToGameThread()
	{
		net.postToGameThread(mock(NetworkCommand.class));
	}

	@Test
	public void startGame()
	{
		net.startGame(new MockWorld());
	}

	@Test
	public void setGetPlayerName()
	{
		net.startDebug();
		final String name = "Test";
		net.startServer(name);

		assertEquals(name, net.getPlayerName());
	}

	@Test
	public void addRemoveObserver()
	{


		NetworkObserver o = mock(NetworkObserver.class);
		net.addObserver(o);
		assertEquals(1, net.getNotifier().getObserverCount());

		net.removeObserver(o);
		assertEquals(0, net.getNotifier().getObserverCount());
	}

	@Test
	public void isServer()
	{
		assertFalse(net.isServer());

		net.startServer("Test");
		assertTrue(net.isServer());
	}
}
