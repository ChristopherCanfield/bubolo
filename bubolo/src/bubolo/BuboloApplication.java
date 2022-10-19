package bubolo;

import static bubolo.Config.DefaultPixelsPerWorldUnit;
import static bubolo.Config.TargetWindowHeight;
import static bubolo.Config.TargetWindowWidth;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;

import bubolo.Systems.NetworkType;
import bubolo.graphics.Graphics;
import bubolo.graphics.TeamColor;
import bubolo.input.InputActionObserver;
import bubolo.input.InputManager.Action;
import bubolo.map.MapImporter;
import bubolo.net.command.CreateTank;
import bubolo.ui.GameScreen;
import bubolo.ui.LoadingScreen;
import bubolo.ui.LobbyScreen;
import bubolo.ui.MainMenuScreen;
import bubolo.ui.MapSelectionScreen;
import bubolo.ui.MultiplayerSetupScreen;
import bubolo.ui.MultiplayerSetupScreen.PlayerType;
import bubolo.ui.Screen;
import bubolo.util.FrameInfo;
import bubolo.util.GameRuntimeException;
import bubolo.util.Nullable;
import bubolo.util.Units;
import bubolo.world.Entity;
import bubolo.world.Tank;
import bubolo.world.Tile;
import bubolo.world.World;

/**
 * The Game: this is where the subsystems are initialized, as well as where the main game loop is.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class BuboloApplication extends AbstractGameApplication implements InputActionObserver {
	private static Logger logger = Logger.getLogger(Config.AppProgramaticTitle);

	private final int windowWidth;
	private final int windowHeight;

	private Graphics graphics;
	private Screen screen;

	private String defaultMapName = "Canfield Island.json";
	private Path mapPath = FileSystems.getDefault().getPath("res", "maps", defaultMapName);

	// Player information for network games.
	private PlayerInfo playerInfo;

	// Whether to print the amount of time each frame takes.
	private boolean printFrameTime;
	private FrameInfo frameInfo;

	/**
	 * Constructs an instance of the game application. Only one instance should ever exist.
	 *
	 * @param windowWidth the width of the window.
	 * @param windowHeight the height of the window.
	 * @param commandLineArgs the arguments passed to the application through the command line. The only application setting is -frameInfo, which prints
	 * frame debug info.
	 */
	public BuboloApplication(int windowWidth, int windowHeight, String[] commandLineArgs) {
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;

		// The first command line argument specifies the map to use. If there is no argument, use the default map.
		for (int i = 0; i < commandLineArgs.length; i++) {
			if (commandLineArgs[i].equals("-frameInfo")) {
				printFrameTime = true;
			}
		}
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		world.addEntityLifetimeObserver(graphics);
		graphics.setWorldSize(world().getWidth(), world().getHeight());
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
		Systems.setGraphics(graphics);
		frameInfo = new FrameInfo(graphics);
		Gdx.input.setInputProcessor(Systems.input());
		Systems.input().addActionObserver(this);

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

			Systems.messenger().update();
			Systems.input().update();

			switch (state) {
				case MultiplayerStarting:
				case MultiplayerLobby:
					Systems.network().update(this);
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
				case SinglePlayerGame: {

					frameInfo.beginFrame();

					world().update();
					Systems.network().update(this);
					graphics.draw(world(), screen);

					frameInfo.endFrame();
					if (printFrameTime) {
						System.out.println(frameInfo.toString());
					}
					break;
				}
				case SinglePlayerLoading: {
					LoadingScreen loadingScreen = (LoadingScreen) screen;
					graphics.draw(loadingScreen);
					if (loadingScreen.drawCount() > 1 && !isReady()) {
						setWorld(importWorld());
						Systems.initializeAudio(world().getWidth(), world().getHeight(),
								TargetWindowWidth * DefaultPixelsPerWorldUnit,
								TargetWindowHeight * DefaultPixelsPerWorldUnit);

						var spawn = world().getRandomSpawn();
						Entity.ConstructionArgs tankSpawnArgs = new Entity.ConstructionArgs(spawn.x(), spawn.y(), 0);

						Tank tank = world().addEntity(Tank.class, tankSpawnArgs);
						tank.initialize("Player 1", TeamColor.Blue, true, world());

						Systems.initializeNetwork(NetworkType.Null);

						setReady(true);
						setState(State.SinglePlayerGame, tank);
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw e;
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
			mapPath = (Path) arg;
			Systems.initializeNetwork();
			screen = new MultiplayerSetupScreen(this, PlayerType.Server);
			break;

		case MultiplayerSetupClient:
			assert previousState == State.MainMenu;
			Systems.initializeNetwork();
			screen = new MultiplayerSetupScreen(this, PlayerType.Client);
			break;

		case MultiplayerLobby:
			assert previousState == State.MultiplayerSetupServer || previousState == State.MultiplayerSetupClient;
			assert arg != null;

			if (previousState == State.MultiplayerSetupServer) {
				setWorld(importWorld());
			}

			this.playerInfo = (PlayerInfo) arg;
			screen = new LobbyScreen(this, graphics, world(), (PlayerInfo) arg);
			break;

		case MultiplayerStarting:
			assert previousState == State.MultiplayerLobby;
			assert screen != null;
			assert arg != null;

			Systems.initializeAudio(world().getWidth(), world().getHeight(),
					TargetWindowWidth * DefaultPixelsPerWorldUnit,
					TargetWindowHeight * DefaultPixelsPerWorldUnit);

			Tile spawnTile = (Tile) arg;
			Entity.ConstructionArgs tankSpawnArgs = new Entity.ConstructionArgs(
					spawnTile.column() * Units.TileToWorldScale,
					spawnTile.row() * Units.TileToWorldScale,
					0);

			Tank tank = world().addEntity(Tank.class, tankSpawnArgs);
			tank.initialize(playerInfo.name(), playerInfo.color(), true, world());

			Systems.network().send(new CreateTank(tank));

			setReady(true);
			break;

		case MultiplayerGame: {
			assert previousState == State.MultiplayerStarting;
			assert screen instanceof LobbyScreen;

			screen.dispose();
			var localTank = world().getLocalTank();
			screen = new GameScreen(localTank.getPlayer());
			localTank.setInventoryObserver((GameScreen) screen);

			break;
		}
		case SinglePlayerLoading: {
			assert previousState == State.SinglePlayerSetup;
			assert mapPath != null;
			mapPath = (Path) arg;
			screen = new LoadingScreen(mapName());
			break;
		}
		case SinglePlayerGame: {
			assert previousState == State.SinglePlayerLoading;
			assert arg != null;

			Tank localTank = (Tank) arg;
			screen = new GameScreen(localTank.getPlayer());
			localTank.setInventoryObserver((GameScreen) screen);

			break;
		}
		case Settings:
			break;
		}
	}

	private World importWorld() {
		try {
			MapImporter importer = new MapImporter();
			World world = importer.importJsonMap(mapPath);
			return world;
		} catch (IOException e) {
			throw new GameRuntimeException(e);
		}
	}

	@Override
	public void resize(int width, int height) {
		if (screen != null) {
			screen.viewportResized(width, height);
		}
		graphics.resize(width, height);
	}

	@Override
	public void onInputAction(Action action) {
		if (action == Action.Quit) {
			Gdx.app.exit();
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
		Systems.dispose();
		graphics.dispose();

		/*
		 * TODO (2021-04-13): After updating to lwjgl3, the process remains in the background even after the window is
		 * closed and this dispose method is called. I'm not sure why that is. System.exit is a temporary hack until I
		 * can look into it further.
		 */
		System.exit(0);
	}
}
