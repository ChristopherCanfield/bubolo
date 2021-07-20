package bubolo.integration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import bubolo.AbstractGameApplication;
import bubolo.Config;
import bubolo.graphics.Graphics;
import bubolo.ui.gui.MessageBarTestScreen;

public class MesageBarTest extends AbstractGameApplication {
	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
		cfg.setTitle(Config.AppTitle);
		cfg.setWindowedMode(Config.TargetWindowWidth, Config.TargetWindowHeight);
		cfg.setForegroundFPS(Config.FPS);
		cfg.useVsync(false);
		new Lwjgl3Application(new MesageBarTest(), cfg);
	}

	private Graphics graphics;
	private MessageBarTestScreen screen;


	@Override
	public void create() {
		graphics = new Graphics(Config.TargetWindowWidth, Config.TargetWindowHeight);
		screen = new MessageBarTestScreen();
		Gdx.input.setInputProcessor(screen);
	}

	@Override
	public void resize(int width, int height) {
		graphics.resize(width, height);
		if (screen != null) {
			screen.viewportResized(width, height);
		}
	}

	@Override
	public void render() {
		graphics.draw(screen);
	}

	@Override
	public void dispose() {
		screen.dispose();
		graphics.dispose();
	}
}
