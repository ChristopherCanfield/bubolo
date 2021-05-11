/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.net.command;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import bubolo.mock.MockTank;
import bubolo.mock.MockWorld;
import bubolo.net.NetworkCommand;
import bubolo.world.Entity;
import bubolo.world.Grass;

/**
 * @author BU CS673 - Clone Productions
 */
public class NetworkCommandTest
{
	@Test
	public void testCreateEntityCommand()
	{
		NetworkCommand c = new CreateEntity(Grass.class, Entity.nextId(), 0, 0, 0);
		c.execute(new MockWorld());
	}

	@Test
	public void createEntitygetId()
	{
		UUID id = Entity.nextId();
		NetworkCommand c = new CreateEntity(Grass.class, id, 0, 0, 0);
		assertEquals(id, ((CreateEntity)c).getId());
	}

	@Test
	public void testCreateTankCommand()
	{
		NetworkCommand c = new CreateTank(new MockTank());
		c.execute(new MockWorld());
	}

	@Test
	public void testMoveEntity()
	{
		Grass grass = new Grass();
		MockWorld world = new MockWorld();
		world.add(grass);

		NetworkCommand c = new MoveEntity(grass);
		c.execute(world);
	}

}
