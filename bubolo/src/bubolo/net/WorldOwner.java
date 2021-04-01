package bubolo.net;

import bubolo.world.World;

/**
 * Used by the network system to set an object's world after the world has been
 * received over the network.
 *
 * @author Christopher D. Canfield
 */
public interface WorldOwner {

	/**
	 * @return the object's world.
	 */
	World world();

	/**
	 * Sets the object's world.
	 *
	 * @param world the world received over the network.
	 */
	void setWorld(World world);
}
