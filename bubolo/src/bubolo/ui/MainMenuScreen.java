package bubolo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import bubolo.ui.gui.ButtonGroup;
import bubolo.ui.gui.Label;
import bubolo.ui.gui.LayoutArgs;
import bubolo.ui.gui.PositionableUiComponent.HOffsetFrom;
import bubolo.ui.gui.PositionableUiComponent.OffsetType;
import bubolo.ui.gui.PositionableUiComponent.VOffsetFrom;
import bubolo.ui.gui.UiComponent.HoveredObjectInfo;

public class MainMenuScreen extends AbstractScreen {
	private final Color clearColor =  new Color(0.85f, 0.85f, 0.85f, 1);

	private ButtonGroup buttonGroup;
	private Label versionText;
	private final BuboloApplication app;

	private final Color backgroundDistortionColor = new Color(1, 1, 1, 0f);
	private final Texture backgroundTexture;

	public MainMenuScreen(BuboloApplication app) {
		this.app = app;

		this.backgroundTexture = new Texture(new FileHandle(Config.UiPath.resolve("main_menu_background_blurred.png").toFile()));

		addButtonGroup();
		addVersionText();
	}

	private void addButtonGroup() {
		var buttonGroupArgs = new ButtonGroup.Args(300, 50);
		buttonGroupArgs.selectOnHover = true;
		buttonGroupArgs.paddingBetweenButtons = 10;
		buttonGroupArgs.backgroundColor = new Color(0.5f, 0.5f, 0.5f, 0.75f);
		buttonGroupArgs.buttonBackgroundColor = new Color(1, 1, 1, 0.75f);

		var layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 55);
		buttonGroup = new ButtonGroup(layoutArgs, buttonGroupArgs);
		buttonGroup.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		buttonGroup.setVerticalOffset(0, OffsetType.ScreenUnits, VOffsetFrom.Center);
		buttonGroup.addButton("Single Player Game", this::onSinglePlayerButtonActivated);
		buttonGroup.addButton("Join Multiplayer Game", this::onJoinMultiplayerButtonActivated);
		buttonGroup.addButton("Host Multiplayer Game", this::onHostMultiplayerButtonActivated);
		buttonGroup.addButton("Settings", this::onSettingsButtonActivated);
		buttonGroup.addButton("Exit", button -> { Gdx.app.exit(); });

		root.add(buttonGroup);
	}

	private void addVersionText() {
		var layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		versionText = new Label(layoutArgs, Config.Version, Fonts.Arial16, Color.WHITE);
		versionText.setVerticalOffset(0.975f, OffsetType.Percent, VOffsetFrom.Top);
		versionText.setHorizontalOffset(5, OffsetType.ScreenUnits, HOffsetFrom.Left);
		versionText.recalculateLayout(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		root.add(versionText);
	}

	/**
	 * @param button unused.
	 */
	private void onSinglePlayerButtonActivated(Button button) {
		setInputEventsEnabled(false);
		app.setState(State.SinglePlayerSetup);
	}

	/**
	 * @param button unused.
	 */
	private void onJoinMultiplayerButtonActivated(Button button) {
		setInputEventsEnabled(false);
		app.setState(State.MultiplayerSetupClient);
	}

	/**
	 * @param button unused.
	 */
	private void onHostMultiplayerButtonActivated(Button button) {
		setInputEventsEnabled(false);
		app.setState(State.MultiplayerMapSelection);
	}

	/**
	 * @param button unused.
	 */
	private void onSettingsButtonActivated(Button button) {

	}

	@Override
	public Color clearColor() {
		return clearColor;
	}

	@Override
	protected void preDraw(Graphics graphics) {
		graphics.batch().begin();
		graphics.batch().draw(backgroundTexture, 0, 0, graphics.camera().viewportWidth, graphics.camera().viewportHeight);
		graphics.batch().end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		var shapeRenderer = graphics.shapeRenderer();
		shapeRenderer.setColor(backgroundDistortionColor);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.rect(0, 0, graphics.camera().viewportWidth, graphics.camera().viewportHeight);
		shapeRenderer.end();
	}

	@Override
	protected void postDraw(Graphics graphics) {
	}

	@Override
	public void onKeyDown(int keycode) {
		if (keycode == Keys.ESCAPE) {
			Gdx.app.exit();
		}
	}

	@Override
	protected void onMouseHoveredOverObject(HoveredObjectInfo hoveredObjectInfo) {
	}

	@Override
	protected void onDispose() {
		backgroundTexture.dispose();
	}
}
