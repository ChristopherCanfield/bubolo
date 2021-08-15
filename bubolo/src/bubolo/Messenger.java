package bubolo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bubolo.util.GameLogicException;
import bubolo.util.Nullable;
import bubolo.util.Timer;
import bubolo.world.ActorEntity;
import bubolo.world.DeepWater;
import bubolo.world.Entity;
import bubolo.world.Mine;
import bubolo.world.MineExplosion;
import bubolo.world.PlayerAttributes;
import bubolo.world.Tank;
import bubolo.world.World;

/**
 * Forwards in-game messages to observers.
 *
 * @author Christopher D. Canfield
 */
public class Messenger {

	/**
	 * Receives in-game messages from the Messenger.
	 *
	 * @author Christopher D. Canfield
	 */
	public interface MessageObserver {

		/**
		 * Indicates that an object owned by the local player is under attack.
		 *
		 * @param message a message that can be displayed to the player.
		 */
		void messageObjectUnderAttack(String message);

		/**
		 * Indicates that an object was captured.
		 *
		 * @param message a message that can be displayed to the player.
		 * @param thisPlayerLostObject true if the local player lost an object.
		 * @param thisPlayerCapturedObject true if the local player captured an object.
		 */
		void messageObjectCaptured(String message, boolean thisPlayerLostObject, boolean thisPlayerCapturedObject);

		/**
		 * Indicates that a player died.
		 *
		 * @param message a message that can be displayed to the player.
		 * @param thisPlayerDied true if the local player died.
		 */
		void messagePlayerDied(String message, boolean thisPlayerDied);

		/**
		 * Called when a player has disconnected.
		 *
		 * @param message a message that can be displayed to the player.
		 */
		void messagePlayerDisconnected(String message);

		/**
		 * Called when a player proposes an alliance with this player.
		 *
		 * @param message a message that can be displayed to the player.
		 */
		void messageAllianceRequestReceived(String message);

		/**
		 * Called when this player sends an alliance request to another player.
		 *
		 * @param message a message that can be displayed to the player.
		 */
		void messageAllianceRequestSent(String message);

		/**
		 * Called when a player accepts an alliance request.
		 *
		 * @param message a message that can be displayed to the player.
		 */
		void messageAllianceRequestAccepted(String message);

		/**
		 * Called when a player rejects an alliance request.
		 *
		 * @param message a message that can be displayed to the player.
		 */
		void messageAllianceRequestRejected(String message);
	}

	private final List<MessageObserver> observers = new CopyOnWriteArrayList<>();
	private final Timer<Void> timer = new Timer<>(1);
	private boolean readyToReceiveUnderAttackMessages = true;

	/**
	 * Must be called once per game tick.
	 */
	public void update() {
		timer.update(null);
	}

	public void addObserver(MessageObserver observer) {
		assert !observers.contains(observer);
		observers.add(observer);
	}

	public void removeObserver(MessageObserver observer) {
		boolean observerFound = observers.remove(observer);
		if (!observerFound) {
			throw new GameLogicException("Object " + observer.toString()
					+ " attempted to be removed from the Messenger, but it was not found in the observers list.");
		}
	}

	int observerCount() {
		return observers.size();
	}

	public void notifyObjectUnderAttack(World world, ActorEntity objectUnderAttack, @Nullable ActorEntity damageProvider) {
		if (readyToReceiveUnderAttackMessages && damageProvider != null && objectUnderAttack.hasOwner()) {
			var owner = objectUnderAttack.owner();
			if (owner.isOwnedByLocalPlayer()) {
				readyToReceiveUnderAttackMessages = false;
				timer.scheduleSeconds(5, (Void) -> readyToReceiveUnderAttackMessages = true);

				String zone = world.getZoneFromTile(objectUnderAttack.tileColumn(), objectUnderAttack.tileRow());
				String attackerName = world.getOwningPlayerName(damageProvider.id());
				notifyObjectUnderAttack(objectUnderAttack.getClass(), zone, attackerName);
			}
		}
	}

	/**
	 * Notifies observers that an object owned by the local player is under attack.
	 *
	 * @param objectType the type of object that is under attack.
	 * @param zone the world zone that the object is located in.
	 * @param attackerName the name of the attacker.
	 */
	public void notifyObjectUnderAttack(Class<? extends ActorEntity> objectType, String zone, @Nullable String attackerName) {
		var message = buildUnderAttackMessage(objectType, zone, attackerName);
		for (var observer : observers) {
			observer.messageObjectUnderAttack(message);
		}
	}

