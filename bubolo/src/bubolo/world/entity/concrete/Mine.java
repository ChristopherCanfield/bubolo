package bubolo.world.entity.concrete;

import bubolo.audio.Audio;
import bubolo.audio.Sfx;
import bubolo.world.ActorEntity;

/**
 * Mines can be placed by Tanks to do damage to enemy Tanks, or to destroy/modify
 * Terrain/structures.
 *
 * @author BU CS673 - Clone Productions
 */
public class Mine extends ActorEntity
{
	/**
	 * Boolean representing whether this Mine is exploding! OH NO!
	 */
	private boolean isExploding = false;

	/**
	 *  amount of time before mine becomes active in milliseconds
	 */
	private static int FUSE_TIME = 5000;

	/**
	 * time the mine was created in milliseconds
	 */
	private long createdTime;

	private static final int width = 25;
	private static final int height = 25;

	/**
	 * Constructs a new Mine.
	 */
	public Mine(ConstructionArgs args)
	{
		super(args, width, height);

		createdTime = System.currentTimeMillis();
		setOwnedByLocalPlayer(true);
		updateBounds();
	}

	/**
	 * Checks to see if this mine is currently exploding!
	 *
	 * @return true if this mine is in the process of exploding, false otherwise.
	 */
	public boolean isExploding()
	{
		return isExploding;
	}

	/**
	 * Sets the explosion status of this Mine.
	 *
	 * @param explode
	 *            should be true if this mine should be exploding, false otherwise.
	 */
	public void setExploding(boolean explode)
	{
		this.isExploding = explode;
	}

	/**
	 * get the status of this mine. will be inactive until the fuse time has elapsed since the mine was created
	 * @return
	 * 		whether or not this mine is active
	 */
	public boolean isActive()
	{
		boolean active = false;
		if ((this.createdTime + FUSE_TIME) < System.currentTimeMillis())
		{
			active = true;
		}
		return active;
	}

	@Override
	protected void onDispose()
	{
		Audio.play(Sfx.MINE_EXPLOSION);
	}

	@Override
	public boolean isSolid() {
		return false;
	}
}
