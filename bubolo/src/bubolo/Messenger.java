package bubolo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bubolo.util.GameLogicException;
import bubolo.util.Nullable;
import bubolo.world.ActorEntity;
import bubolo.world.Entity;

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
		 */
		void messageReceivedObjectUnderAttack(String message, Class<? extends ActorEntity> objectType, String zone);

		/**
		 * Indicates that an object was captured.
		 *
		 * @param message a message that can be displayed to the human player.
		 * @param objectType the type of object that was captured.
		 * @param zone the world zone that the object is located in.
		 * @param originalOwnerIsLocalPlayer whether the original owner is the local player.
		 * @param originalOwnerName the original owner's name.
		 * @param newOwnerName the new owner's name.
		 */
		void messageReceivedObjectCaptured(String message, Class<? extends ActorEntity> objectType, String zone, boolean originalOwnerIsLocalPlayer, String originalOwnerName, String newOwnerName);

		/**
		 * Indicates that a player died.
		 *
		 * @param message a message that can be displayed to the human player.
		 * @param deadPlayerName the name of the dead player.
		 * @param localPlayerDied whether the player who died is the local player.
		 * @param killerType the type of the object that killed the player.
		 * @param killerPlayerName the name of the player who killed the player. May be null.
		 */
		void messageReceivedPlayerDied(String message, String deadPlayerName, boolean localPlayerDied, Class<? extends Entity> killerType, @Nullable String killerPlayerName);
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
	}

	/**
	 * Notifies observers that an object was captured.
	 *
	 * @param objectType the type of object that was captured.
	 * @param zone the world zone that the object is located in.
	 * @param originalOwnerIsLocalPlayer whether the original owner is the local player.
	 * @param originalOwnerName the original owner's name.
	 * @param newOwnerName the new owner's name.
	 */
	public void notifyObjectCaptured(Class<? extends ActorEntity> objectType, String zone, boolean originalOwnerIsLocalPlayer, String originalOwnerName, String newOwnerName) {
		// @TODO (cdc 2021-07-18): Implement this.
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
		// @TODO (cdc 2021-07-18): Implement this.
	}
}
