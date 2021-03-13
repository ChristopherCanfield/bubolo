/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.graphics;

import java.util.List;

/**
 * Only for use in tests.
 *
 * @author BU CS673 - Clone Productions
 */
public class SpawnSpriteViewer
{
	/**
	 * Makes spawn points visible. For use in debugging.
	 */
	public void setSpawnsVisible()
	{
		SpriteSystem spriteSystem = SpriteSystem.getInstance();
		List<Sprite> spriteList = spriteSystem.getSprites();
		for (final Sprite sprite : spriteList)
		{
			if (sprite instanceof SpawnSprite)
			{
				((SpawnSprite)sprite).setVisible(true);
			}
		}
	}
}
