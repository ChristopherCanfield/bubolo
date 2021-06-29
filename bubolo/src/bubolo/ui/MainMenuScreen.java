package bubolo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import bubolo.BuboloApplication;
import bubolo.Config;
import bubolo.GameApplication.State;
import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.ui.gui.Button;
import bubolo.ui.gui.Label;
import bubolo.ui.gui.LayoutArgs;
import bubolo.ui.gui.UiComponent.HOffsetFrom;
import bubolo.ui.gui.UiComponent.OffsetType;
import bubolo.ui.gui.UiComponent.VOffsetFrom;
import bubolo.ui.gui.VButtonGroup;

public class MainMenuScreen implements Screen, InputProcessor {
	private final Color clearColor =  new Color(0.85f, 0.85f, 0.85f, 1);

	private VButtonGroup buttonGroup;
	private Label versionText;
	private final BuboloApplication app;

	private final Color backgroundDistortionColor = new Color(1, 1, 1, 0f);
	private final Texture backgroundTexture;

	public MainMenuScreen(BuboloApplication app) {
		this.app = app;

		this.backgroundTexture = new Texture(new FileHandle(Config.UiPath.resolve("main_menu_background_blurred.png").toFile()));

		addButtonGroup();
		addVersionText();

		Gdx.input.setInputProcessor(this);
	}

	private void addButtonGroup() {
		var buttonGroupArgs = new VButtonGroup.Args(300, 50);
		buttonGroupArgs.paddingBetweenButtons = 10;
		buttonGroupArgs.backgroundColor = new Color(0.5f, 0.5f, 0.5f, 0.75f);
		buttonGroupArgs.buttonBackgroundColor = new Color(1, 1, 1, 0.75f);

		var layoutArgs = new LayoutArgs(0, 0, Config.TargetWindowWidth, Config.TargetWindowHeight, 55);

		buttonGroup = new VButtonGroup(layoutArgs, buttonGroupArgs);
		buttonGroup.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		buttonGroup.setVerticalOffset(0, OffsetType.ScreenUnits, VOffsetFrom.Center);
		buttonGroup.addButton("Single Player Game", this::onSinglePlayerButtonActivated);
		buttonGroup.addButton("Join Multiplayer Game", this::onJoinMultiplayerButtonActivated);
		buttonGroup.addButton("Host Multiplayer Game", this::onHostMultiplayerButtonActivated);
		buttonGroup.addButton("Settings", this::onSettingsButtonActivated);
		buttonGroup.addButton("Exit", button -> { Gdx.app.exit(); });
	}

	private void addVersionText() {
		var layoutArgs = new LayoutArgs(0, 0, Config.TargetWindowWidth, Config.TargetWindowHeight, 0);
		versionText = new Label(layoutArgs, Config.Version, Fonts.Arial16, Color.WHITE);
		versionText.setVerticalOffset(0.975f, OffsetType.Percent, VOffsetFrom.Top);
		versionText.setHorizontalOffset(5, OffsetType.ScreenUnits, HOffsetFrom.Left);
		versionText.recalculateLayout(0, 0, Config.TargetWindowWidth, Config.TargetWindowHeight);
	}

	private void onSinglePlayerButtonActivated(Button button) {
		app.setState(State.SinglePlayerSetup);
	}

	private void onJoinMultiplayerButtonActivated(Button button) {
		app.setState(State.MultiplayerSetupClient);
	}

	private void onHostMultiplayerButtonActivated(Button button) {
		app.setState(State.MultiplayerMapSelection);
	}

	private void onSettingsButtonActivated(Button button) {

	}

	@Override
	public Color clearColor() {
		return clearColor;
	}

	@Override
	public void draw(Graphics graphics) {
		graphics.batch().begin();
		graphics.batch().draw(backgroundTexture, 0, 0, graphics.camera().viewportWidth, graphics.camera().viewportHeight);
		graphics.batch().end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		var shapeRenderer = graphics.shapeRenderer();
		shapeRenderer.setColor(backgroundDistortionColor);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.rect(0, 0, graphics.camera().viewportWidth, graphics.camera().viewportHeight);
		shapeRenderer.end();

		buttonGroup.draw(graphics);
		versionText.draw(graphics);

		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	@Override
	public void onViewportResized(int newWidth, int newHeight) {
		buttonGroup.recalculateLayout(0, 0, newWidth, newHeight);
		versionText.recalculateLayout(0, 0, newWidth, newHeight);
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
		buttonGroup.onMouseClicked(screenX, screenY);
		buttonGroup.activateSelectedButton();
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		int buttonIndex = buttonGroup.onMouseMoved(screenX, screenY);
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
