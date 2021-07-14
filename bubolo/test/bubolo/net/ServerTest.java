/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.net;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author BU CS673 - Clone Productions
 */
public class ServerTest
{
	private Server server;

	@Before
	public void setup()
	{
		server = new Server(new MockNetwork());
	}

	@After
	public void teardown()
	{
		server.dispose();
	}

	/**
	 * Test method for {@link bubolo.net.Server#send(bubolo.net.NetworkCommand)}.
	 */
	@Test
	public void testSend()
	{
		server.send(new NetworkCommand() {
			private static final long serialVersionUID = 1L;
		});
	}

	@Test
	public void startServer()
	{
		server.startServer("Server player");
	}

	@Test
	public void startGameNoClients()
	{
		try {
			server.startGame();
			fail("Exception expected, but none encountered.");
		} catch (IllegalStateException e) { }
	}
}
