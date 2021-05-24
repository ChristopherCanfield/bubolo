package bubolo.mock;

import bubolo.net.WorldOwner;
import bubolo.world.GameWorld;
import bubolo.world.World;

public class MockWorldOwner implements WorldOwner {
	private final World world;

	public MockWorldOwner() {
		this.world = new GameWorld(2, 2);
	}

	@Override
	public World world() {
		return world;
	}

	@Override
	public void setWorld(World world) {
		assert(false);
	}
}
