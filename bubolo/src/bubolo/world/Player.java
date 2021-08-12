package bubolo.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bubolo.graphics.TeamColor;
import bubolo.util.Nullable;

public class Player {
	private final String playerName;
	private final TeamColor playerColor;
	private final boolean isLocal;
	private boolean alliedWithLocalPlayer;

	private final Set<Tank> allies = new HashSet<>(4);
	private List<String> pendingAllianceRequests;


	Player(String playerName, TeamColor playerColor, boolean isLocal) {
		this.playerName = playerName;
		this.playerColor = playerColor;
		this.isLocal = isLocal;

		if (isLocal) {
			pendingAllianceRequests = new ArrayList<>();
		}
	}

	public String name() {
		return playerName;
	}

	public TeamColor color() {
		return playerColor;
	}

	public boolean isLocal() {
		return isLocal;
	}

	/**
	 * @return whether this player is either controlled by or allied with the local player.
	 */
	boolean isAlliedWithLocalPlayer() {
		return isLocal || alliedWithLocalPlayer;
	}

	void addAlly(Tank tank) {
		this.allies.add(tank);
		if (tank.isOwnedByLocalPlayer()) {
			alliedWithLocalPlayer = true;
		}
	}

	void removeAlly(Tank tank) {
		this.allies.remove(tank);
		if (tank.isOwnedByLocalPlayer()) {
			alliedWithLocalPlayer = false;
		}
	}

	/**
	 * @param actor the actor to check. May be null.
	 * @return whether this tank is allied with the specified actor, or its owner.
	 */
	boolean isAlliedWith(@Nullable ActorEntity actor) {
		if (actor == null) {
			return false;
		} else if (actor instanceof Tank tank) {
			return allies.contains(tank);
		} else if (actor.owner() instanceof Tank tank) {
			return allies.contains(tank);
		} else {
			return false;
		}
	}

	public void addAllianceRequest(String playerName) {
		assert !this.playerName.equals(playerName);
		pendingAllianceRequests.add(playerName);
	}

	public void removeAllianceRequest(String playerName) {
		assert !this.playerName.equals(this);
		pendingAllianceRequests.remove(playerName);
	}
}
