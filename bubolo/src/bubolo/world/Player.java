package bubolo.world;

import java.util.ArrayList;
import java.util.Collections;
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
	private final List<Player> pendingAllianceRequests;
	private Player[] otherPlayers;


	Player(String playerName, TeamColor playerColor, boolean isLocal, Tank owningTank, List<Tank> allTanks) {
		this.playerName = playerName;
		this.playerColor = playerColor;
		this.isLocal = isLocal;
		allies.add(owningTank);

		if (isLocal) {
			pendingAllianceRequests = new ArrayList<>();
			otherPlayers = tanksToPlayers(allTanks);
		} else {
			pendingAllianceRequests = null;
		}
	}

	private static Player[] tanksToPlayers(List<Tank> allTanks) {
		Player[] otherPlayers = new Player[allTanks.size() - 1];
		int playerIndex = 0;
		for (Tank tank : allTanks) {
			if (!tank.isOwnedByLocalPlayer()) {
				otherPlayers[playerIndex++] = tank.getPlayer();
			}
		}
		return otherPlayers;
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
	 * Returns a list of allied players. Intended for use by the diplomacy screen.
	 *
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
	 * @return a list of enemy players.
	 */
	public List<Player> getEnemyPlayers() {
		assert isLocal : "Player.getEnemyPlayers() can only be called on the local player.";

		int enemyCount = otherPlayers.length - allies.size() + 1;
		if (enemyCount == 0) {
			return Collections.emptyList();
		}

		var enemies = new ArrayList<Player>();
		for (int i = 0; i < otherPlayers.length; i++) {
			if (isEnemy(otherPlayers[i].playerName, allies)) {
				enemies.add(otherPlayers[i]);
			}
		}

		return enemies;
	}

	public List<Player> getPendingAllianceRequests() {
		return Collections.unmodifiableList(pendingAllianceRequests);
	}

	private static boolean isEnemy(String name, Set<Tank> allies) {
		for (Tank tank : allies) {
			if (tank.playerName().equals(name)) {
				return false;
			}
		}
		return true;
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
		assert !this.playerName.equals(requestingPlayer.playerName);
		pendingAllianceRequests.add(requestingPlayer);
	}

	public void removeAllianceRequest(Player requestingPlayer) {
		assert !this.playerName.equals(requestingPlayer.playerName);
		pendingAllianceRequests.remove(requestingPlayer);
	}
}
