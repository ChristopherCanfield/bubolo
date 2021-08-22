package bubolo.net.command;

import java.util.UUID;

import bubolo.net.NetworkGameCommand;
import bubolo.util.Nullable;
import bubolo.world.Entity;
import bubolo.world.Tank;
import bubolo.world.World;

public class TankDeath implements NetworkGameCommand {
	private static final long serialVersionUID = 1L;

	private final UUID tankId;
	private final Class<? extends Entity> killerType;
	private final @Nullable String killerName;

	public TankDeath(UUID tankId, Class<? extends Entity> killerType, @Nullable String killerName) {
		assert tankId != null;
		assert killerType != null;

		this.tankId = tankId;
		this.killerType = killerType;
		this.killerName = killerName;
	}

	@Override
	public void execute(World world) {
		var tank = (Tank) world.getEntity(tankId);
		// The local tank can't be killed by the network.
		assert !tank.isOwnedByLocalPlayer();

		System.out.printf("Tank net death received for %s. Killed by %s (%s)%n", tank.getPlayer().name(), killerName, killerType);

		tank.netDeath(world, killerType, killerName);
	}
}
