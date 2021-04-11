package bubolo.world;

import bubolo.Config;

/**
 * MineExplosions are created when mines blow up. The explosion area is larger than the mine that exploded.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class MineExplosion extends ActorEntity {
	/** The explosion's lifetime, in milliseconds */
	private static final int explosionLifeTicks = (int) (500 / Config.MillisPerFrame);

	private static final float totalDamage = 25;
	private static final float damagePerTick = totalDamage / explosionLifeTicks;

	/** The time the explosion will end. */
	private int framesRemaining = explosionLifeTicks;

	private static final int width = 60;
	private static final int height = 60;

	/**
	 * Constructs a new MineExplosion.
	 *
	 * @param args the entity's construction arguments.
	 * @param world reference to the game world.
	 */
	protected MineExplosion(ConstructionArgs args, World world) {
		super(args, width, height);
		updateBounds();
	}

	@Override
	public void onUpdate(World world) {
		if (framesRemaining == 0) {
			dispose();

		} else {
			framesRemaining--;

			for (Collidable collider : world.getNearbyCollidables(this, false, Damageable.class)) {
				if (overlapsEntity(collider)) {
					// We know the collider is a damageable, since we filtered to include only Damageables.
					Damageable damageable = (Damageable) collider;
					damageable.receiveDamage(damagePerTick, world);
				}
			}
		}
	}

	@Override
	public boolean isSolid() {
		return false;
	}
}
