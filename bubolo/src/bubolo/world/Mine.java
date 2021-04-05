package bubolo.world;

import bubolo.audio.Audio;
import bubolo.audio.Sfx;

/**
 * Mines can be placed by Tanks to do damage to enemy Tanks, or to destroy/modify Terrain/structures.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Mine extends ActorEntity {
	/** Whether this Mine is exploding. */
	private boolean isExploding = false;

	/** Amount of time before mine becomes active, in milliseconds */
	private static final int fuseTimeMillis = 5000;

	/** Time the mine was created in milliseconds. */
	private long createdTime;

	private static final int width = 25;
	private static final int height = 25;

	/**
	 * Constructs a new Mine.
	 *
	 * @param args the entity's construction arguments.
	 */
	protected Mine(ConstructionArgs args) {
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
	public boolean isExploding() {
		return isExploding;
	}

	/**
	 * Sets the explosion status of this Mine.
	 *
	 * @param explode should be true if this mine should be exploding, false otherwise.
	 */
	public void setExploding(boolean explode) {
		this.isExploding = explode;
	}

	/**
	 * Whether the mine is active or not. The mine starts inactive, and becomes active after the delay is reached.
	 *
	 * @return whether or not this mine is active
	 */
	public boolean isActive() {
		boolean active = false;
		if ((this.createdTime + fuseTimeMillis) < System.currentTimeMillis()) {
			active = true;
		}
		return active;
	}

	@Override
	protected void onDispose() {
		Audio.play(Sfx.MINE_EXPLOSION);
	}

	@Override
	public boolean isSolid() {
		return false;
	}
}
