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
public class ClientConnectedTest
{
	/**
	 * Test method for {@link bubolo.net.command.ClientConnected#getClientName()}.
	 */
	@Test
	public void testGetClientName()
	{
		NetworkCommand command = new ClientConnected("Test");
		command.execute(new MockWorldOwner());
	}

	/**
	 * Test method for {@link bubolo.net.command.ClientConnected#execute(bubolo.world.World)}.
	 */
	@Test
	public void testExecute()
	{
		final String NAME = "TEST";
		ClientConnected command = new ClientConnected(NAME);
		assertEquals(NAME, command.getClientName());
	}
}
