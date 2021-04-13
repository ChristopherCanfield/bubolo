package bubolo;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import bubolo.BuboloApplication.PlayerType;
import bubolo.ui.MenuScreen;

/**
 * The application's entry point.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Main {

	/**
	 * The application's entry point.
	 *
	 * @param args command line arguments are passed to the application on startup.
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Runnable serverApplication = () -> {
			var cfg = defaultLwjglAppConfig();
			new Lwjgl3Application(
					new BuboloApplication(Config.InitialWindowWidth, Config.InitialWindowHeight, PlayerType.Host, args), cfg);
		};

		Runnable clientApplication = () -> {
			var cfg = defaultLwjglAppConfig();
			new Lwjgl3Application(
					new BuboloApplication(Config.InitialWindowWidth, Config.InitialWindowHeight, PlayerType.Client, args), cfg);
		};

		Runnable singlePlayerApplication = () -> {
			var cfg = defaultLwjglAppConfig();
			new Lwjgl3Application(new BuboloApplication(Config.InitialWindowWidth, Config.InitialWindowHeight,
					PlayerType.LocalSinglePlayer, args), cfg);
		};

		MenuScreen menuScreen = new MenuScreen(singlePlayerApplication, serverApplication, clientApplication);
		menuScreen.setVisible(true);
	}

	private static Lwjgl3ApplicationConfiguration defaultLwjglAppConfig() {
		Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
		cfg.setTitle(Config.AppTitle);
		cfg.setWindowedMode(Config.InitialWindowWidth, Config.InitialWindowHeight);
		cfg.setForegroundFPS(Config.FPS);
		return cfg;
	}
}
