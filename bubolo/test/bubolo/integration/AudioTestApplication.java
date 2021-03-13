package bubolo.integration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import bubolo.AbstractGameApplication;
import bubolo.audio.Audio;
import bubolo.audio.Sfx;
import bubolo.graphics.Graphics;
import bubolo.world.GameWorld;

/**
 * For testing only.
 *
 * @author BU CS673 - Clone Productions
 */
public class AudioTestApplication extends AbstractGameApplication
{
	public static void main(String[] args)
	{
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "BUBOLO Audio Integration";
		cfg.width = 600;
		cfg.height = 600;
		new LwjglApplication(new AudioTestApplication(600, 600), cfg);
	}

	private int windowWidth;
	private int windowHeight;

	private Graphics graphics;

	private long lastUpdate;

	private int frame = 0;
	private long MAX_FRAMES = TICKS_PER_SECOND * 15;

	/**
	 * The number of game ticks (calls to <code>update</code>) per second.
	 */
	public static final long TICKS_PER_SECOND = 30;

	/**
	 * The number of milliseconds per game tick.
	 */
	public static final long MILLIS_PER_TICK = 1000 / TICKS_PER_SECOND;

	/**
	 * Constructs an instance of the game application. Only one instance should
	 * ever exist.
	 * @param windowWidth the width of the window.
	 * @param windowHeight the height of the window.
	 */
	public AudioTestApplication(int windowWidth, int windowHeight)
	{
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
	}

	/**
	 * Create anything that relies on graphics, sound, windowing, or input devices here.
	 * @see <a href="http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void create()
	{
		graphics = new Graphics(windowWidth, windowHeight);

		world = new GameWorld(32*30, 32*30);

		Audio.startMusic();

		setReady(true);
	}

	/**
	 * Called automatically by the rendering library.
	 * @see <a href="http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void render()
	{
		if (frame == 0) Audio.play(Sfx.CANNON_FIRED);
		if (frame == 30) Audio.play(Sfx.EXPLOSION);
		if (frame == 45) Audio.play(Sfx.PILLBOX_BUILT);
		if (frame == 60) Audio.play(Sfx.PILLBOX_HIT);
		if (frame == 75) Audio.play(Sfx.ROAD_BUILT);
		if (frame == 90) Audio.play(Sfx.TANK_DROWNED);
		if (frame == 105) Audio.play(Sfx.TANK_HIT);
		if (frame == 120) Audio.play(Sfx.TANK_IN_SHALLOW_WATER);
		if (frame == 135) Audio.play(Sfx.TREE_GATHERED);
		if (frame == 150) Audio.play(Sfx.TREE_HIT);
		if (frame == 165) Audio.play(Sfx.WALL_BUILT);
		if (frame == 180) Audio.play(Sfx.WALL_HIT);
		if (frame == 210) Audio.play(Sfx.TANK_EXPLOSION);
		if (frame == 230) Audio.play(Sfx.MINE_EXPLOSION);

		graphics.draw(world);

		if (frame > MAX_FRAMES)
		{
			Gdx.app.exit();
		}
		++frame;

		// Ensure that the world is only updated as frequently as MILLIS_PER_TICK.
		long currentMillis = System.currentTimeMillis();
		if (currentMillis > (lastUpdate + MILLIS_PER_TICK))
		{
			world.update();
			lastUpdate = currentMillis;
		}
	}

	/**
	 * Called when the application is destroyed.
	 * @see <a href="http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void dispose()
	{
		Audio.dispose();
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void resume()
	{
	}
}
