/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.net.command;

import org.junit.Test;

import bubolo.mock.MockWorld;
import bubolo.net.NetworkCommand;
import bubolo.world.entity.Entity;
import bubolo.world.entity.concrete.Pillbox;
import bubolo.world.entity.concrete.Tank;

/**
 * @author BU CS673 - Clone Productions
 */
public class CreateOwnableTest
{
	/**
	 * Test method for {@link bubolo.net.command.CreateBullet#execute(bubolo.world.World)}.
	 */
	@Test
	public void testExecute()
	{
		MockWorld world = new MockWorld();
		
		Pillbox pillbox = new Pillbox();
		world.add(pillbox);
		
		Entity tank = new Tank();
		world.add(tank);
		
		NetworkCommand command = new CreateOwnable(pillbox.getClass(), pillbox.getId(), pillbox.getX(), pillbox.getY(), pillbox.getRotation(), pillbox.getOwnerId());
		command.execute(world);
	}
}
