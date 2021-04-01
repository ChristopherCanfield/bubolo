/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.net;

import java.io.Serializable;

/**
 * A command that will be sent across the network to other users. The execute method
 * will be called exactly once when it reaches the other player, so any processing
 * (such as the creation of new Entities) should be performed in this method.
 * <code>NetworkCommand</clone>s must be immutable.
 *
 * @author Christopher D. Canfield
 */
public interface NetworkCommand extends Serializable
{
	/**
	 * Called when this NetworkCommand reaches another player. Perform any processing, such as creating new
	 * Entities, in this method. References on one machine will not be valid on another, so instead of using
	 * references directly, you must get a reference to an entity by using its UUID:
	 * <br><br>
	 * <code>Entity entity = World.getEntity(id)</code>
	 * <br><br>
	 * Because of this, many <code>execute</code> implementations will require an object's UUID.
	 *
	 * @param worldOwner reference to a world owner. You can safely use this directly.
	 */
	public void execute(WorldOwner worldOwner);
}
