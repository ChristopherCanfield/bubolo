package bubolo.net.command;

import java.util.UUID;

import bubolo.util.Nullable;

/**
 * Used by the UpdateTankAttributes network command to set the tank's speed and health, and whether it is carrying a pillbox.
 *
 * @author Christopher D. Canfield
 */
public final class NetTankAttributes {
	public final float speed;
	public final float hitPoints;
	/** May be null. */
	public final @Nullable UUID carriedPillboxId;

	/**
	 * Constructs a Net Tank Attributes object.
	 *
	 * @param speed the tank's new speed.
	 * @param hitPoints the tank's new hit points.
	 * @param carriedPillboxId [optional] the ID of the pillbox carried by this tank, or null if no pillbox is being carried.
	 */
	NetTankAttributes(float speed, float hitPoints, @Nullable UUID carriedPillboxId) {
		this.speed = speed;
		this.hitPoints = hitPoints;
		this.carriedPillboxId = carriedPillboxId;
	}
}
