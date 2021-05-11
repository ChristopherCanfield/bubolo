package bubolo.world;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import bubolo.Config;
import bubolo.controllers.Controller;
import bubolo.controllers.ControllerFactory;
import bubolo.controllers.Controllers;
import bubolo.util.Coords;
import bubolo.util.GameLogicException;
import bubolo.util.Nullable;
import bubolo.util.Timer;

/**
 * The concrete implementation of the World interface. GameWorld is the sole owner of Entity objects.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class GameWorld implements World {
	/**
	 * A tile address on the game map.
	 *
	 * @author Christopher D. Canfield
	 * @since 0.4.0
	 */
	private static record Tile(int column, int row) {
		Tile {
			assert column >= 0;
			assert row >= 0;
		}
	}

	private final List<EntityLifetimeObserver> entityLifetimeObservers = new ArrayList<>();

	private final List<Entity> entities = new ArrayList<>();
	private final List<Entity> entitiesUnmodifiableView = Collections.unmodifiableList(entities);
	private final Map<UUID, Entity> entityMap = new HashMap<>();

	private final List<Tank> tanks = new ArrayList<>();
	private final List<Tank> tanksUnmodifiableView = Collections.unmodifiableList(tanks);

	private final List<ActorEntity> actors = new ArrayList<>();
	private final List<ActorEntity> actorsUnmodifiableView = Collections.unmodifiableList(actors);

	private final List<Spawn> spawns = new ArrayList<>();
	private final List<Spawn> spawnsUnmodifiableView = Collections.unmodifiableList(spawns);

	// first: column; second: row.
	private Terrain[][] terrain;
	private final Map<Tile, TerrainImprovement> terrainImprovements = new HashMap<>();
	private final Map<Tile, Mine> mines = new HashMap<>();

	// The entities to remove.
	private final Set<Entity> entitiesToRemove = new HashSet<>();

	// The list of entities to add. The entities array can't be modified while it is
	// being iterated over.
	private final List<Entity> entitiesToAdd = new ArrayList<>();

	// The craters that will be flooded.
	private final Set<Crater> cratersToFlood = new HashSet<>(4);

	// List of world controllers.
	private final List<Controller> worldControllers = new ArrayList<>();

	private final Timer<World> timer = new Timer<World>(20);

	// This is used to update the tiling state of adaptables only when necessary, rather than every tick.
	// Reducing the number of calls to updateTilingState significantly reduced the time that update takes,
	// and reduced total memory usage (primarily by reducing a large number of boolean[] allocations).
	private List<EdgeMatchable> adaptables = new ArrayList<EdgeMatchable>();
	private boolean adaptableTileModified = false;

	// Width in world units.
	private final int width;
	// Height in world units.
	private final int height;

	private final Random randomGenerator = new Random();

	/**
	 * Constructs a GameWorld object.
	 *
	 * @param worldTileColumns the width of the game world map, in tiles. > 0 && <= Config.MaxWorldColumns.
	 * @param worldTileRows the height of the game world map, in tiles. > 0 && <= Config.MaxWorldRows.
	 */
	public GameWorld(int worldTileColumns, int worldTileRows) {
		assert worldTileColumns > 0;
		assert worldTileColumns <= Config.MaxWorldColumns;
		assert worldTileRows > 0;
		assert worldTileRows <= Config.MaxWorldRows;

		terrain = new Terrain[worldTileColumns][worldTileRows];

		width = worldTileColumns * Coords.TileToWorldScale;
		height = worldTileRows * Coords.TileToWorldScale;
	}

	@Override
	public void addEntityLifetimeObserver(EntityLifetimeObserver observer) {
		assert !entityLifetimeObservers.contains(observer) : "EntityLifetimeObserver " + observer.toString() + " was already added to the world.";

		entityLifetimeObservers.add(observer);
		observer.onObserverAddedToWorld(this);

		for (Entity e : entities) {
			observer.onEntityAdded(e);
		}
	}

	@Override
	public <T extends Entity> T addEntity(Class<T> c, Entity.ConstructionArgs args) throws GameLogicException {
		return addEntity(c, args, null);
	}

	@Override
	public <T extends Entity> T addEntity(Class<T> c, Entity.ConstructionArgs args, ControllerFactory controllerFactory)
			throws GameLogicException, IllegalStateException {
		if (entityMap.containsKey(args.id())) {
			throw new GameLogicException("The specified entity already exists. Entity id: " + args.id() + ". Entity type: "
					+ entityMap.get(args.id()).getClass().getName());
		}

		T entity;
		try {
			var constructor = c.getDeclaredConstructor(Entity.ConstructionArgs.class, World.class);
			entity = constructor.newInstance(args, this);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			String cause = (e.getCause() != null) ? e.getCause().toString() : "No cause reported.";
			throw new GameLogicException(String.format("%s: \n%s", e.toString(), cause));
		}

		assert entity.x() <= getWidth();
		assert entity.y() <= getHeight() : "Invalid entity y: " + entity.y() + "; Max height: " + getHeight();
		assert entity.tileColumn() < getTileColumns();
		assert entity.tileRow() < getTileRows() : String.format("Invalid tile row: %d. Max rows: %d. Position (%d,%d). Type: %s",
				entity.tileRow(), getTileRows(), entity.tileColumn(), entity.tileRow(), entity.getClass().getName());

		if (entity instanceof ActorEntity actor) {
			Controllers.getInstance().createController(actor, controllerFactory);
		}

		processNewSpawn(entity);

		entitiesToAdd.add(entity);
		entityMap.put(entity.id(), entity);

		for (var observer : entityLifetimeObservers) {
			observer.onEntityAdded(entity);
		}

		return entity;
	}

	private void processNewTank(Entity entity) {
		if (entity instanceof Tank tank) {
			tanks.add(tank);
		}
	}

	private void processNewActorEntity(Entity entity) {
		if (entity instanceof ActorEntity actor) {
			actors.add(actor);
		}
	}

	private void processNewSpawn(Entity entity) {
		if (entity instanceof Spawn spawn) {
			spawns.add(spawn);
		}
	}

	private void processNewAdaptable(Entity entity) {
		if (entity instanceof EdgeMatchable adaptable) {
			adaptables.add(adaptable);
			adaptableTileModified = true;
		}
	}

	private void processNewTerrain(Entity entity) {
		if (entity instanceof Terrain t) {
			Terrain existingTerrain = terrain[t.tileColumn()][t.tileRow()];
			if (existingTerrain != null) {
				assert existingTerrain.isDisposed()
						: String.format("Terrain %s added to tile (%d,%d), which already has a terrain: %s",
								t.getClass().getName(), t.tileColumn(), t.tileRow(), existingTerrain.getClass().getName());
			}

			terrain[t.tileColumn()][t.tileRow()] = t;

			markCratersAdjacentToWaterForFlooding(t);
		}
	}

	private void markCratersAdjacentToWaterForFlooding(Entity entity) {
		if (entity instanceof Water || entity instanceof DeepWater) {
			// Check adjacent columns.
			for (int column = entity.tileColumn() - 1; column < entity.tileColumn() + 2; column += 2) {
				addIfCrater(cratersToFlood, column, entity.tileRow());
			}

			// Check adjacent rows.
			for (int row = entity.tileRow() - 1; row < entity.tileRow() + 2; row += 2) {
				addIfCrater(cratersToFlood, entity.tileColumn(), row);
			}
		}
	}

	private void addIfCrater(Set<Crater> craters, int column, int row) {
		if (isValidTile(column, row)) {
			var terrainImprovement = getTerrainImprovement(column, row);
			if (terrainImprovement instanceof Crater crater) {
				craters.add(crater);
			}
		}
	}

	private void processNewTerrainImprovement(Entity entity) {
		if (entity instanceof TerrainImprovement terrainImprovement) {
			// Check for mutually exclusive combinations.
			assert !(terrainImprovement instanceof Terrain);
			assert !(terrainImprovement instanceof Mine);

			Terrain t = terrain[entity.tileColumn()][entity.tileRow()];
			assert t == null || t.isValidBuildTarget() : String.format(
					"Invalid target tile (%d,%d) for terrain improvement %s. Terrain %s is not a valid build target.",
					t.tileColumn(), t.tileRow(), entity.getClass().getSimpleName(), t.getClass().getSimpleName());

			// Add the terrain improvement. If one already exists, ensure that it has been disposed.
			Tile tile = new Tile(entity.tileColumn(), entity.tileRow());
			TerrainImprovement existingTerrainImprovement = terrainImprovements.get(tile);
			if (existingTerrainImprovement != null) {
				assert ((Entity) existingTerrainImprovement).isDisposed()
						: String.format("TerrainImprovement %s added to tile (%d,%d), which already has an improvement: %s",
								terrainImprovement.getClass().getName(), entity.tileColumn(), entity.tileRow(),
								existingTerrainImprovement.getClass().getName());
			}
			terrainImprovements.put(tile, terrainImprovement);
		}
	}

	private void processNewMine(Entity entity) {
		if (entity instanceof Mine mine) {
			// Add the terrain improvement. If one already exists, ensure that it has been disposed.
			Tile tile = new Tile(entity.tileColumn(), entity.tileRow());
			Mine existingMine = mines.get(tile);
			if (existingMine != null) {
				assert mine.isDisposed() : String.format("Mine added to tile (%d,%d), which already has a mine.",
						mine.tileColumn(), mine.tileRow());
			}
			mines.put(tile, mine);
		}
	}

	@Override
	public <T extends Terrain> void populateEmptyTilesWith(Class<T> terrainType) {
		for (int column = 0; column < getTileColumns(); column++) {
			for (int row = 0; row < getTileRows(); row++) {
				if (terrain[column][row] == null) {
					float x = column * Coords.TileToWorldScale;
					float y = row * Coords.TileToWorldScale;
					var args = new Entity.ConstructionArgs(Entity.nextId(), x, y, 0);
					addEntity(terrainType, args);
				}
			}
		}
	}

	@Override
	public Terrain getTerrain(int column, int row) {
		assert column >= 0 && column < getTileColumns() && row >= 0 && row < getTileRows()
				: String.format("Invalid terrain: %d,%d; max terrain is %d,%d.", column, row, getTileColumns(), getTileRows());

		return terrain[column][row];
	}

	@Override
	public TerrainImprovement getTerrainImprovement(int column, int row) {
		return terrainImprovements.get(new Tile(column, row));
	}

	@Override
	public Mine getMine(int column, int row) {
		return mines.get(new Tile(column, row));
	}

	@Override
	public Timer<World> timer() {
		return timer;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getTileColumns() {
		return terrain.length;
	}

	@Override
	public int getTileRows() {
		return terrain[0].length;
	}

	@Override
	public void update() {
		timer.update(this);

		// Update all world controllers
		for (Controller c : worldControllers) {
			c.update(this);
		}

		// Update all non-disposed actors.
		for (var actor : actors) {
			if (!actor.isDisposed()) {
				actor.update(this);
			}
		}

		// Check for disposed entities.
		entitiesToRemove.addAll(entities.stream().filter(e -> e.isDisposed()).toList());

		removeEntities(entitiesToRemove);
		entitiesToRemove.clear();

		if (!entitiesToAdd.isEmpty()) {
			entities.addAll(entitiesToAdd);
			// Sort by type.
			entities.sort(
					(leftEntity, rightEntity) -> leftEntity.getClass().getName().compareTo(rightEntity.getClass().getName()));

			for (Entity entity : entitiesToAdd) {
				processNewTank(entity);
				processNewActorEntity(entity);
				processNewAdaptable(entity);
				processNewTerrain(entity);
				processNewTerrainImprovement(entity);
				processNewMine(entity);
			}
			entitiesToAdd.clear();
		}

		// Add timers for any craters that may flood.
		for (Crater crater : cratersToFlood) {
			timer().scheduleSeconds(4, w -> {
				crater.replaceWithWater(w);
			});
		}
		cratersToFlood.clear();

		if (adaptableTileModified) {
			adaptables.forEach(adaptable -> adaptable.updateTilingState(this));
		}
		adaptableTileModified = false;
	}

	/**
	 * Removes a collection of entities from the game world. Must not be called during iteration of the entities, tanks, actors,
	 * spawns, or adaptables lists.
	 *
	 * @param markedForRemoval a collection of entities to remove.
	 */
	@SuppressWarnings("unlikely-arg-type")
	private void removeEntities(Collection<Entity> markedForRemoval) {
		if (!markedForRemoval.isEmpty()) {
			entities.removeAll(markedForRemoval);
			entityMap.values().removeAll(markedForRemoval);

			terrainImprovements.values().removeAll(markedForRemoval);

			tanks.removeAll(markedForRemoval);
			actors.removeAll(markedForRemoval);
			spawns.removeAll(markedForRemoval);
			mines.values().removeAll(markedForRemoval);

			// Notify the lifetime observers.
			for (var entity : markedForRemoval) {
				for (var observer : entityLifetimeObservers) {
					observer.onEntityRemoved(entity);
				}
			}

			var adaptablesToRemove = markedForRemoval.stream().filter(e -> e instanceof EdgeMatchable).map(e -> (EdgeMatchable) e)
					.toList();
			adaptableTileModified = adaptableTileModified || adaptables.removeAll(adaptablesToRemove);
		}
	}

	@Override
	public Entity getEntity(UUID id) throws GameLogicException {
		Entity entity = entityMap.get(id);
		if (entity == null) {
			throw new GameLogicException("The specified entity does not exist in the game world. Entity id: " + id);
		}
		return entity;
	}

	@Override
	public List<Entity> getEntities() {
		return entitiesUnmodifiableView;
	}

	@Override
	public List<Tank> getTanks() {
		return tanksUnmodifiableView;
	}

	@Override
	public List<ActorEntity> getActors() {
		return actorsUnmodifiableView;
	}

	@Override
	public List<Spawn> getSpawns() {
		return spawnsUnmodifiableView;
	}

	@Override
	public Spawn getRandomSpawn() {
		assert !spawns.isEmpty();
		return spawns.get(randomGenerator.nextInt(spawns.size()));
	}

	@Override
	public List<Collidable> getNearbyCollidables(Entity entity, boolean onlyIncludeSolidObjects) {
		return getNearbyCollidables(entity, onlyIncludeSolidObjects, null);
	}

	@Override
	public List<Collidable> getNearbyCollidables(Entity entity, boolean onlyIncludeSolidObjects, @Nullable Class<?> typeFilter) {
		final int tileMaxDistance = 3;
		return getNearbyCollidables(entity, onlyIncludeSolidObjects, tileMaxDistance, typeFilter);
	}

	@Override
	public List<Collidable> getNearbyCollidables(Entity targetEntity, boolean onlyIncludeSolidObjects, int tileMaxDistance,
			@Nullable Class<?> typeFilter) {
		assert tileMaxDistance >= 0;

		final int startTileColumn = targetEntity.tileColumn() - tileMaxDistance;
		final int startTileRow = targetEntity.tileRow() - tileMaxDistance;

		final int endTileColumn = targetEntity.tileColumn() + tileMaxDistance;
		final int endTileRow = targetEntity.tileRow() + tileMaxDistance;

		List<Collidable> nearbyCollidables = new ArrayList<>();
		for (int column = startTileColumn; column <= endTileColumn; column++) {
			for (int row = startTileRow; row <= endTileRow; row++) {
				if (isValidTile(column, row)) {
					TerrainImprovement ti = terrainImprovements.get(new Tile(column, row));
					if (includeInNearbyCollidablesList((Entity) ti, onlyIncludeSolidObjects, typeFilter)) {
						nearbyCollidables.add((Collidable) ti);
					}
				}
			}
		}

		// Iterate through every actor to determine which ones are nearby. There's really no better way to do this
		// currently; if it becomes a bottleneck, I'll look into optimizing it.
		for (ActorEntity actor : actors) {
			if (!actor.equals(targetEntity)
					&& isEntityWithinTileRange(actor, startTileColumn, endTileColumn, startTileRow, endTileRow)
					&& includeInNearbyCollidablesList(actor, onlyIncludeSolidObjects, typeFilter)) {
				nearbyCollidables.add(actor);
			}
		}

		return nearbyCollidables;
	}

	private static boolean includeInNearbyCollidablesList(Entity e, boolean onlyIncludeSolidObjects,
			@Nullable Class<?> typeFilter) {
		if (e instanceof Collidable c) {
			var result = (!onlyIncludeSolidObjects || c.isSolid()) && (typeFilter == null || typeFilter.isInstance(c));
			return result;
		}
		return false;
	}

	private static boolean isEntityWithinTileRange(Entity e, int minColumn, int maxColumn, int minRow, int maxRow) {
		int col = e.tileColumn();
		int row = e.tileRow();
		return col >= minColumn && col <= maxColumn && row >= minRow && row <= maxRow;
	}

	@Override
	public boolean isValidTile(int column, int row) {
		return column >= 0 && column < getTileColumns() && row >= 0 && row < getTileRows();
	}

	@Override
	public boolean isTileAdjacentToWater(int column, int row) {
		boolean adjacentToWater = false;
		if (isValidTile(column - 1, row)) {
			adjacentToWater = adjacentToWater || isWater(column - 1, row);
		}
		if (isValidTile(column + 1, row)) {
			adjacentToWater = adjacentToWater || isWater(column + 1, row);
		}

		if (isValidTile(column, row - 1)) {
			adjacentToWater = adjacentToWater || isWater(column, row - 1);
		}
		if (isValidTile(column, row + 1)) {
			adjacentToWater = adjacentToWater || isWater(column, row + 1);
		}

		return adjacentToWater;
	}

	private boolean isWater(int column, int row) {
		Terrain terrain = getTerrain(column, row);
		return Terrain.isWater(terrain);
	}

	@Override
	public void addController(Class<? extends Controller> controllerType) {
		for (Controller c : worldControllers) {
			if (c.getClass() == controllerType) {
				return;
			}
		}

		try {
			worldControllers.add(controllerType.getConstructor().newInstance());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new GameLogicException(e);
		}
	}

	@Override
	public void removeController(Class<? extends Controller> controllerType) {
		for (Controller c : worldControllers) {
			if (c.getClass() == controllerType) {
				worldControllers.remove(c);
				return;
			}
		}
	}

	@Override
	public int getControllerCount() {
		return worldControllers.size();
	}
}
