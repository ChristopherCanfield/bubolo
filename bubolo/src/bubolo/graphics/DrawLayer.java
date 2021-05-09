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
	 * The lowest sprite draw layer. Intended for use with most terrain.
	 */
	TerrainLevel1,

	/**
	 * Used for terrain that needs to be drawn on top of overlapping terrains, such as roads.
	 */
	TerrainLevel2,

	/**
	 * The third sprite draw layer. Intended for use with terrain improvements.
	 */
	TerrainImprovements,

	/**
	 * The fourth sprite draw layer. Intended for use with mines.
	 */
	Mines,

	/**
	 * The fifth sprite draw layer. Intended for use with tanks.
	 */
	Tanks,

	/**
	 * The sixth sprite draw layer. Intended for use with effects, such as explosions.
	 */
	Effects,

	/**
	 * The top sprite draw layer, which is drawn above all other layers.
	 */
	Top
}
