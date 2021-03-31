package bubolo.world.entity;

import java.util.UUID;

import bubolo.world.Ownable;

/**
 * Represents MobileEntities that exhibit some kind of behavior in the game world, such as tanks and humans.
 *
 * @author BU CS673 - Clone Productions
 */
@Deprecated
public abstract class Actor extends OldEntity implements Ownable
{

	/**
	 * Used when serializing and de-serializing.
	 */
	private static final long serialVersionUID = 6062132322107891442L;

	/**
	 * Boolean representing whether this Actor belongs to the local player.
	 */
	private boolean isLocalPlayer;

	private UUID ownerUID;

	/**
	 * Construct a new Actor with the specified UUID.
	 *
	 * @param id
	 *            is the existing UUID to be applied to the new Actor.
	 */
	public Actor(UUID id)
	{
		super(id);
	}

	@Override
	public boolean isLocalPlayer()
	{
		return isLocalPlayer;
	}

	@Override
	public void setLocalPlayer(boolean local)
	{
		this.isLocalPlayer = local;
	}

	@Override
	public UUID getOwnerId()
	{
		return this.ownerUID;
	}

	@Override
	public void setOwnerId(UUID ownerUID)
	{
		this.ownerUID = ownerUID;
	}
}
