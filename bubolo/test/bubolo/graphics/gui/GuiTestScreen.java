package bubolo.graphics.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;

import bubolo.Config;
import bubolo.graphics.Graphics;
import bubolo.ui.Screen;
import bubolo.ui.gui.ButtonGroup;
import bubolo.ui.gui.LayoutArgs;

public class GuiTestScreen implements Screen, InputProcessor {
	private final ButtonGroup buttonGroup;
	private final Color clearColor = new Color(0.85f, 0.85f, 0.85f, 1);

	// For scaling window coordinates to screen coordinates.
	private float scaleX = 1;
	private float scaleY = 1;

	public GuiTestScreen() {
		var buttonGroupArgs = new ButtonGroup.Args(300, 50);
		buttonGroupArgs.paddingBetweenButtons = 10;
		var layoutArgs = new LayoutArgs(Config.TargetWindowWidth, Config.TargetWindowHeight, 0);

		buttonGroup = new ButtonGroup(layoutArgs, buttonGroupArgs);
		buttonGroup.addButton("Single Player Game", button -> { System.out.println("I'm an action attached to Single Player Game!"); });
		buttonGroup.addButton("Join Multiplayer Game");
		buttonGroup.addButton("Host Multiplayer Game");
		buttonGroup.addButton("Settings");
		buttonGroup.addButton("I'm Useless.");
		buttonGroup.addButton("Exit", button -> { Gdx.app.exit(); });
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
	public void viewportResized(int newWidth, int newHeight) {
		scaleX = (float) Config.TargetWindowWidth / newWidth;
		scaleY = (float) Config.TargetWindowHeight / newHeight;
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.UP || keycode == Keys.W || keycode == Keys.NUMPAD_8) {
			buttonGroup.selectPrevious();
		} else if (keycode == Keys.DOWN || keycode == Keys.S || keycode == Keys.NUMPAD_5 || keycode == Keys.NUMPAD_2) {
			buttonGroup.selectNext();
		} else if (keycode == Keys.SPACE || keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER) {
			buttonGroup.activateSelectedButton();
		}

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		buttonGroup.onMouseClicked((int) (screenX * scaleX), (int) (screenY * scaleY));
		buttonGroup.activateSelectedButton();
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return buttonGroup.onMouseMoved((int) (screenX * scaleX), (int) (screenY * scaleY)) != -1;
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
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