	private static String buildUnderAttackMessage(Class<? extends ActorEntity> objectType, String zone, String attackerName) {
		var message = new StringBuilder("Your ");
		message.append(objectType.getSimpleName().toLowerCase());
		message.append(" in the ");
		message.append(zone);
		message.append(" zone is under attack");
		if (attackerName != null) {
			message.append(" by ");
			message.append(attackerName);
		}
		message.append('.');

		return message.toString();
	}

	public void notifyObjectCaptured(World world, ActorEntity capturedObject, @Nullable ActorEntity previousOwner, @Nullable ActorEntity newOwner) {
		String zone = world.getZoneFromTile(capturedObject.tileColumn(), capturedObject.tileRow());

		boolean originalOwnerIsLocalPlayer = (previousOwner != null) ? previousOwner.isOwnedByLocalPlayer() : false;
		String originalOwnerName = (previousOwner != null) ? world.getOwningPlayerName(previousOwner.id()) : null;

		boolean newOwnerIsLocalPlayer = (newOwner != null) ? newOwner.isOwnedByLocalPlayer() : false;
		String newOwnerName = (newOwner != null) ? world.getOwningPlayerName(newOwner.id()) : null;

		notifyObjectCaptured(capturedObject.getClass(), zone, originalOwnerIsLocalPlayer, originalOwnerName, newOwnerIsLocalPlayer, newOwnerName);
	}

	/**
	 * Notifies observers that an object was captured.
	 *
	 * @param objectType the type of object that was captured.
	 * @param zone the world zone that the object is located in.
	 * @param originalOwnerIsLocalPlayer whether the original owner is the local player.
	 * @param originalOwnerName the original owner's name. May be null, if the object was neutral.
	 * @param newOwnerIsLocalPlayer whether the new owner is the local player.
	 * @param newOwnerName the new owner's name. May be null, if the object returned to being neutral.
	 */
	public void notifyObjectCaptured(Class<? extends ActorEntity> objectType, String zone,
			boolean originalOwnerIsLocalPlayer, @Nullable String originalOwnerName,
			boolean newOwnerIsLocalPlayer, @Nullable String newOwnerName) {

		assert !(originalOwnerIsLocalPlayer && newOwnerIsLocalPlayer);

		var message = buildObjectCapturedMessage(objectType, zone, originalOwnerIsLocalPlayer, originalOwnerName, newOwnerIsLocalPlayer, newOwnerName);
		for (var observers : observers) {
			observers.messageObjectCaptured(message, originalOwnerIsLocalPlayer, newOwnerIsLocalPlayer);
		}
	}

	private static String buildObjectCapturedMessage(Class<? extends ActorEntity> objectType, String zone,
			boolean originalOwnerIsLocalPlayer, @Nullable String originalOwnerName,
			boolean newOwnerIsLocalPlayer, @Nullable String newOwnerName) {
		var message = new StringBuilder();

		if (originalOwnerIsLocalPlayer) {
			message.append("Your");
		} else if (originalOwnerName != null) {
			message.append(originalOwnerName);
			message.append("'s");
		} else {
			message.append("A neutral");
		}

		message.append(' ');
		message.append(objectType.getSimpleName().toLowerCase());
		message.append(" located in the ");
		message.append(zone);
		message.append(" zone ");

		if (newOwnerName != null) {
			message.append("was captured by ");

			if (newOwnerIsLocalPlayer) {
				message.append("you.");
			} else {
				message.append(newOwnerName);
				message.append('.');
			}
		// If there is no owner name, the object became neutral.
		} else {
			message.append("became neutral.");
		}

		return message.toString();
	}

	/**
	 * Notifies observers that a player died.
	 *
	 * @param deadPlayerName the name of the dead player.
	 * @param localPlayerDied whether the player who died is the local player.
	 * @param killerType the type of the object that killed the player.
	 * @param killerPlayerName the name of the player who killed the player. May be null.
	 */
	public void notifyPlayerDied(String deadPlayerName, boolean localPlayerDied, Class<? extends Entity> killerType, @Nullable String killerPlayerName) {
		var message = buildPlayerDiedMessage(deadPlayerName, localPlayerDied, killerType, killerPlayerName);
		for (var observer : observers) {
			observer.messagePlayerDied(message, localPlayerDied);
		}
	}

