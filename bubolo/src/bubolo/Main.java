package bubolo;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import bubolo.GameApplication.State;
import bubolo.ui.MenuScreen;

/**
 * The application's entry point.
 *
 * @author BU CS673 - Clone Productions
 */
public class Main
{
	private static Application application;

	private static final String APP_DISPLAY_NAME = "Bubolo";
	private static final int INITIAL_SCREEN_WIDTH = 1067;
	private static final int INITIAL_SCREEN_HEIGHT = 600;

	/**
	 * The application's entry point.
	 *
	 * @param args
	 *            Unused. Arguments passed on startup will be ignored.
	 */
	public static void main(String[] args)
	{
		Runnable serverApplication = () -> {
			LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
			cfg.title = APP_DISPLAY_NAME;
			cfg.width = INITIAL_SCREEN_WIDTH;
			cfg.height = INITIAL_SCREEN_HEIGHT;
			setApplication(new LwjglApplication(new BuboloApplication(cfg.width, cfg.height, false,
					State.PLAYER_INFO), cfg));
		};

		Runnable clientApplication = () -> {
			LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
			cfg.title = APP_DISPLAY_NAME;
			cfg.width = INITIAL_SCREEN_WIDTH;
			cfg.height = INITIAL_SCREEN_HEIGHT;
			setApplication(new LwjglApplication(new BuboloApplication(cfg.width, cfg.height, true,
					State.PLAYER_INFO), cfg));
		};

		Runnable singlePlayerApplication = () -> {
			LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
			cfg.title = APP_DISPLAY_NAME;
			cfg.width = INITIAL_SCREEN_WIDTH;
			cfg.height = INITIAL_SCREEN_HEIGHT;
			setApplication(new LwjglApplication(new BuboloApplication(cfg.width, cfg.height, false,
					State.GAME), cfg));
		};

		MenuScreen menuScreen = new MenuScreen(singlePlayerApplication, serverApplication,
				clientApplication);
		menuScreen.setVisible(true);
	}

	/**
	 * Sets the game application.
	 *
	 * @param application
	 *            the game application.
	 */
	static void setApplication(Application application)
	{
		Main.application = application;
	}

	/**
	 * Returns a reference to the application.
	 *
	 * @return the game application.
	 */
	static Application getApplication()
	{
		return Main.application;
	}
}
