package bubolo.graphics;

import com.badlogic.gdx.graphics.Color;

/**
 * Used by certain sprite textures to determine the color to use.
 */
enum SpriteColorSet {
	Blue(0, new Color(89.0f/255, 137.0f/255, 208.0f/255, 1)),
	Red(1, new Color(208.0f/255, 90.0f/255, 104.0f/255, 1)),
	Neutral(2, new Color(215.0f/255, 215.0f/255, 215.0f/255, 1));

	final int row;
	final Color color;

	SpriteColorSet(int row, Color color) {
		this.row = row;
		this.color = color;
	}
}
