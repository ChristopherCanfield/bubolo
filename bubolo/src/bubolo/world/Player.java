package bubolo.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import bubolo.graphics.TeamColor;
import bubolo.util.Nullable;

public class Player implements PlayerAttributes {
	private final UUID id;
	private final String playerName;
	private final TeamColor playerColor;
	private final boolean isLocal;
	private boolean alliedWithLocalPlayer;

	private final Set<Tank> allies = new HashSet<>(4);
	private final List<Player> pendingAllianceRequests;
	private final World world;


	Player(String playerName, TeamColor playerColor, boolean isLocal, Tank owningTank, World world) {
		this.id = owningTank.id();
		this.playerName = playerName;
		this.playerColor = playerColor;
		this.isLocal = isLocal;

		allies.add(owningTank);

		if (isLocal) {
			pendingAllianceRequests = new ArrayList<>();
			this.world = world;
		} else {
			pendingAllianceRequests = null;
			this.world = null;
		}
	}

	@Override
	public UUID id() {
		return id;
	}

	@Override
	public String name() {
		return playerName;
	}

	@Override
	public TeamColor color() {
		return playerColor;
	}

	@Override
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
	 * Returns a list of allied players. Intended for use by the diplomacy screen.
	 *
	 * @precondition this must be the local tank.
	 * @return a list of allied players.
	 */
	public List<Player> getAlliedPlayers() {
		assert isLocal : "Player.getAlliedPlayers() can only be called on the local player.";

		if (allies.size() <= 1) {
			return Collections.emptyList();
		}

		List<Player> names = new ArrayList<>(allies.size() - 1);
		for (Tank allyTank : allies) {
			if (!allyTank.isOwnedByLocalPlayer()) {
				names.add(allyTank.getPlayer());
			}
		}

		return names;
	}

	/**
	 * Returns a list of enemy players. Intended for use by the diplomacy screen.
	 *
	 * @precondition this must be the local tank.
	 * @return a list of enemy players.
	 */
	public List<Player> getEnemyPlayers() {
		assert isLocal : "Player.getEnemyPlayers() can only be called on the local player.";

		var tanks = world.getTanks();

		int enemyCount = tanks.size() - allies.size() + 1;
		if (enemyCount <= 0) {
			return Collections.emptyList();
		}

		var enemies = new ArrayList<Player>(enemyCount);
		for (Tank tank : tanks) {
			if (!allies.contains(tank)) {
				enemies.add(tank.getPlayer());
			}
		}

		return enemies;
	}

	/**
	 * Returns a list of pending alliance requests. Intended for use by the diplomacy screen.
	 *
	 * @precondition this must be the local tank.
	 * @return a list of pending alliance requests.
	 */
	public List<Player> getPendingAllianceRequests() {
		assert isLocal : "Player.getPendingAllianceRequests() can only be called on the local player.";
		return Collections.unmodifiableList(pendingAllianceRequests);
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

	public void addAllianceRequest(Player requestingPlayer) {
		assert !this.equals(requestingPlayer);
		if (!pendingAllianceRequests.contains(requestingPlayer)) {
			pendingAllianceRequests.add(requestingPlayer);
		}
	}

	public void removeAllianceRequest(Player requestingPlayer) {
		assert !this.equals(requestingPlayer);
		pendingAllianceRequests.remove(requestingPlayer);
	}

	// @TODO (cdc 2021-08-15): This may not be needed. Remove it if it isn't.
//	public void removeAllianceRequest(UUID playerIdToRemove) {
//		assert !this.id.equals(playerIdToRemove);
//
//		Player playerToRemove = null;
//		for (Player pendingRequest : pendingAllianceRequests) {
//			if (pendingRequest.id().equals(playerIdToRemove)) {
//				playerToRemove = pendingRequest;
//				break;
//			}
//		}
//
//		if (playerToRemove != null) {
//			removeAllianceRequest(playerToRemove);
//		} else {
//			throw new GameLogicException("Player.removeAllianceRequest called with an ID that does not correspond to an existing request: " + playerIdToRemove);
//		}
//	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Player otherPlayer) {
			return id.equals(otherPlayer.id());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
