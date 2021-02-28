package bubolo.graphics;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

import bubolo.ui.Screen;
import bubolo.util.Coordinates;
import bubolo.world.World;

/**
 * The top-level class for the Graphics system.
 *
 * @author BU CS673 - Clone Productions
 */
public class Graphics
{
	/**
	 * File path where textures are stored.
	 */
	public static final String TEXTURE_PATH = "res/textures/";

	/**
	 * The target number of draw ticks per second.
	 */
	public static final int TICKS_PER_SECOND = 60;

	/**
	 * The number of milliseconds per draw tick.
	 */
	public static final long MILLIS_PER_TICK = 1000 / TICKS_PER_SECOND;

	// Stores the textures, ensuring that only one is needed for all instances
	// of a given sprite.
	private static Map<String, Texture> textures = new HashMap<>();
	private static Map<String, TextureRegion[]> textureRegions1d = new HashMap<>();
	private static Map<String, TextureRegion[][]> textureRegions2d = new HashMap<>();

	private SpriteBatch batch;
	private Camera camera;

	private Sprites spriteSystem;

	// The list of camera controllers.
	private List<CameraController> cameraControllers = new ArrayList<CameraController>();

	// Static reference to this object for the getInstance() method.
	private static Graphics instance = null;

	// The comparator used to sort sprites.
	private static Comparator<Sprite> spriteComparator;

	private List<Sprite> spritesInView = new ArrayList<Sprite>();

	private BackgroundSprite[][] background;

	/**
	 * Gets a reference to the Graphics system. The Graphics system must be explicitly constructed
	 * using the <code>Graphics(width, height)</code> constructor before this is called, or an
	 * <code>IllegalStateException</code> will be thrown. This method is package-private, because
	 * only objects within the Graphics system should have access to it.
	 *
	 * @return a reference to the Graphics system.
	 * @throws IllegalStateException
	 *             when the Graphics system has not been explicitly constructed using the
	 *             <code>Graphics(width, height)</code> constructor.
	 */
	static Graphics getInstance()
	{
		if (instance == null)
		{
			throw new IllegalStateException(
					"Graphics.getInstance cannot be called until the Graphics system has been explicitly constructed.");
		}
		return instance;
	}

	/**
	 * Returns a texture from a path. Ensures that the same texture isn't stored multiple times.
	 * Will load the file if it has not yet been loaded.
	 *
	 * @param path
	 *            the path to the texture file.
	 * @return the requested texture.
	 */
	static Texture getTexture(String path)
	{
		Texture texture = textures.get(path);
		if (texture == null)
		{
			texture = new Texture(new FileHandle(new File(path)));
			textures.put(path, texture);
		}

		return texture;
	}

	/**
	 * Returns a texture region from a path to a texture. Ensures that the same texture region isn't stored
	 * multiple times. Will create the region if it has not yet been created.
	 *
	 * @param path the path to the texture file.
	 * @param spriteType the class type of the sprite requesting the texture.
	 * @return the requested texture region.
	 */
	static TextureRegion[] getTextureRegion1d(String path, Class<? extends Sprite> spriteType) {
		TextureRegion[] textureRegion = textureRegions1d.get(path);
		if (textureRegion == null) {
			Texture texture = getTexture(path);
			textureRegion = TextureUtil.adaptiveSplit(texture, spriteType);
			textureRegions1d.put(path, textureRegion);
		}
		return textureRegion;
	}

	/**
	 * Returns a texture region from a path to a texture. Ensures that the same texture region isn't stored
	 * multiple times. Will create the region if it has not yet been created.
	 *
	 * @param path the path to the texture file.
	 * @param frameWidth each frame's width.
	 * @param frameHeight each frame's height.
	 * @return the requested texture region.
	 */
	static TextureRegion[][] getTextureRegion2d(String path, int frameWidth, int frameHeight) {
		TextureRegion[][] textureRegion = textureRegions2d.get(path);
		if (textureRegion == null) {
			Texture texture = getTexture(path);
			textureRegion = TextureUtil.splitFrames(texture, frameWidth, frameHeight);
			textureRegions2d.put(path, textureRegion);
		}

		return textureRegion;
	}

	/**
	 * Destroys all textures, and destroys the Graphics instance.
	 */
	public static void dispose()
	{
		for (Texture texture : textures.values())
		{
			texture.dispose();
		}
		instance = null;
	}

	/**
	 * Creates the graphics system.
	 *
	 * @param windowWidth
	 *            the width of the window, in pixels.
	 * @param windowHeight
	 *            the height of the window, in pixels.
	 */
	public Graphics(int windowWidth, int windowHeight)
	{
		camera = new OrthographicCamera(windowWidth, windowHeight);
		batch = new SpriteBatch();
		spriteSystem = Sprites.getInstance();
		spriteComparator = new SpriteComparator();

		loadAllTextures();

		synchronized (Graphics.class)
		{
			Graphics.instance = this;
		}
	}

