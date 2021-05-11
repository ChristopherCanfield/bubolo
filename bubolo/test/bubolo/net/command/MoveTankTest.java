/**
 *
 */

package bubolo.net.command;

import java.util.UUID;

import org.junit.Test;

import bubolo.mock.MockTank;
import bubolo.mock.MockWorld;
import bubolo.net.NetworkCommand;
import bubolo.world.Entity;
import bubolo.world.Tank;

/**
 * @author BU CS673 - Clone Productions
 */
public class MoveTankTest
{
	/**
	 * Test method for {@link bubolo.net.command.UpdateTankAttributes#execute(bubolo.world.World)}.
	 */
	@Test
	public void testExecute()
	{
		UUID id = Entity.nextId();
		Tank tank = new MockTank();
		tank.setId(id);

		NetworkCommand command = new UpdateTankAttributes(tank);
		MockWorld world = new MockWorld();
		world.add(tank);
		command.execute(world);
	}
}
