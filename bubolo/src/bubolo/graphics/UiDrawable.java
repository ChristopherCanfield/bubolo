package bubolo.graphics;

/**
 * Objects that draw to the user interface, such as by using ShapeRenderer, can implement this interface. Unlike
 * with the Drawable.draw method, it is fine to interleave calls to ShapeRenderer and Batch using this interface's
 * sole method if needed.
 *
 * @author Christopher D. Canfield
 */
interface UiDrawable {

	/**
	 * Draws UI elements. It is fine to interleave calls to the ShapeRenderer and Batch if needed. Unlike with
	 * the Drawable.draw method, which attempts to batch all draw calls, no batching is performed by the graphics
	 * system when this method is used.
	 *
	 * @param graphics reference to the Graphics system.
	 */
	void drawUiElements(Graphics graphics);
}
