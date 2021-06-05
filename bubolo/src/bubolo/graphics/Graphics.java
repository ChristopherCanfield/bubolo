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
import com.badlogic.gdx.scenes.scene2d.Stage;

import bubolo.ui.Screen;
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
	/**
	 * Textures file path.
	 */
	private static final String TEXTURE_PATH = "res/textures/";

	// Stores the textures, so that only one copy is stored in memory.
	private static Map<String, Texture> textures = new HashMap<>();
	private static Map<String, TextureRegion[]> textureRegions1d = new HashMap<>();
	private static Map<String, TextureRegion[][]> textureRegions2d = new HashMap<>();

	private final SpriteBatch batch;
	private final Camera camera;
	private final ShapeRenderer shapeRenderer;

	private SpriteSystem spriteSystem;

	// The attached camera controllers.
	private CameraController cameraController;

	// The comparator used to sort sprites.
	private static final Comparator<Sprite> spriteComparator = Comparator.comparing(Sprite::getDrawLayer)
			.thenComparing(s -> s.getClass().getSimpleName());

	private final List<Sprite> spritesInView = new ArrayList<Sprite>();

	private final Timer<Graphics> timer = new Timer<Graphics>(10);

	/**
	 * Returns a texture from a file name. Ensures that the same texture isn't stored multiple times. Will load the file
	 * if it has not yet been loaded.
	 *
	 * @param fileName the name of the texture file. Do not include the full path.
	 * @return the requested texture.
	 */
	static Texture getTexture(String fileName) {
		Texture texture = textures.get(fileName);
		if (texture == null) {
			texture = new Texture(new FileHandle(new File(TEXTURE_PATH + fileName)));
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
	 * Returns a texture region from a path that points to a texture. Ensures that the same texture region isn't stored
	 * multiple times. Will create the region if it has not yet been created.
	 *
	 * @param path the path to the texture file.
	 * @param frames the number of frames in the file.
	 * @param frameWidth the width of each frame.
	 * @param paddingWidth the horizontal padding between frames.
	 * @return reference to the texture region array.
	 */
	static TextureRegion[] getTextureRegion1d(String path, int frames, int frameWidth, int paddingWidth) {
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
	static TextureRegion[][] getTextureRegion2d(String path, int frameWidth, int frameHeight) {
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
	static TextureRegion[][] getTextureRegion2d(String path, int frameWidth, int frameHeight, int framePaddingWidth, int framePaddingHeight) {
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
	 * @param windowWidth the width of the window, in pixels.
	 * @param windowHeight the height of the window, in pixels.
	 */
	public Graphics(int windowWidth, int windowHeight) {
		camera = new OrthographicCamera(windowWidth, windowHeight);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		spriteSystem = new SpriteSystem();

		loadAllTextures();
	}

	public Camera camera() {
		return camera;
	}

	public Batch batch() {
		return batch;
	}

	public ShapeRenderer shapeRenderer() {
		return shapeRenderer;
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
	 * Draws the specified screen.
	 *
	 * @param screen the ui screen to update.
	 */
	public void draw(Screen screen) {
		var clearColor = screen.clearColor();
		Gdx.gl20.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		screen.draw(this);
	}

	/**
	 * Draws the entities that are within the camera's clipping boundary.
	 *
	 * @param world reference to the World Model object.
	 */
	public void draw(World world) {
		draw(world, null);
	}

	/**
	 * Draws the entities that are within the camera's clipping boundary.
	 *
	 * @param world the World Model object.
	 * @param ui the game user interface.
	 */
	public void draw(World world, Stage ui) {
		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		timer.update(this);

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

		// Render the user interface.
		if (ui != null) {
			ui.act(Gdx.graphics.getDeltaTime());
			ui.draw();
		}

		// Update the camera controller.
		if (cameraController != null) {
			cameraController.update(world);
		}

		// Remove destroyed sprites from the list.
		List<Sprite> sprites = spriteSystem.getSprites();
		for (int i = 0; i < sprites.size(); ++i) {
			if (sprites.get(i).isDisposed()) {
				sprites.remove(i);
			}
		}
	}

	/**
	 * Attaches the specified camera controller to the graphics system. Only one camera controller can be attached at a
	 * time. The camera controller's update method will be called once per draw call.
	 *
	 * @param controller the camera controller to attach.
	 */
	void setCameraController(CameraController controller) {
		cameraController = controller;
	}

	/**
	 * Draw all sprites, ordered by draw layer.
	 *
	 * @param sprites the list of sprites that will be drawn.
	 */
	private void drawSpritesByLayer(List<Sprite> sprites) {
		// Sort list by draw layer, to ensure that sprites are drawn in the correct order,
		// then by sprite type, to facilitate batching.
		Collections.sort(spritesInView, spriteComparator);

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
		if (sprite instanceof TankSprite tankSprite) {
			if (tankSprite.getEntity().isOwnedByLocalPlayer()) {
				return true;
			}
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
		File textureDirectory = new File(TEXTURE_PATH);
		for (File file : textureDirectory.listFiles()) {
			if (file.getName().endsWith("png")) {
				getTexture(file.getName());
			}
		}
	}
}
