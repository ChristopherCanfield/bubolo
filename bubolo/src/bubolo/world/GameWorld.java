package bubolo.world;

import static bubolo.util.Units.TileToWorldScale;

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
import java.util.function.Predicate;

import bubolo.Config;
import bubolo.controllers.Controller;
import bubolo.controllers.ControllerFactory;
import bubolo.controllers.Controllers;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.DestroyEntity;
import bubolo.util.GameLogicException;
import bubolo.util.Nullable;
import bubolo.util.Timer;
import bubolo.util.Units;

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
	private final Terrain[][] terrain;
	private final TerrainImprovement[][] terrainImprovements;
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
		terrainImprovements = new TerrainImprovement[worldTileColumns][worldTileRows];

		width = worldTileColumns * Units.TileToWorldScale;
		height = worldTileRows * Units.TileToWorldScale;
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

			var col = entity.tileColumn();
			var row = entity.tileRow();
			Terrain t = terrain[col][row];
			assert t == null || t.isValidBuildTarget() : String.format(
					"Invalid target tile (%d,%d) for terrain improvement %s. Terrain %s is not a valid build target.",
					t.tileColumn(), t.tileRow(), entity.getClass().getSimpleName(), t.getClass().getSimpleName());

			// Add the terrain improvement. If one already exists, ensure that it has been disposed.
			TerrainImprovement existingTerrainImprovement = terrainImprovements[entity.tileColumn()][entity.tileRow()];
			if (existingTerrainImprovement != null) {
				assert ((Entity) existingTerrainImprovement).isDisposed()
						: String.format("TerrainImprovement %s added to tile (%d,%d), which already has an improvement: %s",
								terrainImprovement.getClass().getName(), entity.tileColumn(), entity.tileRow(),
								existingTerrainImprovement.getClass().getName());
			}
			terrainImprovements[col][row] = terrainImprovement;
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
					float x = column * Units.TileToWorldScale;
					float y = row * Units.TileToWorldScale;
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
		assert isValidTile(column, row) :
				String.format("Invalid terrain improvement location: %d,%d.", column, row);
		return terrainImprovements[column][row];
	}

	@Override
	public void movePillboxOffTileMap(Pillbox pillbox) {
		var existingPillbox = terrainImprovements[pillbox.tileColumn()][pillbox.tileRow()];
		if (existingPillbox == null) {
			// Search for the pillbox, and remove it if found.
			removePillboxFromTerrainImprovementsArray(pillbox.id());
		}
//		assert pillbox.equals(existingPillbox) : String.format("movePillboxOffTileMap: pillbox was not in expected tile location (%d,%d). Found: %s",
//				pillbox.tileColumn(),
//				pillbox.tileRow(),
//				String.valueOf(existingPillbox));
		terrainImprovements[pillbox.tileColumn()][pillbox.tileRow()] = null;
	}

	// @HACK (cdc 2021-06-08): This is a hack to fix a network crashing issue with moving pillboxes.
	private void removePillboxFromTerrainImprovementsArray(UUID id) {
		for (int column = 0; column < terrainImprovements.length; column++) {
			for (int row = 0; row < terrainImprovements[0].length; row++) {
				var improvement = terrainImprovements[column][row];
				if (improvement != null && improvement.id().equals(id)) {
					terrainImprovements[column][row] = null;
					return;
				}
			}
		}
		System.out.println("removePillboxFromTerrainImprovementsArray: Unable to find pillbox " + id);
	}

	@Override
	public void movePillboxOntoTileMap(Pillbox pillbox, int column, int row) {
		var terrainImprovement = terrainImprovements[column][row];
		if (terrainImprovement != null) {
			if (terrainImprovement.isValidBuildTarget()) {
				terrainImprovement.dispose();
			} else {
				throw new GameLogicException(String.format("Invalid tile location selected for GameWorld.movePillboxOntoTileMap (%d,%d). " +
						"It contains a terrain improvement that can't be built on.",
						column, row));
			}
		}

		terrainImprovements[column][row] = pillbox;
		pillbox.setPosition(column * TileToWorldScale, row * TileToWorldScale);
	}

	@Override
	public Mine getMine(int column, int row) {
		var mine = mines.get(new Tile(column, row));
		if (mine != null && !mine.isDisposed()) {
			return mine;
		} else {
			return null;
		}
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

		// Start the flooding process for craters that are adjacent to water.
		for (Crater crater : cratersToFlood) {
			crater.flood(this);
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
	private void removeEntities(Collection<Entity> markedForRemoval) {
		if (!markedForRemoval.isEmpty()) {
			entities.removeAll(markedForRemoval);
			entityMap.values().removeAll(markedForRemoval);

			tanks.removeAll(markedForRemoval);
			actors.removeAll(markedForRemoval);
			spawns.removeAll(markedForRemoval);
			mines.values().removeAll(markedForRemoval);

			for (var toBeRemoved : markedForRemoval) {
				// Remove if terrain improvement
				if (toBeRemoved instanceof TerrainImprovement) {
					var col = toBeRemoved.tileColumn();
					var row = toBeRemoved.tileRow();
					if (toBeRemoved == terrainImprovements[col][row]) {
						terrainImprovements[col][row] = null;
					}
				}

				// Notify lifetime observers.
				for (var observer : entityLifetimeObservers) {
					observer.onEntityRemoved(toBeRemoved);
				}
			}

			// Notify the network players.
			Network network = NetworkSystem.getInstance();
			markedForRemoval.stream().filter(e -> !(e instanceof Bullet && !(e instanceof Mine))).forEach(e -> {
				network.send(new DestroyEntity(e.id()));
			});

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
	public @Nullable Entity getEntityOrNull(UUID id) {
		return entityMap.get(id);
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
	public List<Collidable> getCollidablesWithinTileDistance(Entity entity, int tileMaxDistance, boolean onlyIncludeSolidObjects) {
		return getCollidablesWithinTileDistance(entity, tileMaxDistance, onlyIncludeSolidObjects);
	}

	@Override
	public List<Collidable> getCollidablesWithinTileDistance(Entity entity, int tileMaxDistance, boolean onlyIncludeSolidObjects,
			@Nullable Class<?> typeFilter) {
		assert tileMaxDistance >= 0;

		final int startTileColumn = entity.tileColumn() - tileMaxDistance;
		final int startTileRow = entity.tileRow() - tileMaxDistance;

		final int endTileColumn = entity.tileColumn() + tileMaxDistance;
		final int endTileRow = entity.tileRow() + tileMaxDistance;

		List<Collidable> nearbyCollidables = new ArrayList<>();
		for (int column = startTileColumn; column <= endTileColumn; column++) {
			for (int row = startTileRow; row <= endTileRow; row++) {
				if (isValidTile(column, row)) {
					TerrainImprovement ti = terrainImprovements[column][row];
					if (includeInNearbyCollidablesList((Entity) ti, onlyIncludeSolidObjects, typeFilter)) {
						nearbyCollidables.add((Collidable) ti);
					}
				}
			}
		}

		// Iterate through every actor to determine which ones are nearby. There's really no better way to do this
		// currently; if it becomes a bottleneck, I'll look into optimizing it.
		for (ActorEntity actor : actors) {
			if (!actor.equals(entity)
					&& !(actor instanceof TerrainImprovement)
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
						// Only include solid objects, if requested.
			var result = (!onlyIncludeSolidObjects || c.isSolid())
					// Filter for instances of the passed in type, if any.
					&& (typeFilter == null || typeFilter.isInstance(c))
					// Don't include pillboxes that are being carried.
					&& !(e instanceof Pillbox pillbox && pillbox.isBeingCarried());
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
	public Terrain getNearestBuildableTerrain(float x, float y) {
		final int tileMaxDistance = 10;

		final int initialColumn = (int) (x / TileToWorldScale);
		final int initialRow = (int) (y / TileToWorldScale);

		Terrain terrain = findTerrainWithinTileRange(initialColumn, initialRow, tileMaxDistance, t -> {
			if (t.isValidBuildTarget()) {
				var improvement = getTerrainImprovement(t.tileColumn(), t.tileRow());
				if (improvement != null) {
					return improvement.isValidBuildTarget();
				} else {
					return true;
				}
			}
			return false;
		});
		assert terrain != null;
		return terrain;
	}

	/**
	 * Finds a terrain that meets a requirement within a specified distance from a target tile. The predicate function determines
	 * whether a given terrain that is within the range is returned. The terrain are checked in order of tile distance, with
	 * nearer tiles being checked first. If no relevant terrain is found, null is returned.
	 *
	 * @param startTileCol the initial tile's column. The initial tile is checked.
	 * @param startTileRow the initial tile's row. The initial tile is checked.
	 * @param maxTileDistance the max distance (inclusive) to check from the start tile.
	 * @param pred determines whether a given entity should be returned.
	 * @return the nearest terrain that that fulfills the requirements of the predicate, or null if no relevant terrain was found.
	 */
	private @Nullable Terrain findTerrainWithinTileRange(int startTileCol, int startTileRow, int maxTileDistance, Predicate<Terrain> pred) {
		// Initial tile.
		Terrain terrain = returnTerrainIfMeetsRequirements(startTileCol, startTileRow, pred);
		if (terrain != null) { return terrain; }

		for (int distance = 1; distance <= maxTileDistance; distance++) {
			// North
			terrain = returnTerrainIfMeetsRequirements(startTileCol, startTileRow + distance, pred);
			if (terrain != null) { return terrain; }

			// South
			terrain = returnTerrainIfMeetsRequirements(startTileCol, startTileRow - distance, pred);
			if (terrain != null) { return terrain; }

			// East
			terrain = returnTerrainIfMeetsRequirements(startTileCol + distance, startTileRow, pred);
			if (terrain != null) { return terrain; }

			// West
			terrain = returnTerrainIfMeetsRequirements(startTileCol - distance, startTileRow, pred);
			if (terrain != null) { return terrain; }

			// Northeast
			terrain = returnTerrainIfMeetsRequirements(startTileCol + distance, startTileRow + distance, pred);
			if (terrain != null) { return terrain; }

			// Northwest
			terrain = returnTerrainIfMeetsRequirements(startTileCol - distance, startTileRow + distance, pred);
			if (terrain != null) { return terrain; }

			// Southeast
			terrain = returnTerrainIfMeetsRequirements(startTileCol + distance, startTileRow - distance, pred);
			if (terrain != null) { return terrain; }

			// Southwest
			terrain = returnTerrainIfMeetsRequirements(startTileCol - distance, startTileRow - distance, pred);
			if (terrain != null) { return terrain; }
		}

		return null;
	}

	private @Nullable Terrain returnTerrainIfMeetsRequirements(int col, int row, Predicate<Terrain> pred) {
		if (isValidTile(col, row)) {
			var terrain = getTerrain(col, row);
			if (pred.test(terrain)) {
				return terrain;
			}
		}
		return null;
	}

	@Override
	public int getTileDistanceToDeepWater(int tileColumn, int tileRow, int maximumDistanceTiles) {
		for (int distance = 1; distance <= maximumDistanceTiles; distance++) {
			if (isTileDeepWater(tileColumn + distance, tileRow)
					|| isTileDeepWater(tileColumn - distance, tileRow)
					|| isTileDeepWater(tileColumn + distance, tileRow + distance)
					|| isTileDeepWater(tileColumn + distance, tileRow - distance)
					|| isTileDeepWater(tileColumn - distance, tileRow + distance)
					|| isTileDeepWater(tileColumn - distance, tileRow - distance)
					|| isTileDeepWater(tileColumn, tileRow + distance)
					|| isTileDeepWater(tileColumn, tileRow - distance)) {
				return distance;
			}
		}
		return -1;
	}

	private boolean isTileDeepWater(int tileColumn, int tileRow) {
		if (isValidTile(tileColumn, tileRow)) {
			return (getTerrain(tileColumn, tileRow) instanceof DeepWater);
		}
		return false;
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
