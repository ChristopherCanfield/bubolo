package bubolo.world;

import com.badlogic.gdx.math.Rectangle;

import bubolo.Config;
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

	/** Whether the local player can see this mine. */
	private boolean canBeSeenByLocalPlayer;

	/**
	 * Constructs a new Mine.
	 *
	 * @param args the entity's construction arguments.
	 * @param world reference to the game world.
	 */
	protected Mine(ConstructionArgs args, World world) {
		super(args, width, height);

		world.timer().scheduleTicks(fuseTimeTicks, w -> {
			setVisibility(w);
			armed = true;
		});
		updateBounds();

		setVisibility(world);
	}

	private void setVisibility(World world) {
		if (!canBeSeenByLocalPlayer) {
			float visibleLeft = centerX() - Config.CameraWorldUnitWidth / 2;
			float visibleBottom = centerY() - Config.CameraWorldUnitHeight / 2;
			Rectangle visibleRect = new Rectangle(visibleLeft, visibleBottom, Config.CameraWorldUnitWidth, Config.CameraWorldUnitHeight);

			var tanks = world.getTanks();
			for (Tank tank : tanks) {
				if (tank.isOwnedByLocalPlayer() && visibleRect.overlaps(tank.bounds().getBoundingRectangle())) {
					canBeSeenByLocalPlayer = true;
				}
			}
		}
	}

	public boolean isVisibleToLocalPlayer() {
		return canBeSeenByLocalPlayer;
	}

	/**
	 * Makes this mine visible to the local player.
	 */
	public void makeVisibleToLocalPlayer() {
		canBeSeenByLocalPlayer = true;
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
		addCrater(world, x(), y());
	}

	private static void addExplosion(World world, float x, float y) {
		var args = new Entity.ConstructionArgs(Entity.nextId(), x, y, 0);
		world.addEntity(MineExplosion.class, args);
	}

	private static void removeTerrainImprovement(World world, int tileColumn, int tileRow) {
		var terrainImprovement = world.getTerrainImprovement(tileColumn, tileRow);
		if (terrainImprovement != null) {
			terrainImprovement.dispose();
		}
	}

	private static Crater addCrater(World world, float x, float y) {
		var args = new Entity.ConstructionArgs(Entity.nextId(), x, y, 0);
		return world.addEntity(Crater.class, args);
	}
}
