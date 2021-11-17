package bubolo.ui.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;

import bubolo.Config;
import bubolo.graphics.Graphics;
import bubolo.input.InputManager.Action;
import bubolo.ui.Screen;
import bubolo.ui.gui.PositionableUiComponent.HOffsetFrom;
import bubolo.ui.gui.PositionableUiComponent.OffsetType;
import bubolo.ui.gui.PositionableUiComponent.VOffsetFrom;

public class GuiTestScreen implements Screen, InputProcessor {
	private final ButtonGroup buttonGroup;
	private final Color clearColor = new Color(0.85f, 0.85f, 0.85f, 1);

	// For scaling window coordinates to screen coordinates.
	private float scaleX = 1;
	private float scaleY = 1;

	public GuiTestScreen() {
		var buttonGroupArgs = new ButtonGroup.Args(300, 50);
		buttonGroupArgs.paddingBetweenButtons = 10;
		var layoutArgs = new LayoutArgs(Config.TargetWindowWidth, Config.TargetWindowHeight, 15);

		buttonGroup = new ButtonGroup(layoutArgs, buttonGroupArgs);
		buttonGroup.addButton("Single Player Game", button -> { System.out.println("I'm an action attached to Single Player Game!"); });
		buttonGroup.addButton("Join Multiplayer Game", button -> System.out.println("Join Multiplayer"));
		buttonGroup.addButton("Host Multiplayer Game", button -> System.out.println("Host Multiplayer"));
		buttonGroup.addButton("Settings", button -> System.out.println("Settings"));
		buttonGroup.addButton("I'm Useless.", button -> System.out.println("Useless"));
		buttonGroup.addButton("Exit", button -> { Gdx.app.exit(); });

		buttonGroup.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		buttonGroup.setVerticalOffset(0.2f, OffsetType.Percent, VOffsetFrom.Top);
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

		buttonGroup.recalculateLayout(newWidth, newHeight);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void onInputAction(Action action) {
		if (action == Action.MenuUp) {
			buttonGroup.selectPrevious();
		} else if (action == Action.MenuDown) {
			buttonGroup.selectNext();
		} else if (action == Action.Activate) {
			buttonGroup.activateSelectedButton();
		} else if (action == Action.Cancel) {
			Gdx.app.exit();
		}
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		buttonGroup.onMouseClicked((int) (screenX * scaleX), (int) (screenY * scaleY));
		buttonGroup.activateSelectedButton();
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return buttonGroup.onMouseMoved((int) (screenX * scaleX), (int) (screenY * scaleY)) != null;
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
	public boolean keyUp(int keycode) {
		return false;
	}
}
