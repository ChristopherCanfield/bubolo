package bubolo.graphics;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import bubolo.world.entity.concrete.Rubble;

/**
 * Test for Rubble Sprite
 */

public class RubbleSpriteTest
{
	@Test
	public void getRotation()
	{
		Sprite sprite = SpriteSystem.getInstance().createSprite(new Rubble());
		boolean check;
		check = (sprite.getRotation() == 0 || sprite.getRotation() == (float) (Math.PI/2) || sprite.getRotation() == (float) (Math.PI) ||
				sprite.getRotation() == (float) (3 * Math.PI / 2));
		assertTrue(check);
	}

}
