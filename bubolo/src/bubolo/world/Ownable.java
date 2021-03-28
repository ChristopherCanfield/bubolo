package bubolo.world;

import java.util.UUID;

/**
 * Interface for Entities that are owned by a specific player. Used to determine object
 * color, friendly/enemy units, etc.
 *
 * @author BU CS673 - Clone Productions
 */
public interface Ownable
{
	/**
	 * returns the UID of the Ownable.
	 * @return the UID of this Ownable.
	 */
	public UUID getId();

	/**
	 * Returns the UID of the tank that owns this entity. Returns null if not owned.
	 *
	 * @return the UID of the tank that owns this entity.
	 */
	public UUID getOwnerId();

	/**
	 * Sets the UID of the tank that owns this entity.
	 *
	 * @param ownerId
	 * 		the UID to of the tank that now owns this entity.
	 */
	public void setOwnerId(UUID ownerId);

	/**
	 * Returns whether this Entity belongs to the local player.
	 *
	 * @return true if this Entity belongs to the local player, false otherwise.
	 */
	public boolean isLocalPlayer();

	/**
	 * Sets whether this Entity belongs to the local player or not.
	 *
	 * @param local
	 *            represents whether this tank should belong to the local player. Pass
	 *            false if not.
	 */
	public void setLocalPlayer(boolean local);

	/**
	 * Returns whether this Entity belongs to any player
	 *
	 * @return true if this Entity belongs to any player, false otherwise.
	 */
	default public boolean isOwned() {
		return getOwnerId() != null;
	}
}
