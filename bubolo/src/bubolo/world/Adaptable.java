package bubolo.world;

/**
 * Used by tiled sprites, such as roads, water, and walls, to select matching adjacent textures.
 *
 * TODO (cdc - 2021-03-29): This should be in the graphics system, not the world, because it only
 * affects the visual representation.
 *
 * @author BU CS673 - Clone Productions
 */
public interface Adaptable
{
	/**
	 * Updates the current adaptive tiling state of this Entity.
	 * @param w reference to the world.
	 */
	public void updateTilingState(World w);

	/**
	 * Returns the current adaptive tiling state of this Entity.
	 * @return the current adaptive tiling state of this Entity.
	 */
	public byte getTilingState();
}
