package bubolo.graphics;

/**
 * Used to determine the draw order. Sprites that are drawn first may be partially or fully covered
 * by a sprite that is drawn later.
 *
 * @author BU CS673 - Clone Productions
 */
enum DrawLayer
{

	/**
	 * The background layer. Using this in anything except for the background layer will result in the
	 * sprite never being drawn.
	 */
	BACKGROUND,

	/**
	 * The lowest general sprite draw layer. Intended for use with terrain.
	 */
	TERRAIN,

	/**
	 * The second sprite draw layer. Intended for use with terrain improvements.
	 */
	TERRAIN_IMPROVEMENTS,

	/**
	 * The third sprite draw layer. Intended for use with mines.
	 */
	MINES,

	/**
	 * The fourth sprite draw layer. Intended for use with tanks.
	 */
	TANKS,

	/**
	 * The fifth sprite draw layer. Intended for use with effects, such as explosions.
	 */
	EFFECTS,

	/**
	 * The top sprite draw layer, which is drawn above all other layers.
	 */
	TOP
}
