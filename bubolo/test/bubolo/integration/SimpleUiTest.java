package bubolo.integration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import bubolo.AbstractGameApplication;
import bubolo.Config;
import bubolo.graphics.Graphics;
import bubolo.graphics.gui.GuiTestScreen;

public class SimpleUiTest extends AbstractGameApplication {
	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
		cfg.setTitle(Config.AppTitle);
		cfg.setWindowedMode(Config.TargetWindowWidth, Config.TargetWindowHeight);
		cfg.setForegroundFPS(Config.FPS);
		cfg.useVsync(false);
		new Lwjgl3Application(new SimpleUiTest(), cfg);
	}

	private Graphics graphics;
	private GuiTestScreen screen;


	@Override
	public void create() {
		graphics = new Graphics(Config.TargetWindowWidth, Config.TargetWindowHeight);
		screen = new GuiTestScreen();
		graphics.camera().position.set(0, 0, 0);
		Gdx.input.setInputProcessor(screen);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render() {
		graphics.draw(screen);
	}

	@Override
	public void dispose() {
		graphics.dispose();
	}
}
