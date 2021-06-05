package bubolo.graphics.gui;

import com.badlogic.gdx.graphics.Color;

import bubolo.Config;
import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.ui.Screen;

public class GuiTestScreen implements Screen {
	private final VButtonGroup buttonGroup;
	private final Color clearColor = new Color(0.25f, 0.25f, 0.25f, 1);

	public GuiTestScreen() {
		buttonGroup = new VButtonGroup(Config.TargetWindowWidth / 0.5f - 20, 20, 100, 30, Fonts.Arial18);
		buttonGroup.addButton("Hello!");
		buttonGroup.addButton("I'm button two!");
		buttonGroup.addButton("Host Multiplayer Game");
		buttonGroup.addButton("Settings");
	}

	@Override
	public void draw(Graphics graphics) {
		buttonGroup.draw(graphics);
	}

	@Override
	public Color clearColor() {
		return clearColor;
	}

	@Override
	public void resize(int newWidth, int newHeight) {
	}

	@Override
	public void dispose() {
	}
}
