/**
 *
 */

package bubolo.net.command;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bubolo.Systems;
import bubolo.mock.MockWorldOwner;
import bubolo.net.Network;
import bubolo.net.NetworkCommand;

/**
 * @author BU CS673 - Clone Productions
 */
public class SendMessageTest
{
	/**
	 * Test method for {@link bubolo.net.command.SendMessage#execute(bubolo.world.World)}.
	 */
	@Test
	public void testExecute()
	{
		NetworkCommand command = new SendMessage("Test");
		command.execute(new MockWorldOwner());
	}

	/**
	 * Test method for {@link bubolo.net.command.SendMessage#getMessage()}.
	 */
	@Test
	public void testGetMessage()
	{
		final String MESSAGE = "TEST TEST";
		final String NAME = "HELLO";
		Network net = Systems.network();
		net.startServer(NAME);

		SendMessage command = new SendMessage(MESSAGE);
		assertEquals(NAME + ": " + MESSAGE, command.getMessage());
	}
}