	/**
	 * Draws the specified screen.
	 *
	 * @param screen the ui screen to update.
	 */
	public void draw(Screen screen)
	{
		Gdx.gl20.glClearColor(0.6f, 0.6f, 0.6f, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		screen.update();
	}

	/**
	 * Draws the entities that are within the camera's clipping boundary.
	 *
	 * @param world
	 *            reference to the World Model object.
	 */
	public void draw(World world)
	{
		draw(world, null);
	}

	/**
	 * Draws the entities that are within the camera's clipping boundary.
	 *
	 * @param world
	 *            the World Model object.
	 * @param ui
	 *            the game user interface.
	 */
	public void draw(World world, Stage ui)
	{
		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// Get list of sprites, and clip sprites that are outside of the camera's
		// view.
		spritesInView.clear();
		for (Sprite sprite : spriteSystem.getSprites())
		{
			if (withinCameraView(camera, sprite))
			{
				spritesInView.add(sprite);
			}
		}

		// Sort list by sprite type, to facilitate batching.
		Collections.sort(spritesInView, spriteComparator);

		// Draw the background layer.
		drawBackground(world);

		// Render sprites by layer.
		drawEntities(spritesInView, DrawLayer.FIRST);
		drawEntities(spritesInView, DrawLayer.SECOND);
		drawEntities(spritesInView, DrawLayer.THIRD);
		drawEntities(spritesInView, DrawLayer.FOURTH);
		drawEntities(spritesInView, DrawLayer.TOP);

		// Render the user interface.
		if (ui != null)
		{
			ui.act(Gdx.graphics.getDeltaTime());
			ui.draw();
		}

		// Update the camera controller(s).
		for (CameraController c : cameraControllers)
		{
			c.update(world);
		}

		// Remove destroyed sprites from the list.
		List<Sprite> sprites = spriteSystem.getSprites();
		for (int i = 0; i < sprites.size(); ++i)
		{
			if (sprites.get(i).isDisposed())
			{
				sprites.remove(i);
			}
		}
	}

	/**
	 * Adds the specified camera controller.
	 *
	 * @param controller
	 *            a camera controller. The update method will be called once per draw call.
	 */
	void addCameraController(CameraController controller)
	{
		cameraControllers.add(controller);
	}

	/**
	 * Draw all entities in the specified layer.
	 *
	 * @param entities
	 *            the list of entities.
	 * @param currentLayer
	 *            the current layer to draw.
	 */
	private void drawEntities(List<Sprite> sprites, DrawLayer currentLayer)
	{
		if (sprites.size() == 0)
		{
			return;
		}

		batch.begin();
		for (Sprite sprite : sprites)
		{
			sprite.draw(batch, camera, currentLayer);
		}
		batch.end();
	}

	/**
	 * Draws the background layer. This is needed to fill in visual gaps related to the adaptable tiling system.
	 */
	private void drawBackground(World world)
	{
		if (background == null) {
			initializeBackground();
		}

		batch.begin();
		for (int row = 0; row < background.length; row++) {
			for (int col = 0; col < background[0].length; col++) {
				var sprite = background[row][col];
				var position = Coordinates.cameraToWorld(camera,
						new Vector2(col * Coordinates.TILE_TO_WORLD_SCALE, row * Coordinates.TILE_TO_WORLD_SCALE));
				// Change the positions of the background sprites so they are always on screen.
				sprite.x = (int) position.x;
				sprite.y = (int) position.y;
				sprite.draw(batch, camera, DrawLayer.BACKGROUND);
			}
		}
		batch.end();
	}

	private void initializeBackground() {
		int rows = Math.round(camera.viewportHeight / Coordinates.TILE_TO_WORLD_SCALE) + 1;
		int columns = Math.round(camera.viewportWidth / Coordinates.TILE_TO_WORLD_SCALE) + 1;
		background = new BackgroundSprite[rows][columns];

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				background[row][col] = new BackgroundSprite(col * BackgroundSprite.WIDTH, row * BackgroundSprite.HEIGHT);
			}
		}
	}

	/**
	 * Returns true if the x, y, height and width of the sprite are within the camera's view.
	 *
	 * @param camera
	 *            the game camera.
	 * @param sprite
	 *            the sprite to check.
	 * @return true if the sprite is within the camera's view, or false otherwise.
	 */
	private static boolean withinCameraView(Camera camera, Sprite sprite)
	{
		if (sprite instanceof TankSprite)
		{
			// Always draw the tank, since otherwise the camera won't be attached to it if the tank
			// starts off screen.
			return true;
		}

		float cameraX = camera.position.x;
		float cameraY = camera.position.y;

		float spriteHalfWidth = sprite.getWidth() / 2;
		float spriteHalfHeight = sprite.getHeight() / 2;
		float spriteX = sprite.getX();
		float spriteY = sprite.getY();

		return (spriteX + spriteHalfWidth + cameraX > 0
				&& spriteX - spriteHalfWidth - cameraX < camera.viewportWidth
				&& spriteY + spriteHalfHeight + cameraY > 0
				&& spriteY - spriteHalfHeight - cameraY < camera.viewportHeight);
	}

	/**
	 * Loads all textures. This isn't strictly necessary, but we encountered slight hiccups when a
	 * sprite type was loaded for the first time. This was most noticeable when the first bullet is
	 * fired. Note that only pngs are currently loaded (if all files were loaded, file system
	 * artificacts could be picked up, like the Windows thumbs.db file).
	 */
	private static void loadAllTextures()
	{
		File textureDirectory = new File(TEXTURE_PATH);
		for (File file : textureDirectory.listFiles())
		{
			if (file.getName().endsWith("png"))
			{
				getTexture(TEXTURE_PATH + file.getName());
			}
		}
	}

	/**
	 * Comparator that is used when sorting sprites.
	 *
	 * @author BU CS673 - Clone Productions
	 */
	private static class SpriteComparator implements Comparator<Sprite>
	{
		@Override
		public int compare(Sprite o1, Sprite o2)
		{
			return (o1.getClass().getName().compareTo(o2.getClass().getName()));
		}
	}
}
