package bubolo;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

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
	public static void main(String[] args) {
		var cfg = defaultLwjglAppConfig();
		new Lwjgl3Application(new BuboloApplication(Config.TargetWindowWidth, Config.TargetWindowHeight, args), cfg);
	}

	private static Lwjgl3ApplicationConfiguration defaultLwjglAppConfig() {
		Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
		cfg.setTitle(Config.AppTitle);
		cfg.setWindowedMode(Config.TargetWindowWidth, Config.TargetWindowHeight);
		cfg.setForegroundFPS(Config.FPS);
		cfg.useVsync(false);
		cfg.setWindowIcon(Config.AppIcon16x16.toString(), Config.AppIcon32x32.toString(), Config.AppIcon48x48.toString());
		return cfg;
	}
}
