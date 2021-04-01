package bubolo.world;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import bubolo.controllers.Controller;
import bubolo.controllers.ControllerFactory;
import bubolo.controllers.Controllers;
import bubolo.controllers.ai.AiTreeController;
import bubolo.util.Coords;
import bubolo.util.GameLogicException;
import bubolo.world.entity.concrete.Mine;
import bubolo.world.entity.concrete.Spawn;
import bubolo.world.entity.concrete.Tank;

/**
 * The concrete implementation of the World interface. GameWorld is the sole owner of Entity
 * objects.
 *
 * @author BU CS673 - Clone Productions
 */
public class GameWorld implements World
{
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

	private EntityCreationObserver entityCreationObserver;

	private final List<Entity> entities = new ArrayList<>();
	private final Map<UUID, Entity> entityMap = new HashMap<>();

	private final List<Tank> tanks = new ArrayList<>();
	private final List<ActorEntity> actors = new ArrayList<>();
	private final List<Spawn> spawns = new ArrayList<>();

	// first: column; second: row.
	private Terrain[][] terrain;
	private Map<Tile, TerrainImprovement> terrainImprovements = new HashMap<>();
	private Map<Tile, Mine> mines = new HashMap<>();

	// The entities to remove.
	private final Set<Entity> entitiesToRemove = new HashSet<>();

	// The list of entities to add. The entities array can't be modified while it is
	// being iterated over.
	private final List<Entity> entitiesToAdd = new ArrayList<>();

	// list of world controllers
	private final List<Controller> worldControllers = new ArrayList<>();

	// These are used to only update the tiling state of adaptables when necessary, rather than every tick.
	// Reducing the number of calls to updateTilingState significantly reduced the time that update takes,
	// and reduced total memory usage (primarily by reducing a large number of boolean[] allocations).
	private List<Adaptable> adaptables = new ArrayList<Adaptable>();
	private boolean adaptableRemovedThisTick = false;
	private boolean adaptableAddedThisTick = false;

	// Width in world units.
	private final int width;
	// Height in world units.
	private final int height;

	/**
	 * Constructs a GameWorld object.
	 *
	 * @param worldTileColumns the width of the game world map, in tiles.
	 * @param worldTileRows the height of the game world map, in tiles.
	 */
	public GameWorld(int worldTileColumns, int worldTileRows)
	{
		assert(worldTileColumns > 0);
		assert(worldTileRows > 0);

		assert worldTileColumns < 2_500 : "Unlikely worldTileColumns value passed to GameWorld: " + worldTileColumns;
		assert worldTileRows < 2_500 : "Unlikely worldTileRows value passed to GameWorld: " + worldTileRows;

		terrain = new Terrain[worldTileColumns][worldTileRows];

		width = worldTileColumns * Coords.TILE_TO_WORLD_SCALE;
		height = worldTileRows * Coords.TILE_TO_WORLD_SCALE;

		addController(AiTreeController.class);
	}

	@Override
	public void setEntityCreationObserver(EntityCreationObserver entityCreationObserver) {
		this.entityCreationObserver = entityCreationObserver;
	}

	@Override
	public Entity getEntity(UUID id) throws GameLogicException
	{
		Entity entity = entityMap.get(id);
		if (entity == null)
		{
			throw new GameLogicException(
					"The specified entity does not exist in the game world. Entity id: " + id);
		}
		return entity;
	}

	@Override
	public List<Entity> getEntities()
	{
		List<Entity> copyOfEntities = Collections.unmodifiableList(entities);
		return copyOfEntities;
	}

	@Override
	public <T extends Entity> T addEntity(Class<T> c, Entity.ConstructionArgs args) throws GameLogicException
	{
		return addEntity(c, args, null);
	}

	@Override
	public <T extends Entity> T addEntity(Class<T> c, Entity.ConstructionArgs args, ControllerFactory controllerFactory) throws GameLogicException, IllegalStateException
	{
		if (entityMap.containsKey(args.id())) {
			throw new GameLogicException("The specified entity already exists. Entity id: " + args.id()
					+ ". Entity type: " + entityMap.get(args.id()).getClass().getName());
		}

		T entity;
		try {
			var constructor = c.getDeclaredConstructor(Entity.ConstructionArgs.class);
			entity = constructor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			throw new GameLogicException(e.getMessage());
		}

		if (entity instanceof Tank tank) {
			tanks.add(tank);
		}

		if (entity instanceof ActorEntity actor) {
			actors.add(actor);
			Controllers.getInstance().createController(actor, controllerFactory);
		}

		if (entity instanceof Spawn spawn) {
			spawns.add(spawn);
		}

		if (entity instanceof Adaptable adaptable) {
			adaptables.add(adaptable);
			adaptableAddedThisTick = true;
		}

		if (entity instanceof Terrain t) {
			// TODO (cdc - 2021-04-01): Add this to the terrain array. If a terrain already exists in this location, dispose and replace it in the array.
			// If a TerrainImprovement or Mine was associated with the previous terrain
		}

		if (entity instanceof TerrainImprovement terrainImprovement) {
			// Check for mutually exclusive combinations.
			assert !(terrainImprovement instanceof Terrain);
			assert !(terrainImprovement instanceof Mine);
			// TODO (cdc - 2021-04-01): Add this to the terrain improvements map.
		}

		if (entity instanceof Mine mine) {
			// TODO (cdc - 2021-04-01): Add this to the mines map.
		}

		entitiesToAdd.add(entity);
		entityMap.put(entity.id(), entity);

		if (entityCreationObserver != null) {
			entityCreationObserver.onEntityCreated(entity);
		}

		return entity;
	}

