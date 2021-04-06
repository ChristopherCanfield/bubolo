package bubolo.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import bubolo.controllers.Controller;
import bubolo.controllers.ControllerFactory;
import bubolo.util.GameLogicException;
import bubolo.world.EntityCreationObserver;
import bubolo.world.Tank;
import bubolo.world.Tile;
import bubolo.world.World;
import bubolo.world.entity.OldEntity;

/**
 * Mock class used for testing components that need a world implementation
 * (Which was not available at the time that this was implemented).
 * @author BU CS673 - Clone Productions
 */
public class MockWorld implements World
{
	private List<OldEntity> entities = new ArrayList<OldEntity>();
	private Map<UUID, OldEntity> entityMap = new HashMap<UUID, OldEntity>();

	/**
	 * Adds the entity to the MockWorld.
	 * @param e the entity to add.
	 */
	public void add(OldEntity e)
	{
		entities.add(e);
		entityMap.put(e.getId(), e);
	}

	@Override
	public OldEntity getEntity(UUID id) throws GameLogicException
	{
		return entityMap.get(id);
	}

	@Override
	public List<OldEntity> getEntities()
	{
		return entities;
	}

	@Override
	public void removeEntity(OldEntity e)
	{
	}

	@Override
	public void removeEntity(UUID id) throws GameLogicException
	{
		removeEntity(getEntity(id));
	}

	@Override
	public int getWidth()
	{
		return 0;
	}

	@Override
	public int getHeight()
	{
		return 0;
	}

	@Override
	public void update()
	{
		// do nothing.
	}

	@Override
	public <T extends OldEntity> T addEntity(Class<T> c) throws GameLogicException
	{
		return addEntity(c, null, null);
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
		return addEntity(c, null, controllerFactory);
	}

	@Override
	public <T extends OldEntity> T addEntity(Class<T> c, UUID id, ControllerFactory controllerFactory)
			throws GameLogicException
	{
		T entity = null;
		try
		{
			entity = c.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new GameLogicException(e);
		}
		entity.setId(id);
		return entity;
	}

	@Override

	public List<Tank> getTanks()
	{
		return null;
	}

	@Override
	public void setTiles(Tile[][] givenTiles)
	{
		// do nothing
	}

	@Override
	public Tile[][] getTiles()
	{
		//do nothing
		return null;
	}

	@Override
	public List<OldEntity> getActors()
	{
		return null;
	}

	@Override
	public List<OldEntity> getEffects()
	{
		return null;
	}

	@Override
	public void setHeight(int height)
	{
	}

	@Override
	public void setWidth(int width)
	{
	}

	@Override
	public List<OldEntity> getSpawns()
	{
		// do nothing
		return null;
	}

	@Override
	public void addController(Class<? extends Controller> controllerType)
	{
	}

	@Override
	public void removeController(Class<? extends Controller> controllerType)
	{
	}

	@Override
	public int getControllerCount()
	{
		return 0;
	}

	@Override
	public Tile getTileFromWorldPosition(float worldX, float worldY) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTileColumns() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTileRows() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setEntityCreationObserver(EntityCreationObserver entityCreationObserver) {
		// TODO Auto-generated method stub

	}
}
