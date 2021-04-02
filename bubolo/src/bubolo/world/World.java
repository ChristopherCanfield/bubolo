package bubolo.world;

import java.util.List;
import java.util.UUID;

import bubolo.controllers.Controller;
import bubolo.controllers.ControllerFactory;
import bubolo.util.GameLogicException;
import bubolo.util.Nullable;
import bubolo.world.entity.concrete.Mine;
import bubolo.world.entity.concrete.Spawn;
import bubolo.world.entity.concrete.Tank;

/**
 * Stores and processes game entities.
 *
 * @author BU CS673 - Clone Productions
 */
public interface World
{
	/**
	 * Returns an entity from a user id. Throws a GameLogicException if the entity is not found.
	 *
	 * @param id
	 *            the entity's unique id.
	 * @return the requested entity.
	 * @throws GameLogicException
	 *             if the entity is not found.
	 */
	public Entity getEntity(UUID id) throws GameLogicException;

	/**
	 * Returns an unmodifiable view of all entities in the world. Ordering should not be assumed, and may change
	 * between calls.
	 *
	 * @return the list of entities.
	 */
	public List<Entity> getEntities();

	/**
	 * Returns an unmodifiable view of all tanks in the world. Ordering should not be assumed, and may change
	 * between calls.
	 *
	 * @return the list of tanks.
	 */
	public List<Tank> getTanks();

	/**
	 * Returns an unmodifiable view of all Spawn Locations in the world. Ordering should not be assumed, and may
	 * change between calls.
	 *
	 * @return the list of Spawns.
	 */
	public List<Spawn> getSpawns();

	/**
	 * Returns an unmodifiable view of all actors in the world. Ordering should not be assumed, and may change
	 * between calls.
	 *
	 * @return the list of actors.
	 */
	public List<ActorEntity> getActors();

	/**
	 * Attaches an entity creation observer to this world. The entity creation observer is notified whenever an entity
	 * is added to the world. Only one observer can be attached to the world at a time.
	 * @param entityCreationObserver
	 */
	public void setEntityCreationObserver(EntityCreationObserver entityCreationObserver);

	/**
	 * Performs the following actions:
	 * <ol>
	 * <li>A new Entity of the specified type is created.</li>
	 * <li>The new Entity is added to the World</li>
	 * <li>A new Sprite is created and added to the Sprites list.</li>
	 * <li>One or more Controllers are created and added to the Controllers list.</li>
	 * </ol>
	 *
	 * @param c
	 *            the entity's class object. For example, to create a new Tank, call this method
	 *            using the following form: <code>World.addEntity(Tank.class).</code>
	 * @return reference to the new entity.
	 * @throws GameLogicException
	 *             if the entity cannot be instantiated, or if the UUID already belongs to an
	 *             entity.
	 */
	public <T extends Entity> T addEntity(Class<T> c, Entity.ConstructionArgs args) throws GameLogicException;

	/**
	 * @see World#addEntity(Class)
	 * @param c
	 *            the entity's class object. For example, to create a new Tank, call this method
	 *            using the following form: <code>World.addEntity(Tank.class).</code>
	 * @param controllerFactory
	 *            an object that implements the ControllerFactory interface. This should be used to
	 *            override the default controller settings. In other words, use a controller factory
	 *            to set different controller(s) for an entity than the default.
	 * @return reference to the new entity. Note that the entity has already been added to the
	 *         World.
	 * @throws GameLogicException
	 *             if the entity cannot be instantiated, or if the UUID already belongs to an
	 *             entity.
	 */
	public <T extends Entity> T addEntity(Class<T> c, Entity.ConstructionArgs args, @Nullable ControllerFactory controllerFactory) throws GameLogicException;

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
		return x < 0 || x > getWidth() || y < 0 || y > getHeight();
	}

	/**
	 * The number of tile columns.
	 * @return The number of tile columns.
	 */
	public int getTileColumns();

	/**
	 * The number of tile rows.
	 * @return The number of tile rows.
	 */
	public int getTileRows();

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
	 *Returns the mine located in specified (column, row) tile position, or null if none is.
	 *
	 * @param column >= 0 and < getTileColumns().
	 * @param row >= 0 and < getTileRows().
	 *
	 * @return the mine in the specified tile position, or false otherwise.
	 */
	public Mine getMine(int column, int row);

	/**
	 * Returns a list of collidables that are adjacent to or near a (column, row) position, possibly filtered by solidness and type.
	 *
	 * @param column the target tile's column.
	 * @param row the target tile's row.
	 * @param onlyIncludeSolidObjects true if only solid objects should be included, or false to include all collidable objects.
	 * @param typeFilter [optional] only collidables of this type will be included in the returned list. May be null, in which case
	 * no type filter is applied.
	 *
	 * @return a list of nearby collidables.
	 */
	public List<Collidable> getNearbyCollidables(int column, int row, boolean onlyIncludeSolidObjects, @Nullable Class<?> typeFilter);

	/**
	 * Returns a list of collidables that are adjacent to or near a (column, row) position, possibly filter by solidness.
	 *
	 * @param column the target tile's column.
	 * @param row the target tile's row.
	 * @param onlyIncludeSolidObjects true if only solid objects should be included, or false to include all collidable objects.
	 *
	 * @return a list of nearby collidables.
	 */
	public List<Collidable> getNearbyCollidables(int column, int row, boolean onlyIncludeSolidObjects);

	/**
	 * Adds a controller of the specified type to the world.
	 *
	 * @param controllerType
	 *            the type of the controller to add.
	 */
	public void addController(Class<? extends Controller> controllerType);

	/**
	 * Removes a controller of the specified type to the world.
	 *
	 * @param controllerType
	 *            the type of the controller to remove.
	 */
	public void removeController(Class<? extends Controller> controllerType);

	/**
	 * Returns the world controller count.
	 * @return the world controller count.
	 */
	public int getControllerCount();
}
