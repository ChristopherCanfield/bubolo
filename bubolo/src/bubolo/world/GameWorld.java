package bubolo.world;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

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
import java.util.stream.Collectors;

import bubolo.controllers.Controller;
import bubolo.controllers.ControllerFactory;
import bubolo.controllers.Controllers;
import bubolo.controllers.ai.AiTreeController;
import bubolo.util.Coords;
import bubolo.util.GameLogicException;
import bubolo.world.entity.OldEntity;
import bubolo.world.entity.OldTerrain;
import bubolo.world.entity.StationaryElement;
import bubolo.world.entity.concrete.Grass;
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
	private EntityCreationObserver entityCreationObserver;

	private final List<Entity> entities = new ArrayList<>();
	private final Map<UUID, Entity> entityMap = new HashMap<>();

	private final List<Tank> tanks = new ArrayList<>();
	private final List<ActorEntity> actors = new ArrayList<>();
	private final List<Spawn> spawns = new ArrayList<>();

	// first: column; second: row.
	private Tile[][] mapTiles = null;

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

	private int width;
	private int height;

	/**
	 * Constructs the GameWorld object.
	 *
	 * @param worldMapWidth
	 *            the width of the game world map.
	 * @param worldMapHeight
	 *            the height of the game world map.
	 */
	public GameWorld(int worldMapWidth, int worldMapHeight)
	{
		int tilesX = worldMapWidth / Coords.TILE_TO_WORLD_SCALE;
		int tilesY = worldMapHeight / Coords.TILE_TO_WORLD_SCALE;
		mapTiles = new Tile[tilesX][tilesY];

		this.width = worldMapWidth;
		this.height = worldMapHeight;

		addController(AiTreeController.class);
	}

	/**
	 * Constructs a default game world. This is intended for use by the network. The map's height
	 * and width must be set before calling the <code>update</code> method.
	 */
	public GameWorld()
	{
		this(0, 0);
	}

	@Override
	public void setEntityCreationObserver(EntityCreationObserver entityCreationObserver) {
		this.entityCreationObserver = entityCreationObserver;
	}



	@Override
	public void setHeight(int height)
	{
		checkArgument(height > 0, "height parameter must be greater than zero: %s", height);
		this.height = height;
	}

	@Override
	public void setWidth(int width)
	{
		checkArgument(width > 0, "width parameter must be greater than zero: %s", width);
		this.width = width;
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

		entitiesToAdd.add(entity);
		entityMap.put(entity.id(), entity);

		if (entityCreationObserver != null) {
			entityCreationObserver.onEntityCreated(entity);
		}

		return entity;
	}

	@Override
	public List<ActorEntity> getActors()
	{
		return actors;
	}

	@Override
	public Tile[][] getTiles()
	{
		return mapTiles;
	}

	@Override
	public Tile getTileFromWorldPosition(float worldX, float worldY) {
		int x = ((int) worldX) / Coords.TILE_TO_WORLD_SCALE;
		int y = ((int) worldY) / Coords.TILE_TO_WORLD_SCALE;

		var tile = mapTiles[x][y];
		assert tile != null;
		return tile;
	}

	@Override
	public void setTiles(Tile[][] mapTiles)
	{
		this.mapTiles = mapTiles;
		setWidth(mapTiles.length * Coords.TILE_TO_WORLD_SCALE);
		setHeight(mapTiles[0].length * Coords.TILE_TO_WORLD_SCALE);

		// Starting on 2/2021, Tiles can be created without an associated Terrain, in order to increase
		// the map importer's flexibility with slightly malformed, but otherwise valid, map files.
		// These lines add a Grass tile to any tile that is missing an associated terrain.
		for (Tile[] tiles : mapTiles) {
			for (Tile tile : tiles) {
				if (!tile.hasTerrain()) {
					tile.setTerrain(addEntity(Grass.class), this);
				}
			}
		}

		for (int i = 0; i < 2; i++) {
			for (Tile[] tiles : mapTiles) {
				for (Tile tile : tiles) {
					OldTerrain terrain = tile.getTerrain();
					updateTilingStateIfAdaptable(this, terrain);

					if (tile.hasElement()) {
						StationaryElement element = tile.getElement();
						updateTilingStateIfAdaptable(this, element);
					}
				}
			}
		}
	}

	private static void updateTilingStateIfAdaptable(World world, OldEntity e) {
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
		assert width / Coords.TILE_TO_WORLD_SCALE == mapTiles.length;
		return mapTiles.length;
	}

	@Override
	public int getTileRows() {
		assert height / Coords.TILE_TO_WORLD_SCALE == mapTiles[0].length;
		return mapTiles[0].length;
	}

	@Override
	public void update()
	{
		// Update all world controllers
		for (Controller c : worldControllers) {
			c.update(this);
		}

		// TODO (cdc - 2021-03-31): Remove these once world/entity refactoring is complete.
		checkState(width > 0,
				"worldMapWidth must be greater than 0. worldMapWidth: %s", width);
		checkState(height > 0,
				"worldMapHeight must be greater than 0. worldMapHeight: %s", height);

		// Update all actors.
		actors.forEach(actor -> actor.update(this));

		// Check for disposed entities.
		entitiesToRemove.addAll(entities.parallelStream()
				.filter(e -> e.isDisposed())
				.collect(Collectors.toList())
		);

		removeEntities(entitiesToRemove);
		entitiesToRemove.clear();

		// Update the tiling state of each entity to add, if applicable.
		if (adaptableRemovedThisTick) {
			adaptables.forEach(adaptable -> adaptable.updateTilingState(this));
		}
		adaptableRemovedThisTick = false;

		entities.addAll(entitiesToAdd);
		entitiesToAdd.clear();
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

			adaptableRemovedThisTick = adaptables.removeAll(markedForRemoval);
		}
	}

	@Override
	public List<Tank> getTanks()
	{
		List<Tank> copyOfTanks = Collections.unmodifiableList(tanks);
		return copyOfTanks;
	}

	@Override
	public List<Spawn> getSpawns()
	{
		List<Spawn> copyOfSpawns = Collections.unmodifiableList(spawns);
		return copyOfSpawns;
	}

	@Override
	public void addController(Class<? extends Controller> controllerType)
	{
		for (Controller c : worldControllers)
		{
			if (c.getClass() == controllerType)
			{
				return;
			}
		}

		try
		{
			worldControllers.add(controllerType.newInstance());
		}
		catch (InstantiationException | IllegalAccessException e)
		{
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
