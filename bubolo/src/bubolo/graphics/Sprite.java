/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.graphics;

import static bubolo.util.Units.worldToCamera;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Base class for sprites.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
abstract class Sprite implements Drawable {
	// The layer that this sprite is drawn to.
	private DrawLayer drawLayer;

	// The sprite's color. White draws the texture as it appears in the file.
	private Color color;

	private static final float defaultScale = 1.f;

	/**
	 * Constructs a sprite.
	 *
	 * @param layer the sprite's draw layer.
	 */
	protected Sprite(DrawLayer layer) {
		this.drawLayer = layer;
		this.color = Color.WHITE;
	}

	/**
	 * Returns the sprite's draw layer.
	 *
	 * @return the sprite's draw layer.
	 */
	protected final DrawLayer getDrawLayer() {
		return drawLayer;
	}

	protected final void setDrawLayer(DrawLayer layer) {
		this.drawLayer = layer;
	}

	/**
	 * A unique ID associated with a texture. For sorting sprites by texture.
	 */
	protected abstract int getTextureId();

	/**
	 * Gets the sprite's color. Sprites are white by default, which means that they draw the texture without changes. Do not
	 * mutate the returned Color directly: Make a copy of it, and then set this sprite's color to the new color using setColor.
	 *
	 * @return the sprite's color.
	 */
	protected final Color getColor() {
		return color;
	}

	/**
	 * Sets the sprite's color. The color can be used to tint the texture image. White causes the texture image to be drawn
	 * without changes.
	 *
	 * @param color the sprite's new color.
	 */
	protected final void setColor(Color color) {
		this.color = new Color(color);
	}

	/**
	 * Returns true if the sprite should be removed, or false otherwise.
	 *
	 * @return true if the sprite should be removed, or false otherwise.
	 */
	protected abstract boolean isDisposed();

	/**
	 * Draws the sprite to the screen. batch.begin() must be called before calling this method.
	 *
	 * @param graphics reference to the Graphics system.
	 */
	abstract void draw(Graphics graphics);

	private static final Vector2 cameraCoordinatesTempVar = new Vector2();

	/**
	 * Draws the texture to the screen. batch.begin() must be called before calling this method.
	 *
	 * @param graphics The graphics system.
	 * @param texture The texture to draw.
	 */
	protected final void drawTexture(Graphics graphics, Texture texture) {
		drawTexture(graphics, texture, defaultScale);
	}

	/**
	 * Draws the texture to the screen. batch.begin() must be called before calling this method.
	 *
	 * @param graphics The graphics system.
	 * @param texture The texture to draw.
	 * @param scale the scale that the texture will be drawn.
	 */
	protected final void drawTexture(Graphics graphics, Texture texture, float scale) {
		Vector2 origin = getOrigin(texture.getWidth(), texture.getHeight());
		Vector2 cameraCoordinates = worldToCamera(graphics.camera(), getX() - origin.x, getY() - origin.y, cameraCoordinatesTempVar);

		var batch = graphics.batch();
		batch.setColor(color);
		batch.draw(texture,
				cameraCoordinates.x,
				cameraCoordinates.y,
				origin.x,
				origin.y,
				texture.getWidth(),
				texture.getHeight(),
				scale,
				scale,
				MathUtils.radiansToDegrees * (getRotation() - MathUtils.PI / 2.f),
				0,
				0,
				texture.getWidth(),
				texture.getHeight(),
				false,
				false);
	}

	/**
	 * Draws the texture region to the screen. batch.begin() must be called before calling this method.
	 *
	 * @param graphics the graphics system.
	 * @param textureRegion The texture region to draw.
	 */
	protected final void drawTexture(Graphics graphics, TextureRegion textureRegion) {
		drawTexture(graphics, textureRegion, defaultScale);
	}

	protected final void drawTexture(Graphics graphics, TextureRegion textureRegion, float scale) {
		Vector2 origin = getOrigin(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
		drawTexture(graphics, textureRegion, scale, getX() - origin.x, getY() - origin.y, origin.x, origin.y, getRotation());
	}

	/**
	 * Draws the texture region to the screen. batch.begin() must be called before calling this method.
	 *
	 * @param graphics the graphics system.
	 * @param texture The texture region to draw.
	 * @param scale the scale that the texture region will be drawn.
	 * @param worldX the world x position.
	 * @param worldY the world y position.
	 * @param originX the x offset.
	 * @param originY the y offset.
	 * @param rotationRadians the object's rotation, in radians.
	 */
	protected final void drawTexture(Graphics graphics, TextureRegion texture, float scale, float worldX, float worldY,
			float originX, float originY, float rotationRadians) {
		Vector2 cameraCoordinates = worldToCamera(graphics.camera(), getX() - originX, getY() - originY, cameraCoordinatesTempVar);

		var batch = graphics.batch();
		batch.setColor(color);
		batch.draw(texture,
				cameraCoordinates.x,
				cameraCoordinates.y,
				originX,
				originY,
				texture.getRegionWidth(),
				texture.getRegionHeight(),
				scale,
				scale,
				MathUtils.radiansToDegrees * (rotationRadians - MathUtils.PI / 2.f));
	}

	private static final Vector2 origin = new Vector2();

	/**
	 * Returns the center of a given width and height.
	 *
	 * @param width the width of a texture or texture region.
	 * @param height the height of a texture or texture region.
	 * @return the center of the given width and height. This object is reused across Sprite instances, and should not be stored.
	 */
	private static Vector2 getOrigin(float width, float height) {
		return origin.set(width / 2.f, height / 2.f);
	}
}
