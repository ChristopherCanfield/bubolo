package bubolo.graphics.gui;

import com.badlogic.gdx.graphics.Color;

import bubolo.Config;
import bubolo.graphics.Graphics;
import bubolo.ui.Screen;

public class GuiTestScreen implements Screen {
	private final VButtonGroup buttonGroup;
	private final Color clearColor = new Color(0.85f, 0.85f, 0.85f, 1);

	public GuiTestScreen() {
		var buttonGroupArgs = new VButtonGroup.Args();
		buttonGroupArgs.left = Config.TargetWindowWidth * 0.5f - 20;
		buttonGroupArgs.top = 200;
		buttonGroupArgs.buttonWidth = 300;
		buttonGroupArgs.buttonHeight = 30;
		buttonGroupArgs.paddingBetweenButtons = 20;

		buttonGroup = new VButtonGroup(buttonGroupArgs);
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