	@Override
	public Terrain getTerrain(int column, int row) {
		return terrain[column][row];
	}

	@Override
	public Terrain getTerrainFromWorldPosition(float worldX, float worldY) {
		int column = (int) worldX / Coords.TILE_TO_WORLD_SCALE;
		int row = (int) worldY / Coords.TILE_TO_WORLD_SCALE;

		var t = terrain[column][row];
		assert t != null;
		return t;
	}

//	@Override
//	public void setTiles(Tile[][] mapTiles)
//	{
//		this.mapTiles = mapTiles;
//		setWidth(mapTiles.length * Coords.TILE_TO_WORLD_SCALE);
//		setHeight(mapTiles[0].length * Coords.TILE_TO_WORLD_SCALE);
//
//		// Starting on 2/2021, Tiles can be created without an associated Terrain, in order to increase
//		// the map importer's flexibility with slightly malformed, but otherwise valid, map files.
//		// These lines add a Grass tile to any tile that is missing an associated terrain.
//		for (Tile[] tiles : mapTiles) {
//			for (Tile tile : tiles) {
//				if (!tile.hasTerrain()) {
//					tile.setTerrain(addEntity(Grass.class), this);
//				}
//			}
//		}
//
//		for (int i = 0; i < 2; i++) {
//			for (Tile[] tiles : mapTiles) {
//				for (Tile tile : tiles) {
//					OldTerrain terrain = tile.getTerrain();
//					updateTilingStateIfAdaptable(this, terrain);
//
//					if (tile.hasElement()) {
//						StationaryElement element = tile.getElement();
//						updateTilingStateIfAdaptable(this, element);
//					}
//				}
//			}
//		}
//	}

	private static void updateTilingStateIfAdaptable(World world, Entity e) {
		if (e instanceof Adaptable adaptable) {
			adaptable.updateTilingState(world);
		}
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
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
	public void update()
	{
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
		entitiesToRemove.addAll(entities.stream()
				.filter(e -> e.isDisposed())
				.toList()
		);

		removeEntities(entitiesToRemove);
		entitiesToRemove.clear();

		// Update the tiling state of each entity to add, if applicable.
		if (adaptableRemovedThisTick) {
			adaptables.forEach(adaptable -> adaptable.updateTilingState(this));
		}
		adaptableRemovedThisTick = false;

		if (!entitiesToAdd.isEmpty()) {
			entities.addAll(entitiesToAdd);
			// Sort by type.
			entities.sort((leftEntity, rightEntity) -> leftEntity.getClass().getName().compareTo(rightEntity.getClass().getName()));
			entitiesToAdd.clear();
		}

		if (adaptableAddedThisTick) {
			adaptables.forEach(adaptable -> adaptable.updateTilingState(this));
		}
		adaptableAddedThisTick = false;
	}

	/**
	 * Removes a collection of entities from the game world. Must not be called during iteration of
	 * the entities, tanks, actors, spawns, or adaptables lists.
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

			var adaptablesToRemove = markedForRemoval.stream()
					.filter(e -> e instanceof Adaptable)
					.map(e -> (Adaptable) e)
					.toList();
			adaptableRemovedThisTick = adaptables.removeAll(adaptablesToRemove);
		}
	}

	@Override
	public List<Tank> getTanks()
	{
		return Collections.unmodifiableList(tanks);
	}

	@Override
	public List<ActorEntity> getActors()
	{
		return Collections.unmodifiableList(actors);
	}

	@Override
	public List<Spawn> getSpawns()
	{
		return Collections.unmodifiableList(spawns);
	}

	@Override
	public void addController(Class<? extends Controller> controllerType)
	{
		for (Controller c : worldControllers) {
			if (c.getClass() == controllerType) {
				return;
			}
		}

		try {
			worldControllers.add(controllerType.getConstructor().newInstance());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new GameLogicException(e);
		}
	}

	@Override
	public void removeController(Class<? extends Controller> controllerType)
	{
		for (Controller c : worldControllers)
		{
			if (c.getClass() == controllerType)
			{
				worldControllers.remove(c);
				return;
			}
		}
	}

	@Override
	public int getControllerCount()
	{
		return worldControllers.size();
	}
}
