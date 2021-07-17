package bubolo.world;

import com.badlogic.gdx.math.Polygon;

import bubolo.Systems;
import bubolo.audio.Sfx;
import bubolo.audio.SfxRateLimiter;

public class Building extends StaticEntity implements TerrainImprovement, Collidable, Damageable {
	private static final int width = 30;
	private static final int height = 30;

	private final BoundingBox boundingBox;

	private final int maxHitPoints = 40;
	private float hitPoints = maxHitPoints;

	private final SfxRateLimiter sfxPlayer = new SfxRateLimiter(150);

	protected Building(ConstructionArgs args, World world) {
		super(args, width, height);

		boundingBox = new BoundingBox(this);
	}

	@Override
	public boolean isValidBuildTarget() {
		return false;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public Polygon bounds() {
		return boundingBox.bounds();
	}

	@Override
	public void updateBounds() {
		boundingBox.updateBounds(this);
	}

	@Override
	public float hitPoints() {
		return hitPoints;
	}

	@Override
	public int maxHitPoints() {
		return maxHitPoints;
	}

	@Override
	public void receiveDamage(float damage, World world) {
		assert damage >= 0;
		hitPoints -= damage;

		if (!isDisposed()) {
			float healthPct = hitPoints / maxHitPoints;
			if (hitPoints <= 0) {
				onDeath(world);
			} else if (healthPct < 0.4f) {
				sfxPlayer.play(Sfx.BuildingHit2, x(), y());
			} else {
				sfxPlayer.play(Sfx.BuildingHit1, x(), y());
			}
		}
	}

	private void onDeath(World world) {
		dispose();
		Systems.audio().play(Sfx.BuildingDestroyed, x(), y());

		// When the building is destroyed, replace it with rubble.
		var args = new Entity.ConstructionArgs(Entity.nextId(), x(), y(), 0);
		world.addEntity(Rubble.class, args);
	}
}
