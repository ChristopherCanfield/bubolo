package bubolo.world.entity.concrete;

import java.util.UUID;

import bubolo.world.entity.StationaryElement;

/**
 * Rubble is created when structures (like Walls) are destroyed.
 *
 * @author BU CS673 - Clone Productions
 */
public class Rubble extends StationaryElement
{
	/**
	 * Used in serialization/de-serialization.
	 */
	private static final long serialVersionUID = 6510667006657276377L;

	/**
	 * Construct a new Rubble with a random UUID.
	 */
	public Rubble()
	{
		this(UUID.randomUUID());
	}

	/**
	 * Construct a new Rubble with the specified UUID.
	 *
	 * @param id
	 *            is the existing UUID to be applied to the new Grass.
	 */
	public Rubble(UUID id)
	{
		super(id);
		setWidth(32);
		setHeight(32);
		updateBounds();
	}
}
