package bubolo.world.entity.concrete;

import bubolo.util.TileUtil;
import bubolo.world.ActorEntity;
import bubolo.world.Damageable;
import bubolo.world.World;
import bubolo.world.entity.OldEntity;

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

	/** The time the explosion started. */
	private final long explosionStart;

	private static final int width = 60;
	private static final int height = 60;

	/**
	 * Constructs a new MineExplosion.
	 */
	public MineExplosion(ConstructionArgs args) {
		super(args, width, height);
		explosionStart = System.currentTimeMillis();
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
		if((EXPLOSION_LENGTH + explosionStart) > System.currentTimeMillis()) {
			for(OldEntity collider:TileUtil.getLocalCollisions(this, world))
			{
				if (collider instanceof Damageable)
				{
					Damageable damageableCollider = (Damageable)collider;
					damageableCollider.takeHit(DAMAGE_PER_TICK);
				}
			}
		}
		else {
			world.removeEntity(this);
		}
	}
	@Override
	public boolean isSolid() {
		return false;
	}
}
