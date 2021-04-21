package bubolo.world;

import bubolo.audio.Audio;
import bubolo.audio.Sfx;
import bubolo.util.Time;

/**
 * Mines can be placed by Tanks to do damage to enemy Tanks, or to destroy/modify Terrain/structures.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Mine extends ActorEntity implements Damageable {
	/** Amount of time before mine becomes active, in milliseconds */
	private static final int fuseTimeTicks = Time.secondsToTicks(5);

	/** Whether the mine is currently armed. Only armed mines explode. */
	private boolean armed;

	private static final int width = 20;
	private static final int height = 20;

	/**
	 * Constructs a new Mine.
	 *
	 * @param args the entity's construction arguments.
	 * @param world reference to the game world.
	 */
	protected Mine(ConstructionArgs args, World world) {
		super(args, width, height);

		world.timer().scheduleTicks(fuseTimeTicks, w -> armed = true);
		setOwnedByLocalPlayer(true);
		updateBounds();
	}

	/**
	 * Whether the mine is armed or not. The mine starts unarmed, and becomes armed after a short delay. Unarmed mines
	 * do not explode when touched.
	 *
	 * @return whether or not this mine is armed.
	 */
	public boolean isArmed() {
		return armed;
	}

	@Override
	protected void onDispose() {
		Audio.play(Sfx.MineExplosion, x(), y());
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public float hitPoints() {
		return 0;
	}

	@Override
	public int maxHitPoints() {
		return 0;
	}

	@Override
	public void receiveDamage(float damage, World world) {
	}
}
