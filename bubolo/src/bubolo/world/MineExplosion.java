package bubolo.world;

/**
 * MineExplosions are created when mines blow up. The explosion area is larger than the mine that exploded.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class MineExplosion extends ActorEntity {
	private static final float damagePerTick = 2;

	/** The explosion's lifetime, in milliseconds */
	private static final long explosionLifeMillis = 500;

	/** The time the explosion will end. */
	private final long explosionEndTime;

	private static final int width = 60;
	private static final int height = 60;

	/**
	 * Constructs a new MineExplosion.
	 *
	 * @param args the entity's construction arguments.
	 */
	protected MineExplosion(ConstructionArgs args) {
		super(args, width, height);
		explosionEndTime = System.currentTimeMillis() + explosionLifeMillis;
		updateBounds();
	}

	/**
	 * @return length of the explosion in milliseconds.
	 */
	public long getExplosionLength() {
		return explosionLifeMillis;
	}

	@Override
	public void onUpdate(World world) {
		if (explosionEndTime < System.currentTimeMillis()) {
			dispose();

		} else {
			for (Collidable collider : world.getNearbyCollidables(this, true, Damageable.class)) {
				// We know the collider is a damageable, since we filtered to include only Damageables.
				Damageable damageable = (Damageable) collider;
				damageable.receiveDamage(damagePerTick, world);
			}
		}
	}

	@Override
	public boolean isSolid() {
		return false;
	}
}
