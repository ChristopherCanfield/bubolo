package bubolo.graphics;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import bubolo.Config;
import bubolo.ui.Screen;
import bubolo.util.Nullable;
import bubolo.util.Timer;
import bubolo.world.Entity;
import bubolo.world.EntityLifetimeObserver;
import bubolo.world.World;

/**
 * The top-level class for the Graphics system.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Graphics implements EntityLifetimeObserver {
	// Stores the textures, so that only one copy is stored in memory.
	private static Map<String, Texture> textures = new HashMap<>();
	private static Map<String, TextureRegion[]> textureRegions1d = new HashMap<>();
	private static Map<String, TextureRegion[][]> textureRegions2d = new HashMap<>();

	private final Camera camera;
	private final OrthographicCamera uiCamera;
	private final SpriteBatch batch;
	private final ShapeRenderer shapeRenderer;
	private final SpriteBatch nonScalingBatch;
	private final ShapeRenderer nonScalingShapeRenderer;

	private SpriteSystem spriteSystem;

	// Controls the camera's position.
	private final TankCameraController cameraController;

	// The comparator used to sort sprites.
	private static final Comparator<Sprite> sortByLayerThenTextureThenName = Comparator.comparing(Sprite::getDrawLayer)
			.thenComparingInt(s -> s.getTextureId())
			.thenComparingInt(s -> s.getClass().getSimpleName().hashCode());

	private final List<Sprite> spritesInView = new ArrayList<Sprite>();

	private final Timer<Graphics> timer = new Timer<Graphics>(10);

	/**
	 * Returns a texture from a file name. Ensures that the same texture isn't stored multiple times. Will load the file
	 * if it has not yet been loaded.
	 *
	 * @param fileName the name of the texture file. Do not include the full path.
	 * @return the requested texture.
	 */
	public static Texture getTexture(String fileName) {
		Texture texture = textures.get(fileName);
		if (texture == null) {
			texture = new Texture(new FileHandle(Config.TextureFilePath.resolve(fileName).toFile()));
			textures.put(fileName, texture);
		}

		return texture;
	}

	/**
	 * Returns a texture region from a path that points to a texture. Ensures that the same texture region isn't stored
	 * multiple times. Will create the region if it has not yet been created.
	 *
	 * @param path the path to the texture file.
	 * @param spriteType the class type of the sprite requesting the texture.
	 * @return the requested texture region.
	 */
	public static TextureRegion[] getTextureRegion1d(String path, Class<? extends Sprite> spriteType) {
		TextureRegion[] textureRegion = textureRegions1d.get(path);
		if (textureRegion == null) {
			Texture texture = getTexture(path);
			textureRegion = TextureUtil.adaptiveSplit(texture, spriteType);
			textureRegions1d.put(path, textureRegion);
		}
		return textureRegion;
	}

	/**
	 * Returns a texture region from a path that points to a texture. Ensures that the same texture region isn't stored
	 * multiple times. Will create the region if it has not yet been created.
	 *
	 * @param path the path to the texture file.
	 * @param frames the number of frames in the file.
	 * @param frameWidth the width of each frame.
	 * @param paddingWidth the horizontal padding between frames.
	 * @return reference to the texture region array.
	 */
	public static TextureRegion[] getTextureRegion1d(String path, int frames, int frameWidth, int paddingWidth) {
		TextureRegion[] textureRegion = textureRegions1d.get(path);
		if (textureRegion == null) {
			Texture texture = getTexture(path);
			textureRegion = TextureUtil.splitFramesInRow(texture, frames, frameWidth, paddingWidth);
			textureRegions1d.put(path, textureRegion);
		}
		return textureRegion;
	}

	/**
	 * Returns a texture region from a path to a texture. Ensures that the same texture region isn't stored multiple
	 * times. Will create the region if it has not yet been created. The frames are in column-row order. This overload
	 * assumes that there is no padding between frames.
	 *
	 * @param path the path to the texture file.
	 * @param frameWidth each frame's width.
	 * @param frameHeight each frame's height.
	 * @return the requested texture region.
	 */
	public static TextureRegion[][] getTextureRegion2d(String path, int frameWidth, int frameHeight) {
		return getTextureRegion2d(path, frameWidth, frameHeight, 0, 0);
	}

	/**
	 * Returns a texture region from a path to a texture. Ensures that the same texture region isn't stored multiple
	 * times. Will create the region if it has not yet been created. The frames are in column-row order. Use this overload
	 * to specify the padding between each frame.
	 *
	 * @param path the path to the texture file.
	 * @param frameWidth each frame's width.
	 * @param frameHeight each frame's height.
	 * @param framePaddingWidth the amount of padding between each column.
	 * @param framePaddingHeight the amount of padding between each row.
	 * @return the requested texture region.
	 */
	public static TextureRegion[][] getTextureRegion2d(String path, int frameWidth, int frameHeight, int framePaddingWidth, int framePaddingHeight) {
		TextureRegion[][] textureRegion = textureRegions2d.get(path);
		if (textureRegion == null) {
			Texture texture = getTexture(path);
			textureRegion = TextureUtil.splitFrames(texture, frameWidth, frameHeight, framePaddingWidth, framePaddingHeight);
			textureRegions2d.put(path, textureRegion);
		}

		return textureRegion;
	}

	/**
	 * Destroys all textures, and destroys the Graphics instance.
	 */
	public void dispose() {
		for (Texture texture : textures.values()) {
			texture.dispose();
		}
	}

	/**
	 * Creates the graphics system.
	 *
	 * @param resolutionX the x resolution (width) of the game's viewport, in pixels.
	 * @param resolutionY the y resolution (height) of the game's viewport, in pixels.
	 */
	public Graphics(int resolutionX, int resolutionY) {
		camera = new OrthographicCamera(resolutionX, resolutionY);
		camera.position.set(0, 0, 0);
		camera.update();

		uiCamera = new OrthographicCamera(resolutionX, resolutionY);
		uiCamera.position.x = uiCamera.position.y = 0;
		uiCamera.update();

		cameraController = new TankCameraController(camera);

		batch = new SpriteBatch(3500);
		shapeRenderer = new ShapeRenderer(500);

		nonScalingBatch = new SpriteBatch(50);
		nonScalingShapeRenderer = new ShapeRenderer(250);
		nonScalingBatch.setProjectionMatrix(uiCamera.combined);
		nonScalingShapeRenderer.setProjectionMatrix(uiCamera.combined);

		spriteSystem = new SpriteSystem();

		loadAllTextures();
	}

	/**
	 * Returns the camera used to render the game. This camera uses a y-up coordinate system, and has a fixed resolution that
	 * scales when the screen size is changed. While that can lead to graphical distortion, it ensures that every player can
	 * see the same amount of the map, regardless of screen resolution.
	 *
	 * @return the camera used to render the game.
	 */
	public Camera camera() {
		return camera;
	}

	/**
	 * Returns the camera used to render user interface elements, such as menu. This camera uses screen coordinates (y-down),
	 * and does not scale when the screen size is increased or decreased.
	 *
	 * @return the camera used to render user interface elements.
	 */
	public OrthographicCamera uiCamera() {
		return uiCamera;
	}

	public Batch batch() {
		return batch;
	}

	public ShapeRenderer shapeRenderer() {
		return shapeRenderer;
	}

	public Batch nonScalingBatch() {
		return nonScalingBatch;
	}

	public ShapeRenderer nonScalingShapeRenderer() {
		return nonScalingShapeRenderer;
	}

	SpriteSystem sprites() {
		return spriteSystem;
	}

	Timer<Graphics> timer() {
		return timer;
	}

	@Override
	public void onEntityAdded(Entity entity) {
		spriteSystem.createSprite(this, entity);
	}

	@Override
	public void onEntityRemoved(Entity entity) {
		// Not used.
	}

	@Override
	public void onObserverAddedToWorld(World world) {
		// Not used.
	}

	/**
	 * Called when the window is resized.
	 *
	 * @param newWidth the new window width.
	 * @param newHeight the new window height.
	 */
	public void resize(int newWidth, int newHeight) {
		uiCamera.setToOrtho(false, newWidth, newHeight);
		nonScalingBatch.setProjectionMatrix(uiCamera.combined);
		nonScalingShapeRenderer.setProjectionMatrix(uiCamera.combined);
	}

	/**
	 * Provides the world size to the graphics system. Required for the camera.
	 *
	 * @param worldWidth the world's width, in world units.
	 * @param worldHeight the world's height, in world height.
	 */
	public void setWorldSize(int worldWidth, int worldHeight) {
		int worldWidthPixels = (int) Config.DefaultPixelsPerWorldUnit * worldWidth;
		int worldHeightPixels = (int) Config.DefaultPixelsPerWorldUnit * worldHeight;
		cameraController.setWorldSize(worldWidthPixels, worldHeightPixels);
	}

	/**
	 * Updates and draws the specified screen.
	 *
	 * @param screen the ui screen to update.
	 */
	public void draw(Screen screen) {
		draw(null, screen);
	}

	/**
	 * Draws the entities that are within the camera's clipping boundary. Must be called once per game tick.
	 *
	 * @param world reference to the game world.
	 */
	public void draw(World world) {
		draw(world, null);
	}

	/**
	 * Draws the game world, followed by the specified screen. Must be called once per game tick.
	 *
	 * @param world reference to the game world.
	 * @param screen the ui screen to update and draw.
	 */
	public void draw(World world, Screen screen) {
		if (screen != null) {
			var clearColor = screen.clearColor();
			Gdx.gl20.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
		} else {
			Gdx.gl20.glClearColor(0, 0, 0, 1);
		}

		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		timer.update(this);

		drawWorld(world);
		drawScreen(screen);

		batch.totalRenderCalls = 0;
	}

	private void drawScreen(@Nullable Screen screen) {
		if (screen != null) {
			screen.draw(this);
		}
	}

	/**
	 * Draws the entities that are within the camera's clipping boundary. Must be called once per game tick.
	 *
	 * @param world reference to the game world.
	 */
	private void drawWorld(@Nullable World world) {
		if (world == null) {
			return;
		}

		// Get list of sprites, and clip sprites that are outside of the camera's view.
		spritesInView.clear();
		for (Sprite sprite : spriteSystem.getSprites()) {
			if (withinCameraView(camera, sprite)) {
				spritesInView.add(sprite);
			}
		}

		// Render sprites.
		drawSpritesByLayer(spritesInView);
		drawTankUiElements(spritesInView);

		// Remove destroyed sprites from the list.
		List<Sprite> sprites = spriteSystem.getSprites();
		for (int i = 0; i < sprites.size(); ++i) {
			if (sprites.get(i).isDisposed()) {
				sprites.remove(i);
			}
		}
	}

	/**
	 * Returns the camera controller, which controls the camera's position.
	 */
	TankCameraController getCameraController() {
		return cameraController;
	}

	/**
	 * Draw all sprites, ordered by draw layer.
	 *
	 * @param sprites the list of sprites that will be drawn.
	 */
	private void drawSpritesByLayer(List<Sprite> sprites) {
		// Sort list by draw layer, to ensure that sprites are drawn in the correct order,
		// then by sprite type, to facilitate batching.
		Collections.sort(spritesInView, sortByLayerThenTextureThenName);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		batch.begin();
		for (Sprite sprite : sprites) {
			sprite.draw(this);
		}
		batch.end();
	}

	/**
	 * This is a separate method to ensure that the tank names are always drawn above all tanks and other objects.
	 */
	private void drawTankUiElements(List<Sprite> spritesInView) {
		var uiDrawablesInView = spritesInView.stream().filter(s -> s instanceof UiDrawable)
				.collect(Collectors.toList());

		// Render the player names.
		batch.begin();
		for (Sprite sprite : uiDrawablesInView) {
			if (sprite instanceof TankSprite tankSprite) {
				tankSprite.drawTankPlayerName(this);
			}
		}
		batch.end();

		// Render UI elements.
		for (Sprite sprite : uiDrawablesInView) {
			assert sprite instanceof UiDrawable;

			if (sprite instanceof UiDrawable uiDrawable) {
				uiDrawable.drawUiElements(this);
			}
		}
	}

	/**
	 * Returns true if the x, y, height and width of the sprite are within the camera's view.
	 *
	 * @param camera the game camera.
	 * @param sprite the sprite to check.
	 * @return true if the sprite is within the camera's view, or false otherwise.
	 */
	private static boolean withinCameraView(Camera camera, Sprite sprite) {
		if (sprite instanceof TankSprite tankSprite
				&& tankSprite.getEntity().isOwnedByLocalPlayer()) {
			return true;
		}

		float cameraX = camera.position.x;
		float cameraY = camera.position.y;

		float spriteHalfWidth = sprite.getWidth() / 2;
		float spriteHalfHeight = sprite.getHeight() / 2;
		float spriteX = sprite.getX();
		float spriteY = sprite.getY();

		return (spriteX + spriteHalfWidth + cameraX > 0 && spriteX - spriteHalfWidth - cameraX < camera.viewportWidth
				&& spriteY + spriteHalfHeight + cameraY > 0
				&& spriteY - spriteHalfHeight - cameraY < camera.viewportHeight);
	}

	/**
	 * Loads all textures. This isn't strictly necessary, but we encountered slight hiccups when a sprite type was
	 * loaded for the first time. This was most noticeable when the first bullet is fired. Textures are assumed to be
	 * pngs.
	 */
	private static void loadAllTextures() {
		File textureDirectory = Config.TextureFilePath.toFile();
		for (File file : textureDirectory.listFiles()) {
			if (file.getName().endsWith("png")) {
				getTexture(file.getName());
			}
		}
	}
}
