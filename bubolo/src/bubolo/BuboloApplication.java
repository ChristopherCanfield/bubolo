package bubolo;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.badlogic.gdx.math.Vector2;

import bubolo.audio.Audio;
import bubolo.graphics.Graphics;
import bubolo.map.MapImporter;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.CreateTank;
import bubolo.ui.LobbyScreen;
import bubolo.ui.PlayerInfoScreen;
import bubolo.ui.Screen;
import bubolo.util.GameRuntimeException;
import bubolo.world.Entity;
import bubolo.world.GameWorld;
import bubolo.world.World;
import bubolo.world.entity.concrete.Spawn;
import bubolo.world.entity.concrete.Tank;

/**
 * The Game: this is where the subsystems are initialized, as well as where the main game loop is.
 *
 * @author BU CS673 - Clone Productions
 */
public class BuboloApplication extends AbstractGameApplication
{
	private static Logger logger = Logger.getLogger(Config.AppProgramaticTitle);

	private final int windowWidth;
	private final int windowHeight;

	private final boolean isClient;
	private final State initialState;

	private Graphics graphics;

	private Network network;

	private Screen screen;

	private Path mapPath = FileSystems.getDefault().getPath("res", "maps/Everard Island.json");

	/**
	 * Constructs an instance of the game application. Only one instance should ever exist.
	 *
	 * @param windowWidth
	 *            the width of the window.
	 * @param windowHeight
	 *            the height of the window.
	 * @param isClient
	 *            specifies whether this is a client player.
	 * @param initialState
	 *            the initial application state.
	 */
	public BuboloApplication(int windowWidth, int windowHeight, boolean isClient, State initialState, String[] commandLineArgs)
	{
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.isClient = isClient;
		this.initialState = initialState;

		if (commandLineArgs.length != 0) {
			Path argPath = FileSystems.getDefault().getPath("res", "maps/" + commandLineArgs[0]);
			if (Files.exists(argPath)) {
				mapPath = argPath;
			}
		}
	}

	/**
	 * Create anything that relies on graphics, sound, windowing, or input devices here.
	 *
	 * @see <a
	 *      href="http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void create()
	{
		initializeLogger();

		Audio.initialize();
		graphics = new Graphics(windowWidth, windowHeight);
		network = NetworkSystem.getInstance();

		// Server or single-player
		if (!isClient)
		{
			try
			{
				MapImporter importer = new MapImporter();
				world = importer.importJsonMap(mapPath, graphics);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				throw new GameRuntimeException(e);
			}
		}
		// Client in net game
		else
		{
			world = new GameWorld();
		}

		world.setEntityCreationObserver(graphics);
		setState(initialState);
	}

	private static void initializeLogger() {
		try {
			// TODO (cdc - 2021-03-16): This file probably belongs in appdata and equivalent on other systems, rather than temp.
			FileHandler fileHandler = new FileHandler("%t" + Config.AppTitle + ".log");
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

		logger.setLevel(Level.WARNING);
	}

	/**
	 * Called automatically by the rendering library.
	 *
	 * @see <a
	 *      href="http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void render()
	{
		final State state = getState();
		if (state == State.NET_GAME)
		{
			graphics.draw(world);
			world.update();
			network.update(world);
		}
		else if (state == State.LOCAL_GAME)
		{
			graphics.draw(world);
			world.update();
		}
		else if (state == State.GAME_LOBBY ||
				state == State.GAME_STARTING)
		{
			graphics.draw(screen);
			network.update(world);
		}
		else if (state == State.PLAYER_INFO)
		{
			graphics.draw(screen);
		}
	}

	@Override
	public void onStateChanged()
	{
		var state = getState();
		switch (state) {
		case NET_GAME: {
			screen.dispose();

			Entity.ConstructionArgs args;
			if (!isClient) {
				Vector2 spawnLocation = getRandomSpawn(world);
				args = new Entity.ConstructionArgs(UUID.randomUUID(), spawnLocation.x, spawnLocation.y, 0);
			} else {
				// TODO (cdc - 2021-03-31): What is the purpose of using this, rather than spawn locations?
				args = new Entity.ConstructionArgs(UUID.randomUUID(), getRandomX(), 200, 0);
			}

			Tank tank = world.addEntity(Tank.class, args);
			tank.setPlayerName(network.getPlayerName());
			tank.setOwnedByLocalPlayer(true);

			network.send(new CreateTank(tank));

			setReady(true);
		} break;
		case LOCAL_GAME: {
			screen.dispose();

			Entity.ConstructionArgs args;
			if (!isClient) {
				Vector2 spawnLocation = getRandomSpawn(world);
				args = new Entity.ConstructionArgs(UUID.randomUUID(), spawnLocation.x, spawnLocation.y, 0);
			} else {
				// TODO (cdc - 2021-03-31): What is the purpose of using this, rather than spawn locations?
				args = new Entity.ConstructionArgs(UUID.randomUUID(), getRandomX(), 200, 0);
			}

			Tank tank = world.addEntity(Tank.class, args);
			tank.setPlayerName(network.getPlayerName());
			tank.setOwnedByLocalPlayer(true);

			network.send(new CreateTank(tank));

			setReady(true);
		} break;
		case GAME_LOBBY:
			screen = new LobbyScreen(this, world);
			break;
		case PLAYER_INFO:
			screen = new PlayerInfoScreen(this, isClient);
			break;
		case GAME_STARTING:
			// Do nothing.
			break;
		case MAIN_MENU:
			// Do nothing.
			// TODO (cdc - 2021-03-31): Allow the main menu to be displayed again.
			break;
		}
	}

	/**
	 * Returns a random spawn point.
	 *
	 * @return the location of a random spawn point.
	 */
	private static Vector2 getRandomSpawn(World world)
	{
		List<Spawn> spawns = world.getSpawns();
		if (spawns.size() > 0)
		{
			Random randomGenerator = new Random();
			Spawn spawn = spawns.get(randomGenerator.nextInt(spawns.size()));
			return new Vector2(spawn.x(), spawn.y());
		}
		return null;
	}

	private static int getRandomX()
	{
		int val = (new Random()).nextInt(10);
		return (1250 + (100 * val));
	}

	/**
	 * Called when the application is destroyed.
	 *
	 * @see <a
	 *      href="http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void dispose()
	{
		Audio.dispose();
	}
}
