package bubolo.world;

import java.util.UUID;

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

	private static int maxHitPoints = 2;
	private float hitPoints = maxHitPoints;

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

	@Override
	protected void onUpdate(World world) {
		if (isArmed()) {
			// Check if any tanks are touching this mine. If they are, explode the mine.
			for (Tank tank : world.getTanks()) {
				if (tank.overlapsEntity(this)) {
					explode(world);

					return;
				}
			}
		}
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
		return hitPoints;
	}

	@Override
	public int maxHitPoints() {
		return maxHitPoints;
	}

	@Override
	public void receiveDamage(float damage, World world) {
		hitPoints -= damage;

		if (!isDisposed() && hitPoints <= 0) {
			explode(world);
		}
	}

	public void explode(World world) {
		assert !isDisposed();

		dispose();

		addExplosion(world, x(), y());
		removeTerrainImprovement(world, tileColumn(), tileRow());
		var crater = addCrater(world, x(), y());
		floodCraterIfAdjacentToWater(world, crater);
	}

	private static void addExplosion(World world, float x, float y) {
		var args = new Entity.ConstructionArgs(UUID.randomUUID(), x, y, 0);
		world.addEntity(MineExplosion.class, args);
	}

	private static void removeTerrainImprovement(World world, int tileColumn, int tileRow) {
		var terrainImprovement = world.getTerrainImprovement(tileColumn, tileRow);
		if (terrainImprovement != null) {
			terrainImprovement.dispose();
		}
	}

	private static Crater addCrater(World world, float x, float y) {
		var args = new Entity.ConstructionArgs(UUID.randomUUID(), x, y, 0);
		return world.addEntity(Crater.class, args);
	}

	private static void floodCraterIfAdjacentToWater(World world, Crater crater) {
		if (isAdjacentToWater(world, crater.tileColumn(), crater.tileRow())) {
			world.timer().scheduleSeconds(4, w -> {
				crater.replaceWithWater(world);
			});
		}
	}

	private static boolean isAdjacentToWater(World world, int tileX, int tileY) {
		boolean adjacentToWater = false;
		if (world.isValidTile(tileX - 1, tileY)) {
			adjacentToWater = adjacentToWater || isWater(world, tileX - 1, tileY);
		}
		if (world.isValidTile(tileX + 1, tileY)) {
			adjacentToWater = adjacentToWater || isWater(world, tileX + 1, tileY);
		}

		if (world.isValidTile(tileY - 1, tileY)) {
			adjacentToWater = adjacentToWater || isWater(world, tileX, tileY - 1);
		}
		if (world.isValidTile(tileY + 1, tileY)) {
			adjacentToWater = adjacentToWater || isWater(world, tileX, tileY + 1);
		}

		return adjacentToWater;
	}

	private static boolean isWater(World world, int column, int row) {
		Terrain terrain = world.getTerrain(column, row);
		return Terrain.isWater(terrain);
	}
}