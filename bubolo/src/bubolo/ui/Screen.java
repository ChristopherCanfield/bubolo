package bubolo.ui;

import com.badlogic.gdx.graphics.Color;

import bubolo.graphics.Graphics;

public interface Screen {
	/**
	 * @return The screen clear color.
	 */
	Color clearColor();

	/**
	 * Updates and draws the screen.
	 *
	 * @param graphics reference to the graphics system.
	 */
	void draw(Graphics graphics);

	/**
	 * Called when the window is resized.
	 *
	 * @param newWidth the new window width.
	 * @param newHeight the new window height.
	 */
	void viewportResized(int newWidth, int newHeight);

	/**
	 * Releases all heavy-weight resources.
	 */
	void dispose();
}
