package bubolo.world;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import bubolo.controllers.Controller;
import bubolo.controllers.ControllerFactory;
import bubolo.controllers.Controllers;
import bubolo.controllers.ai.AiTreeController;
import bubolo.util.Coords;
import bubolo.util.GameLogicException;
import bubolo.world.entity.Actor;
import bubolo.world.entity.Effect;
import bubolo.world.entity.OldEntity;
import bubolo.world.entity.StationaryElement;
import bubolo.world.entity.Terrain;
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

	private final List<OldEntity> entities = new ArrayList<OldEntity>();
	private final Map<UUID, OldEntity> entityMap = new HashMap<UUID, OldEntity>();

	// first: x; second: y.
	private Tile[][] mapTiles = null;

	// The list of entities to remove. The entities array can't be modified while it
	// is being iterated over.
	private final List<OldEntity> entitiesToRemove = new ArrayList<OldEntity>();

	// The list of entities to add. The entities array can't be modified while it is
	// being iterated over.
	private final List<OldEntity> entitiesToAdd = new ArrayList<OldEntity>();

	// the list of Tanks that exist in the world
	private final List<Tank> tanks = new ArrayList<Tank>();

	// list of world controllers
	private final List<Controller> worldControllers = new ArrayList<Controller>();

	// the list of all Effects that currently exist in the world
	private final List<OldEntity> effects = new ArrayList<OldEntity>();

	// the list of all Actors which currently exist in the world
	private final List<OldEntity> actors = new ArrayList<OldEntity>();

	// the list of all Spawn Locations currently in the world
	private final List<OldEntity> spawns = new ArrayList<OldEntity>();

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
	public OldEntity getEntity(UUID id) throws GameLogicException
	{
		OldEntity entity = entityMap.get(id);
		if (entity == null)
		{
			throw new GameLogicException(
					"The specified entity does not exist in the game world. Entity id: " + id);
		}
		return entity;
	}

	@Override
	public List<OldEntity> getEntities()
	{
		List<OldEntity> copyOfEntities = Collections.unmodifiableList(entities);
		return copyOfEntities;
	}

	@Override
	public <T extends OldEntity> T addEntity(Class<T> c) throws GameLogicException
	{
		return addEntity(c, UUID.randomUUID(), null);
	}

	@Override
	public <T extends OldEntity> T addEntity(Class<T> c, UUID id) throws GameLogicException
	{
		return addEntity(c, id, null);
	}

	@Override
	public <T extends OldEntity> T addEntity(Class<T> c, ControllerFactory controllerFactory)
			throws GameLogicException
	{
		return addEntity(c, UUID.randomUUID(), controllerFactory);
	}

	@Override
	public <T extends OldEntity> T addEntity(Class<T> c, UUID id, ControllerFactory controllerFactory)
			throws GameLogicException, IllegalStateException
	{
		if (entityMap.containsKey(id))
		{
			throw new GameLogicException("The specified entity already exists. Entity id: " + id +
					". Entity type: " + entityMap.get(id).getClass().getName());
		}

		T entity;
		try
		{
			entity = c.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new GameLogicException(e.getMessage());
		}

		entity.setId(id);

		Controllers.getInstance().createController(entity, controllerFactory);

		if (entity instanceof Tank tank)
		{
			tanks.add(tank);
		}

		if (entity instanceof Effect)
		{
			effects.add(entity);
		}

		if (entity instanceof Actor)
		{
			actors.add(entity);
		}

		if (entity instanceof Spawn)
		{
			spawns.add(entity);
		}

		if (entity instanceof Adaptable adaptable) {
			adaptables.add(adaptable);
			adaptableAddedThisTick = true;
		}

		entitiesToAdd.add(entity);
		entityMap.put(entity.getId(), entity);

		if (entityCreationObserver != null) {
			entityCreationObserver.onEntityCreated(entity);
		}

		return entity;
	}

	@Override
	public void removeEntity(OldEntity e)
	{
		e.dispose();
		entityMap.remove(e.getId());

		if (e instanceof Tank)
		{
			tanks.remove(e);
		}

		if (e instanceof Actor)
		{
			actors.remove(e);
		}

		if (e instanceof Effect)
		{
			effects.remove(e);
		}

		if (e instanceof Spawn)
		{
			spawns.remove(e);
		}

		if (e instanceof Adaptable adaptable) {
			adaptables.remove(adaptable);
			adaptableRemovedThisTick = true;
		}
	}

	@Override
	public List<OldEntity> getActors()
	{
		return actors;
	}

	@Override
	public List<OldEntity> getEffects()
	{
		return effects;
	}

	@Override
	public void removeEntity(UUID id) throws GameLogicException
	{
		removeEntity(entityMap.get(id));
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
					Terrain terrain = tile.getTerrain();
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
		for (Controller c : worldControllers)
		{
			c.update(this);
		}

		checkState(width > 0,
				"worldMapWidth must be greater than 0. worldMapWidth: %s", width);
		checkState(height > 0,
				"worldMapHeight must be greater than 0. worldMapHeight: %s", height);

		// Update all entities.
		for (OldEntity e : entities)
		{
			if (!e.isDisposed()) { e.update(this); }

			if (e.isDisposed())
			{
				entitiesToRemove.add(e);
			}
		}

		entities.removeAll(entitiesToRemove);
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

	@Override
	public List<Tank> getTanks()
	{
		List<Tank> copyOfTanks = Collections.unmodifiableList(tanks);
		return copyOfTanks;
	}

	@Override
	public List<OldEntity> getSpawns()
	{
		List<OldEntity> copyOfSpawns = Collections.unmodifiableList(spawns);
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
