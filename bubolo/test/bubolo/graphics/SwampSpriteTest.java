package bubolo.graphics;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import bubolo.world.entity.concrete.Swamp;

public class SwampSpriteTest
{

	@Test
	public void getRotation()
	{
		Sprite sprite = Sprites.getInstance().createSprite(new Swamp());
		boolean check;
		check = (sprite.getRotation() == 0 || sprite.getRotation() == (float) (Math.PI/2) || sprite.getRotation() == (float) (Math.PI) ||
				sprite.getRotation() == (float) (3 * Math.PI / 2));
		assertTrue(check);
	}
}
