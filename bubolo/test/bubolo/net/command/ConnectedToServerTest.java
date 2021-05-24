/**
 *
 */

package bubolo.net.command;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bubolo.mock.MockWorldOwner;
import bubolo.net.NetworkCommand;

/**
 * @author BU CS673 - Clone Productions
 */
public class ConnectedToServerTest
{
	/**
	 * Test method for {@link bubolo.net.command.ConnectedToServer#execute(bubolo.world.World)}.
	 */
	@Test
	public void testExecute()
	{
		NetworkCommand command = new ConnectedToServer("Client", "Server");
		command.execute(new MockWorldOwner());
	}

	@Test
	public void testValues()
	{
		final String CLIENT_NAME = "CLIENT";
		final String SERVER_NAME = "SERVER";
		ConnectedToServer command = new ConnectedToServer(CLIENT_NAME, SERVER_NAME);
		assertEquals(CLIENT_NAME, command.getClientName());
		assertEquals(SERVER_NAME, command.getServerName());
	}
}
