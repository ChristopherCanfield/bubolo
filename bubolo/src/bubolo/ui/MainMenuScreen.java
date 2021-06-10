package bubolo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;

import bubolo.BuboloApplication;
import bubolo.Config;
import bubolo.GameApplication.State;
import bubolo.graphics.Graphics;
import bubolo.graphics.gui.Button;
import bubolo.graphics.gui.VButtonGroup;

public class MainMenuScreen implements Screen, InputProcessor {
	private final Color clearColor =  new Color(0.85f, 0.85f, 0.85f, 1);

	// For scaling window coordinates to screen coordinates.
	private float scaleX = 1;
	private float scaleY = 1;

	private final VButtonGroup buttonGroup;
	private final BuboloApplication app;

	public MainMenuScreen(BuboloApplication app) {
		this.app = app;

		var buttonGroupArgs = new VButtonGroup.Args();
		buttonGroupArgs.left = Config.TargetWindowWidth * 0.5f - 100;
		buttonGroupArgs.top = 200;
		buttonGroupArgs.buttonWidth = 300;
		buttonGroupArgs.buttonHeight = 50;
		buttonGroupArgs.paddingBetweenButtons = 10;

		buttonGroup = new VButtonGroup(buttonGroupArgs);
		buttonGroup.addButton("Single Player Game", this::onSinglePlayerButtonActivated);
		buttonGroup.addButton("Join Multiplayer Game", this::onJoinMultiplayerButtonActivated);
		buttonGroup.addButton("Host Multiplayer Game", this::onHostMultiplayerButtonActivated);
		buttonGroup.addButton("Settings", this::onSettingsButtonActivated);
		buttonGroup.addButton("Exit", button -> { Gdx.app.exit(); });
	}

	private void onSinglePlayerButtonActivated(Button button) {
		app.setState(State.SinglePlayerSetup);
	}

	private void onJoinMultiplayerButtonActivated(Button button) {
	}

	private void onHostMultiplayerButtonActivated(Button button) {

	}

	private void onSettingsButtonActivated(Button button) {

	}

	@Override
	public Color clearColor() {
		return clearColor;
	}

	@Override
	public void draw(Graphics graphics) {
		buttonGroup.draw(graphics);
	}

	@Override
	public void resize(int newWidth, int newHeight) {
//		scaleX = (float) Config.TargetWindowWidth / newWidth;
//		scaleY = (float) Config.TargetWindowHeight / newHeight;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.UP || keycode == Keys.W || keycode == Keys.NUMPAD_8) {
			buttonGroup.selectPrevious();
		} else if (keycode == Keys.DOWN || keycode == Keys.S || keycode == Keys.NUMPAD_5 || keycode == Keys.NUMPAD_2) {
			buttonGroup.selectNext();
		} else if (keycode == Keys.SPACE || keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER) {
			buttonGroup.activateSelectedButton();
		} else if (keycode == Keys.ESCAPE) {
			Gdx.app.exit();
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
		int buttonIndex = buttonGroup.onMouseMoved((int) (screenX * scaleX), (int) (screenY * scaleY));
		buttonGroup.selectButton(buttonIndex);
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

	@Override
	public void dispose() {
		if (Gdx.input.getInputProcessor() == this) {
			Gdx.input.setInputProcessor(null);
		}
	}
}
