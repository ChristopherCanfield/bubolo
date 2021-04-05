package bubolo.mock;

import java.util.UUID;

import bubolo.controllers.ControllerFactory;
import bubolo.util.GameLogicException;
import bubolo.world.Grass;
import bubolo.world.Pillbox;
import bubolo.world.Tile;
import bubolo.world.entity.OldEntity;

/**
 * Mock class used for testing components that need a world implementation
 * (Which is not available at the time that this was implemented).
 * @author BU CS673 - Clone Productions
 */
public class MockPillboxCreator extends MockWorld
{
	private Tile mapTiles[][];
	
	public MockPillboxCreator()
	{
		mapTiles = new Tile[1][1];
		mapTiles[0][0] = new Tile(0 ,0 ,new Grass());
	}
	
	@Override
	public <T extends OldEntity> T addEntity(Class<T> c) throws GameLogicException
	{
		return (T) new Pillbox();
	}

	@Override
	public <T extends OldEntity> T addEntity(Class<T> c, UUID id) throws GameLogicException
	{
		return (T) new Pillbox();
	}

	@Override
	public <T extends OldEntity> T addEntity(Class<T> c, ControllerFactory controllerFactory)
			throws GameLogicException
	{
		return (T) new Pillbox();
	}

	@Override
	public <T extends OldEntity> T addEntity(Class<T> c, UUID id, ControllerFactory controllerFactory)
			throws GameLogicException
	{
		return (T) new Pillbox();
	}

	@Override
	public Tile[][] getTiles()
	{
		return mapTiles;
	}
}

