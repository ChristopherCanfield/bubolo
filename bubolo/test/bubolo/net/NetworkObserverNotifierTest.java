package bubolo.net;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import bubolo.net.command.SendMessage.MessageType;

/**
 * @author BU CS673 - Clone Productions
 */
public class NetworkObserverNotifierTest
{
	private NetworkObserverNotifier notifier;
	private MockNetworkObserver o;

	@Before
	public void setup()
	{
		this.notifier = new NetworkObserverNotifier();
		this.o = new MockNetworkObserver();
	}

	/**
	 * Test method for {@link bubolo.net.NetworkObserverNotifier#addObserver(bubolo.net.NetworkObserver)}.
	 */
	@Test
	public void testAddObserver()
	{
		notifier.addObserver(new MockNetworkObserver());
		assertEquals(1, notifier.getObserverCount());
	}

	/**
	 * Test method for {@link bubolo.net.NetworkObserverNotifier#removeObserver(bubolo.net.NetworkObserver)}.
	 */
	@Test
	public void testRemoveObserver()
	{
		notifier.addObserver(o);
		notifier.removeObserver(o);
		assertEquals(0, notifier.getObserverCount());
	}

	/**
	 * Test method for {@link bubolo.net.NetworkObserverNotifier#notifyConnect(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testNotifyConnect()
	{
		notifier.addObserver(o);

		final String CLIENT = "CLIENT";
		final String SERVER = "SERVER";
		notifier.notifyConnect(CLIENT, SERVER);

		assertEquals(CLIENT, o.getClientName());
		assertEquals(SERVER, o.getServerName());
	}

	/**
	 * Test method for {@link bubolo.net.NetworkObserverNotifier#notifyClientConnected(java.lang.String)}.
	 */
	@Test
	public void testNotifyClientConnected()
	{
		notifier.addObserver(o);

		final String CLIENT = "CLIENT";
		notifier.notifyClientConnected(CLIENT);

		assertEquals(CLIENT, o.getClientName());
	}

	/**
	 * Test method for {@link bubolo.net.NetworkObserverNotifier#notifyClientDisconnected(java.lang.String)}.
	 */
	@Test
	public void testNotifyClientDisconnected()
	{
		notifier.addObserver(o);

		final String CLIENT = "CLIENT";
		notifier.notifyClientDisconnected(CLIENT);

		assertEquals(CLIENT, o.getClientName());
	}

	@Test
	public void testNotifyClientReady() {
		notifier.addObserver(o);

		notifier.notifyClientReady("CLIENT");
		assertEquals("CLIENT", o.getClientName());
	}

	/**
	 * Test method for {@link bubolo.net.NetworkObserverNotifier#notifyMessageReceived(MessageType, java.lang.String)}.
	 */
	@Test
	public void testNotifyMessageReceived()
	{
		notifier.addObserver(o);

		final String MESSAGE = "MESSAGE";
		notifier.notifyMessageReceived(MessageType.Message, MESSAGE);

		assertEquals(MESSAGE, o.getMessage());
	}
}
