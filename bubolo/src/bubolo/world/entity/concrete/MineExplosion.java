package bubolo.world.entity.concrete;

import bubolo.world.ActorEntity;
import bubolo.world.Collidable;
import bubolo.world.Damageable;
import bubolo.world.World;

/**
 * MineExplosions are created when mines blow up! They're large, and create Craters on top of
 * whatever Terrain is underneath them.
 *
 * @author BU CS673 - Clone Productions
 */
public class MineExplosion extends ActorEntity
{
	private static final float DAMAGE_PER_TICK = 2;

	/** Length of explosion in milliseconds */
	private static final long EXPLOSION_LENGTH = 500;

	/** The time the explosion will end. */
	private final long explosionEndTime;

	private static final int width = 60;
	private static final int height = 60;

	/**
	 * Constructs a new MineExplosion.
	 *
	 * @param args the entity's construction arguments.
	 */
	public MineExplosion(ConstructionArgs args) {
		super(args, width, height);
		explosionEndTime = System.currentTimeMillis() + EXPLOSION_LENGTH;
		updateBounds();
	}

	/**
	 * @return length of the explosion in milliseconds.
	 */
	public long getExplosionLength()
	{
		return EXPLOSION_LENGTH;
	}


	@Override
	public void onUpdate(World world)
	{
		if (explosionEndTime < System.currentTimeMillis()) {
			dispose();

		} else {
			for (Collidable collider : world.getNearbyCollidables(this, true, Damageable.class)) {
				// We know the collider is a damageable, since we filtered to include only Damageables.
				Damageable damageable = (Damageable) collider;
				damageable.receiveDamage(DAMAGE_PER_TICK, world);
			}
		}
	}
	@Override
	public boolean isSolid() {
		return false;
	}
}
