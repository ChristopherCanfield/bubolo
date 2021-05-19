package bubolo.world;

import java.util.List;
import java.util.UUID;

import bubolo.controllers.Controller;
import bubolo.controllers.ControllerFactory;
import bubolo.util.GameLogicException;
import bubolo.util.Nullable;
import bubolo.util.Timer;

/**
 * Stores and processes game entities.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public interface World {
	/**
	 * Returns an entity from an entity ID. Throws a GameLogicException if the entity is not found.
	 *
	 * @param id the entity's unique id.
	 * @return the requested entity.
	 * @throws GameLogicException if the entity is not found.
	 */
	public Entity getEntity(UUID id) throws GameLogicException;

	/**
	 * Returns an entity from an entity ID, or null if the entity does not exist.
	 *
	 * @param id the entity's unique id.
	 * @return the requested entity, or null if the entity does not exist.
	 */
	public @Nullable Entity getEntityOrNull(UUID id);

	/**
	 * Returns an unmodifiable view of all entities in the world.
	 *
	 * @return the list of entities.
	 */
	public List<Entity> getEntities();

	/**
	 * Returns an unmodifiable view of all tanks in the world.
	 *
	 * @return the list of tanks.
	 */
	public List<Tank> getTanks();

	/**
	 * Returns an unmodifiable view of all Spawn Locations in the world.
	 *
	 * @return the list of spawns.
	 */
	public List<Spawn> getSpawns();

	/**
	 * @return a randomly selected spawn location.
	 */
	public Spawn getRandomSpawn();

	/**
	 * Returns an unmodifiable view of all actors in the world.
	 *
	 * @return the list of actors.
	 */
	public List<ActorEntity> getActors();

	public Timer<World> timer();

	/**
	 * Constructs and adds an entity to the world, and returns a reference to the newly constructed entity.
	 *
	 * The following actions are performed:
	 * <ol>
	 * <li>A new Entity of the specified type is created.</li>
	 * <li>The new Entity is added to the World</li>
	 * <li>A new Sprite is created and added to the Sprites list.</li>
	 * <li>One or more Controllers are created and added to the Controllers list.</li>
	 * </ol>
	 *
	 * @param c the entity's class object. For example, to create a new Tank, call this method using the following form:
	 *     <code>World.addEntity(Tank.class, args).</code>
	 * @param args the entity's construction arguments.
	 * @return reference to the new entity.
	 * @throws GameLogicException if the entity cannot be instantiated, or if the UUID already belongs to an entity.
	 */
	public <T extends Entity> T addEntity(Class<T> c, Entity.ConstructionArgs args) throws GameLogicException;

	/**
	 * @see World#addEntity(Class, Entity.ConstructionArgs)
	 * @param c the entity's class object. For example, to create a new Tank, call this method using the following form:
	 *     <code>World.addEntity(Tank.class, args).</code>
	 * @param args the entity's construction arguments.
	 * @param controllerFactory an object that implements the ControllerFactory interface. This should be used to override the
	 *     default controller settings. In other words, use a controller factory to set different controller(s) for an entity than
	 *     the default.
	 * @return reference to the new entity. Note that the entity has already been added to the World.
	 * @throws GameLogicException if the entity cannot be instantiated, or if the UUID already belongs to an entity.
	 */
	public <T extends Entity> T addEntity(Class<T> c, Entity.ConstructionArgs args, @Nullable ControllerFactory controllerFactory)
			throws GameLogicException;

	/**
	 * Populates all empty tiles with the specified terrain type.
	 *
	 * @param terrainType the terrain type to populate all empty tiles with.
	 */
	public <T extends Terrain> void populateEmptyTilesWith(Class<T> terrainType);

	/**
	 * Adds an entity lifetime observer to this world. The entity lifetime observer is notified whenever an entity is added to or
	 * removed from the world.
	 * <p>
	 * When the observer is first added to observe the world, it receives notifications for all currently alive entities.
	 * </p>
	 *
	 * @param observer the observer to notify when entities are added to or removed from the world.
	 */
	public void addEntityLifetimeObserver(EntityLifetimeObserver observer);

	/**
	 * Returns the width of the world in world units.
	 *
	 * @return the width of the world.
	 */
	public int getWidth();

	/**
	 * Returns the height of the world in world units.
	 *
	 * @return the height of the world.
	 */
	public int getHeight();

	/**
	 * True if the point is within the world, or false otherwise.
	 *
	 * @param x x position of the point, in world units.
	 * @param y y position of the point, in world units.
	 *
	 * @return true if the point is within the world.
	 */
	default public boolean containsPoint(float x, float y) {
		return !(x < 0 || x > getWidth() || y < 0 || y > getHeight());
	}

	/**
	 * The number of tile columns.
	 *
	 * @return The number of tile columns.
	 */
	public int getTileColumns();

	/**
	 * The number of tile rows.
	 *
	 * @return The number of tile rows.
	 */
	public int getTileRows();

	/**
	 * @param column the column to check.
	 * @param row the row to check.
	 * @return true if column is >= 0 and less than getTileColumns() and row is >= 0 and less than getTileRows().
	 */
	public boolean isValidTile(int column, int row);

	/**
	 * Whether the target tile is adjacent to water.
	 *
	 * @param column the tile's column.
	 * @param row the tile's row.
	 * @return true if the target tile is adjacent to water.
	 */
	public boolean isTileAdjacentToWater(int column, int row);

	/**
	 * Updates the game world. Must be called once per game tick.
	 */
	public void update();

	/**
	 * Returns the terrain located in the specified (column, row) tile position.
	 *
	 * @param column >= 0 and < getTileColumns().
	 * @param row >= 0 and < getTileRows().
	 *
	 * @return the terrain in the specified tile position.
	 */
	public Terrain getTerrain(int column, int row);

	/**
	 * Returns the terrain improvement located in the specified (column, row) tile position, or null if none is.
	 *
	 * @param column >= 0 and < getTileColumns().
	 * @param row >= 0 and < getTileRows().
	 *
	 * @return the terrain improvement in the specified tile position, or false otherwise.
	 */
	public TerrainImprovement getTerrainImprovement(int column, int row);

	/**
	 * Moves a pillbox off of the tile map. This is intended to be used when pillboxes are picked up by tanks. Pillboxes
	 * that are off of the tile map won't be returned using the getTerrainImprovements method.
	 *
	 * @param pillbox the pillbox to move.
	 */
	public void movePillboxOffTileMap(Pillbox pillbox);

	/**
	 * Moves a pillbox back onto the tile map. This is intended to be used when a pillbox is placed by a tank. The pillbox's
	 * setPosition method will be called with the new position. If the tile has a terrain improvement that can be built on, the
	 * terrain improvement will be disposed.
	 *
	 * @param column >= 0 and < getTileColumns().
	 * @param row >= 0 and < getTileRows().
	 * @throws GameLogicException if the specified tile is not a valid build location.
	 */
	public void movePillboxOntoTileMap(Pillbox pillbox, int column, int row);

	/**
	 * Returns the mine located in specified (column, row) tile position, or null if none is.
	 *
	 * @param column >= 0 and < getTileColumns().
	 * @param row >= 0 and < getTileRows().
	 *
	 * @return the mine in the specified tile position, or false otherwise.
	 */
	public Mine getMine(int column, int row);

	/**
	 * Returns a list of collidables that are adjacent to or near an entity. The collidables may be filtered by solidness and
	 * type. The entity that is passed in is not included in the returned list.
	 *
	 * @param entity the target entity.
	 * @param onlyIncludeSolidObjects true if only solid objects should be included, or false to include all collidable objects.
	 * @param typeFilter [optional] only collidables of this type will be included in the returned list. May be null, in which
	 *     case no type filter is applied.
	 *
	 * @return a list of nearby collidables.
	 */
	public List<Collidable> getNearbyCollidables(Entity entity, boolean onlyIncludeSolidObjects, @Nullable Class<?> typeFilter);

	/**
	 * Returns a list of collidables that are adjacent to or near an entity. The collidables may be filtered by solidness and
	 * type. The entity that is passed in is not included in the returned list. This overload allows for the max distance, in
	 * tiles, to be passed in.
	 *
	 * @param entity the target entity.
	 * @param onlyIncludeSolidObjects true if only solid objects should be included, or false to include all collidable objects.
	 * @param tileMaxDistance the maximum distance that an object can be from this entity. Must be >= 0.
	 * @param typeFilter [optional] only collidables of this type will be included in the returned list. May be null, in which
	 *     case no type filter is applied.
	 *
	 * @return a list of nearby collidables.
	 */
	public List<Collidable> getNearbyCollidables(Entity entity, boolean onlyIncludeSolidObjects, int tileMaxDistance,
			@Nullable Class<?> typeFilter);

	/**
	 * Returns a list of collidables that are adjacent to or near an entity. The collidables may be filtered by solidness. The
	 * entity that is passed in is not included in the returned list.
	 *
	 * @param entity the target entity.
	 * @param onlyIncludeSolidObjects true if only solid objects should be included, or false to include all collidable objects.
	 *
	 * @return a list of nearby collidables.
	 */
	public List<Collidable> getNearbyCollidables(Entity entity, boolean onlyIncludeSolidObjects);

	/**
	 * Finds the nearest buildable terrain to the x,y world unit position.
	 *
	 * @param x the target x position, in world units.
	 * @param y the target y position, in world units.
	 * @return the nearest buildable terrain to the specified position.
	 */
	public Terrain getNearestBuildableTerrain(float x, float y);

	/**
	 * Returns the number of tiles to the nearest deep water, up to the maximum distance.
	 *
	 * @param tileColumn the target's column.
	 * @param tileRow the target's row.
	 * @param maxDistanceTiles the maximum distance (inclusive) to check, in tiles.
	 * @return the number of tiles to the nearest deep water, or -1 if not found within the maximum distance.
	 */
	public int getTileDistanceToDeepWater(int tileColumn, int tileRow, int maxDistanceTiles);

	/**
	 * Adds a controller of the specified type to the world.
	 *
	 * @param controllerType the type of the controller to add.
	 */
	public void addController(Class<? extends Controller> controllerType);

	/**
	 * Removes a controller of the specified type to the world.
	 *
	 * @param controllerType the type of the controller to remove.
	 */
	public void removeController(Class<? extends Controller> controllerType);

	/**
	 * Returns the world controller count.
	 *
	 * @return the world controller count.
	 */
	public int getControllerCount();
}