	private static String buildPlayerDiedMessage(String deadPlayerName, boolean localPlayerDied, Class<? extends Entity> killerType, @Nullable String killerPlayerName) {
		var message = new StringBuilder();

		// Add the subject's name.
		if (localPlayerDied) {
			message.append("You");
		} else {
			message.append(deadPlayerName);
		}

		// Special case for deep water.
		if (killerType.equals(DeepWater.class)) {
			message.append(" drowned.");
			return message.toString();
		}

		if (localPlayerDied) {
			message.append(" were killed by ");
		} else {
			message.append(" was killed by ");
		}

		if (killerType.equals(Tank.class)) {
			message.append(killerPlayerName);
		} else {
			if (localPlayerDied && deadPlayerName.equals(killerPlayerName)) {
				message.append("your own ");
			} else if (killerPlayerName != null) {
				if (killerPlayerName.equals(deadPlayerName)) {
					message.append("their own ");
				} else {
					message.append(killerPlayerName);
					message.append("'s ");
				}
			} else {
				message.append("a neutral ");
			}

			if (killerType.equals(MineExplosion.class)) {
				message.append(Mine.class.getSimpleName().toLowerCase());
			} else {
				message.append(killerType.getSimpleName().toLowerCase());
			}
		}

		message.append(".");
		return message.toString();
	}

	/**
	 * Notifies observers that an alliance request has been received.
	 *
	 * @param requesterName the requester's name.
	 */
	public void notifyAllianceRequestReceived(String requesterName) {
		String message = requesterName + " proposes an alliance. Use the diplomacy screen (F1 or DPAD Up) to accept or reject this request.";

		for (var observer : observers) {
			observer.messageAllianceRequestReceived(message);
		}
	}

	public void notifyAllianceRequestSent(String targetName) {
		String message = "Alliance request sent to " + targetName;

		for (var observer : observers) {
			observer.messageAllianceRequestSent(message);
		}
	}

	/**
	 * Notifies observers that a player has accepted an alliance request.
	 *
	 * @param requester the player who requested the alliance.
	 * @param accepter the player who accepted the alliance request.
	 */
	public void notifyAllianceRequestAccepted(PlayerAttributes requester, PlayerAttributes accepter) {
		var message = buildAllianceRequestAcceptedMessage(requester, accepter);

		for (var observer : observers) {
			observer.messageAllianceRequestAccepted(message);
		}
	}

	private static String buildAllianceRequestAcceptedMessage(PlayerAttributes requester, PlayerAttributes accepter) {
		StringBuilder message = new StringBuilder();

		if (requester.isLocal()) {
			message.append("You are");
		} else {
			message.append(requester.name()).append(" is");
		}

		message.append(" now allied with ");

		if (accepter.isLocal()) {
			message.append("you.");
		} else {
			message.append(accepter.name()).append('.');
		}

		return message.toString();
	}

	/**
	 * Notifies observers that a player has rejected an alliance request.
	 *
	 * @param requester the player who requested the alliance.
	 * @param rejecter the player who rejected the alliance request.
	 */
	public void notifyAllianceRequestRejected(PlayerAttributes requester, PlayerAttributes rejecter) {
		var message = buildAllianceRequestRejectedMessage(requester, rejecter);

		for (var observer : observers) {
			observer.messageAllianceRequestRejected(message);
		}
	}

	private static String buildAllianceRequestRejectedMessage(PlayerAttributes requester, PlayerAttributes rejecter) {
		StringBuilder message = new StringBuilder();

		if (rejecter.isLocal()) {
			message.append("You");
		} else {
			message.append(rejecter.name());
		}

		message.append(" rejected an alliance request from ");

		if (requester.isLocal()) {
			message.append("you.");
		} else {
			message.append(requester.name()).append('.');
		}

		return message.toString();
	}

	/**
	 * Notifies observers that a player has disconnected from the game.
	 *
	 * @param playerName the name of the player who disconnected. May be null.
	 */
	public void notifyPlayerDisconnected(@Nullable String playerName) {
		String message = ((playerName != null) ? playerName : "A player") + " has left the game.";
		for (var observer : observers) {
			observer.messagePlayerDisconnected(message);
		}
	}
}
