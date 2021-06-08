package bubolo;

import static bubolo.Config.DefaultPixelsPerWorldUnit;
import static bubolo.Config.TargetWindowHeight;
import static bubolo.Config.TargetWindowWidth;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import bubolo.audio.Audio;
import bubolo.controllers.ai.ForestGrowthController;
import bubolo.graphics.Graphics;
import bubolo.map.MapImporter;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.CreateTank;
import bubolo.ui.LobbyScreen;
import bubolo.ui.PlayerInfoScreen;
import bubolo.ui.Stage2dScreen;
import bubolo.util.GameRuntimeException;
import bubolo.world.Entity;
import bubolo.world.Tank;
import bubolo.world.World;

/**
 * The Game: this is where the subsystems are initialized, as well as where the main game loop is.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class BuboloApplication extends AbstractGameApplication {
	private static Logger logger = Logger.getLogger(Config.AppProgramaticTitle);

	private final int windowWidth;
	private final int windowHeight;

	private Graphics graphics;

	private Network network;

	private Stage2dScreen screen;

	private Path mapPath = FileSystems.getDefault().getPath("res", "maps/Canfield Island.json");

	public enum PlayerType {
		LocalSinglePlayer, Host, Client
	}

	private final PlayerType playerType;

	/**
	 * Constructs an instance of the game application. Only one instance should ever exist.
	 *
	 * @param windowWidth the width of the window.
	 * @param windowHeight the height of the window.
	 * @param playerType whether this is a local single player, network host, or network client.
	 * @param commandLineArgs the arguments passed to the application through the command line. The first argument
	 * specifies the map to use. Any additional arguments are ignored.
	 */
	public BuboloApplication(int windowWidth, int windowHeight, PlayerType playerType, String[] commandLineArgs) {
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.playerType = playerType;

		// The first command line argument specifies the map to use. If there is no argument, use the default map.
		if (commandLineArgs.length != 0) {
			Path argPath = FileSystems.getDefault().getPath("res", "maps/" + commandLineArgs[0]);
			if (Files.exists(argPath)) {
				mapPath = argPath;
			}
		}
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		world.addEntityLifetimeObserver(graphics);
	}

	/**
	 * Create anything that relies on graphics, sound, windowing, or input devices here.
	 *
	 * @see <a href=
	 * "http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void create() {
		initializeLogger();

		graphics = new Graphics(windowWidth, windowHeight);
		network = NetworkSystem.getInstance();

		// Server or single-player
		if (playerType != PlayerType.Client) {
			try {
				MapImporter importer = new MapImporter();
				// Import the map.
				var world = importer.importJsonMap(mapPath);
				setWorld(world);
				world.addEntityLifetimeObserver(new ForestGrowthController());
			} catch (IOException e) {
				e.printStackTrace();
				throw new GameRuntimeException(e);
			}
		}

		if (playerType == PlayerType.LocalSinglePlayer) {
			setState(State.SinglePlayerGame);
		} else {
			setState(State.NetGameSetup);
		}
	}

	private static void initializeLogger() {
		try {
			// TODO (cdc - 2021-03-16): This log file probably belongs in appdata and equivalent on other systems,
			// rather than temp.
			FileHandler fileHandler = new FileHandler("%t" + Config.AppTitle + ".log", 5_000, 3, true);
			logger.addHandler(fileHandler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

		logger.setLevel(Level.WARNING);
	}

	/**
	 * Called automatically by the rendering library.
	 *
	 * @see <a href=
	 * "http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void render() {
		try {
			final State state = getState();
			World world = world();
			if (state == State.MultiplayerGame) {
				graphics.draw(world);
				world.update();
				network.update(this);
			} else if (state == State.SinglePlayerGame) {
				graphics.draw(world);
				world.update();
			} else if (state == State.NetGameLobby || state == State.NetGameStarting) {
				graphics.draw(screen);
				network.update(this);
			} else if (state == State.NetGameSetup) {
				graphics.draw(screen);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	@Override
	protected void onStateChanged(State previousState, State newState) {
		World world = world();

		switch (newState) {
		case MainMenu:
			// Do nothing.
			// TODO (cdc - 2021-03-31): Allow the main menu to be displayed again.
			break;
		case NetGameSetup:
			boolean isClient = (playerType == PlayerType.Client);
			screen = new PlayerInfoScreen(this, isClient);
			break;
		case NetGameLobby:
			screen = new LobbyScreen(this, world);
			break;
		case NetGameStarting:
			assert previousState == State.NetGameLobby;
			// Do nothing.
			break;
		case MultiplayerGame: {
			screen.dispose();

			Audio.initialize(world.getWidth(), world.getHeight(), TargetWindowWidth * DefaultPixelsPerWorldUnit,
					TargetWindowHeight * DefaultPixelsPerWorldUnit);

			var spawn = world.getRandomSpawn();
			Entity.ConstructionArgs args = new Entity.ConstructionArgs(Entity.nextId(), spawn.x(), spawn.y(), 0);

			Tank tank = world.addEntity(Tank.class, args);
			tank.setPlayerName(network.getPlayerName());
			tank.setControlledByLocalPlayer(true);

			network.send(new CreateTank(tank));

			setReady(true);
			break;
		}
		case SinglePlayerSetup:
			assert previousState == State.MainMenu;

			// TODO (cdc - 2021-06-08): Implement this.
			break;
		case SinglePlayerGame: {
			if (screen != null) {
				screen.dispose();
			}

			Audio.initialize(world.getWidth(), world.getHeight(), TargetWindowWidth * DefaultPixelsPerWorldUnit,
					TargetWindowHeight * DefaultPixelsPerWorldUnit);

			var spawn = world.getRandomSpawn();
			Entity.ConstructionArgs args = new Entity.ConstructionArgs(Entity.nextId(), spawn.x(), spawn.y(), 0);

			Tank tank = world.addEntity(Tank.class, args);
			tank.setControlledByLocalPlayer(true);

			network.startDebug();

			setReady(true);
			break;
		}
		case Settings:
			break;
		}
	}

	@Override
	public void resize(int width, int height) {
		if (screen != null) {
			screen.resize(width, height);
		}
	}

	/**
	 * Called when the application is destroyed.
	 *
	 * @see <a href=
	 * "http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void dispose() {
		Audio.dispose();
		NetworkSystem.getInstance().dispose();
		graphics.dispose();
		/*
		 * TODO (2021-04-13): After updating to lwjgl3, the process remains in the background even after the window is
		 * closed and this dispose method is called. I'm not sure why that is. System.exit is a temporary hack until I
		 * can look into it further.
		 */
		System.exit(0);
	}
}
