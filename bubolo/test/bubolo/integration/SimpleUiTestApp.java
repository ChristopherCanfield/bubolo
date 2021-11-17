package bubolo.integration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import bubolo.AbstractGameApplication;
import bubolo.Config;
import bubolo.Systems;
import bubolo.graphics.Graphics;
import bubolo.ui.gui.GuiTestScreen;

public class SimpleUiTestApp extends AbstractGameApplication {
	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
		cfg.setTitle(Config.AppTitle);
		cfg.setWindowedMode(Config.TargetWindowWidth, Config.TargetWindowHeight);
		cfg.setForegroundFPS(Config.FPS);
		cfg.useVsync(false);
		new Lwjgl3Application(new SimpleUiTestApp(), cfg);
	}

	private Graphics graphics;
	private GuiTestScreen screen;


	@Override
	public void create() {
		graphics = new Graphics(Config.TargetWindowWidth, Config.TargetWindowHeight);
		screen = new GuiTestScreen();
		graphics.camera().position.set(0, 0, 0);
		Gdx.input.setInputProcessor(Systems.input());
		Systems.input().addActionObserver(screen);
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
		Systems.input().update();
		graphics.draw(screen);
	}

	@Override
	public void dispose() {
		screen.dispose();
		graphics.dispose();
	}
}
