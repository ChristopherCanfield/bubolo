package bubolo.graphics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SpawnSpriteTest
{
	@Test
	public void getSetVisible()
	{
		SpawnSprite sprite = new SpawnSprite(null);
		assertFalse(sprite.getVisible());

		sprite.setVisible(true);
		assertTrue(sprite.getVisible());

		sprite.setVisible(false);
		assertFalse(sprite.getVisible());
	}
}
