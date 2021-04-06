package bubolo.graphics;

import bubolo.mock.MockTank;

/**
 * A mock Sprite class used for testing calls to drawTexture(batch, camera, layer, TextureRegion)
 * in the Graphics system.
 * @author BU CS673 - Clone Productions
 */
public class MockSpriteTextureRegion extends AbstractEntitySprite<MockTank>
{
	/**
	 * Constructs a mock Tank object.
	 */
	public MockSpriteTextureRegion()
	{
		super(DrawLayer.MINES, new MockTank());
	}

	@Override
	void draw(Graphics graphics)
	{
	}
}
