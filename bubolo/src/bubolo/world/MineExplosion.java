package bubolo.world;

import bubolo.util.Units;
import bubolo.util.Time;

/**
 * MineExplosions are created when mines blow up. The explosion area is larger than the mine that exploded.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class MineExplosion extends ActorEntity {
	/** The explosion's lifetime, in seconds */
	public static final float LifetimeSeconds = 0.75f;

	private static final float totalDamage = 25;
	private static final float damagePerTick = totalDamage / Time.secondsToTicks(LifetimeSeconds);

	private static final int width = Units.TileToWorldScale;
	private static final int height = Units.TileToWorldScale;

	private static final int blastRadiusTiles = 1;

	/**
	 * Constructs a new MineExplosion.
	 *
	 * @param args the entity's construction arguments.
	 * @param world reference to the game world.
	 */
	protected MineExplosion(ConstructionArgs args, World world) {
		super(args, width, height);
		updateBounds();

		world.timer().scheduleSeconds(LifetimeSeconds, w -> {
			dispose();
		});
	}

	@Override
	public void onUpdate(World world) {
		for (Collidable collider : world.getCollidablesWithinTileDistance (this, blastRadiusTiles, false, Damageable.class)) {
			// We know the collider is a damageable, since we filtered to include only Damageables.
			Damageable damageable = (Damageable) collider;
			damageable.receiveDamage(damagePerTick, world);
		}
	}

	@Override
	public boolean isSolid() {
		return false;
	}
}
