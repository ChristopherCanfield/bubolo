package bubolo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bubolo.util.GameLogicException;
import bubolo.util.Nullable;
import bubolo.world.ActorEntity;
import bubolo.world.DeepWater;
import bubolo.world.Entity;
import bubolo.world.Tank;

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
		 * @param message a message that can be displayed to the human player.
		 * @param objectType the type of object that is under attack.
		 * @param zone the world zone that the object is located in.
		 * @param attackerName the attacker's name.
		 */
		void messageObjectUnderAttack(String message, Class<? extends ActorEntity> objectType, String zone, String attackerName);

		/**
		 * Indicates that an object was captured.
		 *
		 * @param message a message that can be displayed to the human player.
		 * @param objectType the type of object that was captured.
		 * @param zone the world zone that the object is located in.
		 * @param originalOwnerIsLocalPlayer whether the original owner is the local player.
		 * @param originalOwnerName the original owner's name. Will be null if the object was neutral.
		 * @param newOwnerIsLocalPlayer whether the new owner is the local player.
		 * @param newOwnerName the new owner's name. Will be null if the object became neutral.
		 */
		void messageObjectCaptured(String message, Class<? extends ActorEntity> objectType, String zone,
				boolean originalOwnerIsLocalPlayer, @Nullable String originalOwnerName,
				boolean newOwnerIsLocalPlayer, @Nullable String newOwnerName);

		/**
		 * Indicates that a player died.
		 *
		 * @param message a message that can be displayed to the human player.
		 * @param deadPlayerName the name of the dead player.
		 * @param localPlayerDied whether the player who died is the local player.
		 * @param killerType the type of the object that killed the player.
		 * @param killerPlayerName the name of the player who killed the player. May be null.
		 */
		void messagePlayerDied(String message, String deadPlayerName, boolean localPlayerDied, Class<? extends Entity> killerType, @Nullable String killerPlayerName);
	}

	private List<MessageObserver> observers = new CopyOnWriteArrayList<>();

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

	/**
	 * Notifies observers that an object owned by the local player is under attack.
	 *
	 * @param objectType the type of object that is under attack.
	 * @param zone the world zone that the object is located in.
	 * @param attackerName the name of the attacker.
	 */
	public void notifyObjectUnderAttack(Class<? extends ActorEntity> objectType, String zone, String attackerName) {
		// @TODO (cdc 2021-07-18): Implement this.

		var message = buildUnderAttackMessage(objectType, zone, attackerName);
		for (var observer : observers) {
			observer.messageObjectUnderAttack(message, objectType, zone, attackerName);
		}
	}

	private static String buildUnderAttackMessage(Class<? extends ActorEntity> objectType, String zone, String attackerName) {
		var message = new StringBuilder("Your ");
		message.append(objectType.getSimpleName());
		message.append(" in the ");
		message.append(zone);
		message.append(" zone is under attack by ");
		message.append(attackerName);
		message.append('.');

		return message.toString();
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

		var message = buildObjectCapturedMessage(objectType, zone, originalOwnerIsLocalPlayer, originalOwnerName, newOwnerIsLocalPlayer, newOwnerName);
		for (var observers : observers) {
			observers.messageObjectCaptured(message, objectType, zone, originalOwnerIsLocalPlayer, originalOwnerName, newOwnerIsLocalPlayer, newOwnerName);
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
		message.append(objectType.getSimpleName());
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
			observer.messagePlayerDied(message, deadPlayerName, localPlayerDied, killerType, killerPlayerName);
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

		//
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
			if (killerPlayerName != null) {
				message.append(killerPlayerName);
				message.append("'s ");
			} else {
				message.append(" a neutral ");
			}
			message.append(killerType.getSimpleName());
		}

		message.append(".");
		return message.toString();
	}
}
