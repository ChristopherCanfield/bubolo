package bubolo.mock;

import java.util.UUID;

import bubolo.world.Entity;

/**
 * Dummy Entity class for unit testing methods without any Graphics involvement. This allows the
 * tests to run outside of an OpenGL environment, because no textures are fetched.
 *
 * @author BU CS673 - Clone Productions
 */
public class MockEntity extends Entity
{
	/**
	 * Construct a new DummyEntity with the specified UUID.
	 *
	 * @param id
	 *            is the existing UUID to be applied to the new DummyEntity.
	 */
	public MockEntity(UUID id)
	{
		super(id, 0, 0);
	}

	/**
	 * Construct a new DummyEntity with the specified UUID.
	 */
	public MockEntity()
	{
		this(Entity.nextId());
	}

	@Override
	public float x() {
		return 0;
	}

	@Override
	public float y() {
		return 0;
	}

	@Override
	public float rotation() {
		return 0;
	}

}
