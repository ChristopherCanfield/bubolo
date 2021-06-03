package bubolo.world;

import com.badlogic.gdx.math.Polygon;

public class Building extends StaticEntity implements TerrainImprovement, Collidable, Damageable {
	private static final int width = 30;
	private static final int height = 30;

	private final BoundingBox boundingBox;

	private final int maxHitPoints = 40;
	private float hitPoints = maxHitPoints;

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
		hitPoints -= damage;
		// @TODO (cdc 2021-06-02): Replace this with rubble when it's destroyed.
	}
}
