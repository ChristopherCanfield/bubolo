/**
 *
 */

package bubolo.net.command;

/**
 * Used by the UpdateTankAttributes network command to set the tank's speed and health.
 *
 * @author Christopher D. Canfield
 */
public final class NetTankAttributes {
	public final float speed;
	public final float hitPoints;

	/**
	 * Constructs a Net Tank Speed object.
	 *
	 * @param speed the tank's new speed.
	 * @param hitPoints the tank's new hit points.
	 */
	NetTankAttributes(float speed, float hitPoints) {
		this.speed = speed;
		this.hitPoints = hitPoints;
	}
}
