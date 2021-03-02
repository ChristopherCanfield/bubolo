package bubolo.integration;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import bubolo.AbstractGameApplication;
import bubolo.GameApplication;
import bubolo.audio.Audio;
import bubolo.graphics.Graphics;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.world.GameWorld;
import bubolo.world.Tile;
import bubolo.world.World;
import bubolo.world.entity.concrete.Base;
import bubolo.world.entity.concrete.Bullet;
import bubolo.world.entity.concrete.Crater;
import bubolo.world.entity.concrete.DeepWater;
import bubolo.world.entity.concrete.Engineer;
import bubolo.world.entity.concrete.Grass;
import bubolo.world.entity.concrete.Mine;
import bubolo.world.entity.concrete.MineExplosion;
import bubolo.world.entity.concrete.Pillbox;
import bubolo.world.entity.concrete.Road;
import bubolo.world.entity.concrete.Rubble;
import bubolo.world.entity.concrete.Swamp;
import bubolo.world.entity.concrete.Tank;
import bubolo.world.entity.concrete.Tree;
import bubolo.world.entity.concrete.Wall;
import bubolo.world.entity.concrete.Water;

/**
 * For testing only.
 * 
 * @author BU CS673 - Clone Productions
 */
public class GraphicsTestApplication extends AbstractGameApplication
{
	public static void main(String[] args)
	{
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "BUBOLO Tank Controller Integration";
		cfg.width = 1067;
		cfg.height = 600;
		new LwjglApplication(new GraphicsTestApplication(1067, 600), cfg);
	}
	
	private int windowWidth;
	private int windowHeight;
	
	private Graphics graphics;
	
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
	public GraphicsTestApplication(int windowWidth, int windowHeight)
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
		Audio.initialize();
		
		Network net = NetworkSystem.getInstance();
		net.startDebug();
		
		graphics = new Graphics(windowWidth, windowHeight);
		
		world = new GameWorld(50*32, 50*32);
		
		Tile[][] mapTiles = new Tile[50][50];
		
		for (int row = 0; row < 50; row++)
		{
			for (int column = 0; column < 50; column++)
			{
				Grass grass = (Grass) world.addEntity(Grass.class).setTransform(column, row, 0);
				mapTiles[column][row] = new Tile(column, row, grass);
				
			}
		}
		
		world.setMapTiles(mapTiles);
		
		// TODO: Adjust as needed.
		Tank t = (Tank) world.addEntity(Tank.class).setTransform(100, 100, 0);
		t.setLocalPlayer(true);
		world.addEntity(Base.class).setTransform(32*6, 32*4, 0);
		world.addEntity(Bullet.class).setTransform(32*7, 32*4, 90);
		world.addEntity(Crater.class).setTransform(32*8, 32*5, 0);
		world.addEntity(DeepWater.class).setTransform(32*7, 32*6, 0);
		world.addEntity(Engineer.class).setTransform(32*8, 32*6, 0);
		world.addEntity(Mine.class).setTransform(32*8, 32*7, 0);
		world.addEntity(Pillbox.class).setTransform(32*9, 32*6, 0);
		world.addEntity(MineExplosion.class).setTransform(32*11, 32*11, 0);
		
		// 2 roads
		world.addEntity(Road.class).setTransform(32*10, 32*10, 0);
		world.addEntity(Road.class).setTransform(32*10, 32*11, 0);
		
		world.addEntity(Rubble.class).setTransform(32*11, 32*6, 0);
		world.addEntity(Swamp.class).setTransform(32*12, 32*11, 0);
		world.addEntity(Tree.class).setTransform(32*12, 32*12, 0);
		world.addEntity(Wall.class).setTransform(32*13, 32*12, 0);
		world.addEntity(Water.class).setTransform(32*14, 32*12, 0);
		
		setReady(true);
	}
	
	/**
	 * Called automatically by the rendering library.
	 * @see <a href="http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void render()
	{
		graphics.draw(world);
		world.update();
		
		// (cdc - 4/3/2014): Commented out, b/c update was being called twice. Additionally,
		// the game is extremely jittery when this is used instead of calling update continuously.
		
		// Ensure that the world is only updated as frequently as MILLIS_PER_TICK. 
//		long currentMillis = System.currentTimeMillis();
//		if (currentMillis > (lastUpdate + MILLIS_PER_TICK))
//		{
//			world.update();
//			lastUpdate = currentMillis;
//		}
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
