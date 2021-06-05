package bubolo.graphics.gui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;

import bubolo.Config;
import bubolo.graphics.Graphics;
import bubolo.ui.Screen;

public class GuiTestScreen implements Screen, InputProcessor {
	private final VButtonGroup buttonGroup;
	private final Color clearColor = new Color(0.85f, 0.85f, 0.85f, 1);

	public GuiTestScreen() {
		var buttonGroupArgs = new VButtonGroup.Args();
		buttonGroupArgs.left = Config.TargetWindowWidth * 0.5f - 100;
		buttonGroupArgs.top = 200;
		buttonGroupArgs.buttonWidth = 300;
		buttonGroupArgs.buttonHeight = 50;
		buttonGroupArgs.paddingBetweenButtons = 10;

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

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return buttonGroup.onMouseClicked(screenX, screenY);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return buttonGroup.onMouseMoved(screenX, screenY);
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
