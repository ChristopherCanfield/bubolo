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
import bubolo.graphics.Graphics;
import bubolo.map.MapImporter;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.CreateTank;
import bubolo.ui.LoadingScreen;
import bubolo.ui.LobbyScreen;
import bubolo.ui.MainMenuScreen;
import bubolo.ui.MapSelectionScreen;
import bubolo.ui.MultiplayerSetupScreen;
import bubolo.ui.MultiplayerSetupScreen.PlayerType;
import bubolo.ui.Screen;
import bubolo.util.GameRuntimeException;
import bubolo.util.Nullable;
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
	private Screen screen;

	private String mapName = "Canfield Island.json";
	private Path mapPath = FileSystems.getDefault().getPath("res", "maps", mapName);

	/**
	 * Constructs an instance of the game application. Only one instance should ever exist.
	 *
	 * @param windowWidth the width of the window.
	 * @param windowHeight the height of the window.
	 * @param commandLineArgs the arguments passed to the application through the command line. The first argument
	 * specifies the map to use. Any additional arguments are ignored.
	 */
	public BuboloApplication(int windowWidth, int windowHeight, String[] commandLineArgs) {
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;

		// The first command line argument specifies the map to use. If there is no argument, use the default map.
		if (commandLineArgs.length != 0) {
			mapName = commandLineArgs[0];
			Path argPath = FileSystems.getDefault().getPath("res", "maps", mapName);
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

	public String mapName() {
		return mapPath.getFileName().toString().replace(Config.MapFileExtension, "");
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

		setState(State.MainMenu);
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

			switch (state) {
			case MultiplayerStarting:
			case MultiplayerLobby:
				network.update(this);
				//$FALL-THROUGH$
			case MainMenu:
			case MultiplayerMapSelection:
			case MultiplayerSetupClient:
			case MultiplayerSetupServer:
			case SinglePlayerSetup:
			case Settings:
				graphics.draw(screen);
				break;
			case MultiplayerGame:
			case SinglePlayerGame:
				graphics.draw(world);
				world.update();
				network.update(this);
				break;
			case SinglePlayerLoading:
				LoadingScreen loadingScreen = (LoadingScreen) screen;
				graphics.draw(loadingScreen);
				if (loadingScreen.drawCount() > 1 && !isReady()) {
					setUpWorld();
					Audio.initialize(world().getWidth(), world().getHeight(), TargetWindowWidth * DefaultPixelsPerWorldUnit,
							TargetWindowHeight * DefaultPixelsPerWorldUnit);

					var spawn = world().getRandomSpawn();
					Entity.ConstructionArgs args = new Entity.ConstructionArgs(Entity.nextId(), spawn.x(), spawn.y(), 0);

					Tank tank = world().addEntity(Tank.class, args);
					tank.setControlledByLocalPlayer(true);

					network.startDebug();
					setReady(true);
					setState(State.SinglePlayerGame);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	@Override
	protected void onStateChanged(State previousState, State newState, @Nullable Object arg) {
		if (screen != null && !(screen instanceof LobbyScreen)) {
			screen.dispose();
			screen = null;
		}

		switch (newState) {
		case MainMenu:
			screen = new MainMenuScreen(this);
			graphics.camera().position.set(0, 0, 0);
			break;
		case MultiplayerMapSelection:
		case SinglePlayerSetup:
			assert previousState == State.MainMenu || previousState == State.MultiplayerSetupServer;
			State nextState = (newState == State.SinglePlayerSetup) ? State.SinglePlayerLoading : State.MultiplayerSetupServer;
			screen = new MapSelectionScreen(this, nextState);
			break;
		case MultiplayerSetupServer:
			assert previousState == State.MultiplayerMapSelection;
			assert mapPath != null;
			screen = new MultiplayerSetupScreen(this, PlayerType.Server);
			mapPath = (Path) arg;
			break;
		case MultiplayerSetupClient:
			assert previousState == State.MainMenu;
			screen = new MultiplayerSetupScreen(this, PlayerType.Client);
			break;
		case MultiplayerLobby:
			if (previousState == State.MultiplayerSetupServer) {
				setUpWorld();
			}
			screen = new LobbyScreen(this, world());
			break;
		case MultiplayerStarting:
			assert previousState == State.MultiplayerLobby;
			assert screen != null;

			Audio.initialize(world().getWidth(), world().getHeight(), TargetWindowWidth * DefaultPixelsPerWorldUnit,
					TargetWindowHeight * DefaultPixelsPerWorldUnit);

			var spawn = world().getRandomSpawn();
			Entity.ConstructionArgs args = new Entity.ConstructionArgs(Entity.nextId(), spawn.x(), spawn.y(), 0);

			Tank tank = world().addEntity(Tank.class, args);
			tank.setPlayerName(network.getPlayerName());
			tank.setControlledByLocalPlayer(true);

			network.send(new CreateTank(tank));

			setReady(true);
			break;
		case MultiplayerGame: {
			if (screen != null) {
				screen.dispose();
				screen = null;
			}
			break;
		}
		case SinglePlayerLoading:
			assert previousState == State.SinglePlayerSetup;
			assert mapPath != null;
			mapPath = (Path) arg;
			screen = new LoadingScreen(mapName());
			break;
		case SinglePlayerGame: {
			assert previousState == State.SinglePlayerLoading;
			break;
		}
		case Settings:
			break;
		}
	}

	private void setUpWorld() {
		try {
			MapImporter importer = new MapImporter();
			World world = importer.importJsonMap(mapPath);
			setWorld(world);
		} catch (IOException e) {
			throw new GameRuntimeException(e);
		}
	}

	@Override
	public void resize(int width, int height) {
		graphics.resize(width, height);
		if (screen != null) {
			screen.onViewportResized(width, height);
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
