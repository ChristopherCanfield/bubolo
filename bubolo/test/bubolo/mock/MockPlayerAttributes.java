package bubolo.mock;

import java.util.UUID;

import bubolo.graphics.TeamColor;
import bubolo.world.PlayerAttributes;

public class MockPlayerAttributes implements PlayerAttributes {

	private boolean localPlayer;

	public MockPlayerAttributes(boolean localPlayer) {
		this.localPlayer = localPlayer;
	}

	@Override
	public UUID id() {
		return UUID.randomUUID();
	}

	@Override
	public String name() {
		return "Test Player";
	}

	@Override
	public TeamColor color() {
		return TeamColor.Black;
	}

	@Override
	public boolean isLocal() {
		return localPlayer;
	}
}
