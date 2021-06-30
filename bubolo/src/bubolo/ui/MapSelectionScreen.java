package bubolo.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import bubolo.BuboloApplication;
import bubolo.Config;
import bubolo.GameApplication.State;
import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.map.InvalidMapException;
import bubolo.map.MapImporter;
import bubolo.map.MapImporter.MapInfo;
import bubolo.ui.gui.ButtonGroup;
import bubolo.ui.gui.Image;
import bubolo.ui.gui.Label;
import bubolo.ui.gui.LayoutArgs;
import bubolo.ui.gui.UiComponent;
import bubolo.ui.gui.UiComponent.HOffsetFrom;
import bubolo.ui.gui.UiComponent.HOffsetFromObjectSide;
import bubolo.ui.gui.UiComponent.OffsetType;
import bubolo.ui.gui.UiComponent.VOffsetFrom;
import bubolo.ui.gui.UiComponent.VOffsetFromObjectSide;
import bubolo.util.GameRuntimeException;

public class MapSelectionScreen implements Screen, InputProcessor {
	private final Color clearColor = Color.WHITE;

	private final BuboloApplication app;
	private final State nextState;

	private final Color backgroundDistortionColor = new Color(1, 1, 1, 0f);

	private final MapImporter mapImporter = new MapImporter();

	private Map<String, MapInfo> mapInfo = new HashMap<>();

	private final List<UiComponent> uiComponents = new ArrayList<>();

	private ButtonGroup mapPathsGroup;
	private Image mapPreviewImage;
	private Label screenTitleLabel;
	private Label mapNameLabel;
	private Label mapAuthorLabel;
	private Label mapLastUpdatedLabel;
	private Label mapSizeLabel;
	private Label mapDescriptionLabel;

	private final String mapNameText = "Name: ";
	private final String authorNameText = "Author: ";
	private final String mapDescriptionText = "Description: ";
	private final String mapSizeText = "Size: ";
	private final String lastUpdatedText = "Last Updated: ";

	private static final String mapFileExtension = ".json";

	private static final int secondRowTopOffset = 135;
	private static final int mapInfoLabelPadding = 10;

	private static final int minDescriptionRowSize = 450;
	private static final float targetDescriptionRowSizePct = 0.4f;

	private static final float targetPreviewImageWidthPct = 0.4f;
	private static final float targetPreviewImageHeightPct = targetPreviewImageWidthPct * 0.57f;

	private enum TabGroup {
		MapNames,
		OkCancel
	}

	private TabGroup activeGroup = TabGroup.MapNames;

	/**
	 * @param app reference to the game application.
	 * @param nextState the next app state. Used to enable this screen to be used with both single and multiplayer games.
	 */
	public MapSelectionScreen(BuboloApplication app, State nextState) {
		this.app = app;
		this.nextState = nextState;

		addTitle();
		List<Path> paths;
		try {
			paths = mapImporter.loadMapFilePaths();
		} catch (IOException e) {
			throw new GameRuntimeException("Unable to load map file names.\n\n" + e.toString());
		}
		addMapPaths(paths);

		importMapInfo(paths);
		addMapInfoUiComponents();

		if (!paths.isEmpty()) {
			mapPathsGroup.selectButton(0);
			onSelectedMapChanged();
		}

		Gdx.input.setInputProcessor(this);
	}

	private void addTitle() {
		var layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		screenTitleLabel = new Label(layoutArgs, "Map Selection", Fonts.UiTitleFont, Color.BLACK);
		screenTitleLabel.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		screenTitleLabel.setVerticalOffset(20, OffsetType.ScreenUnits, VOffsetFrom.Top);
		uiComponents.add(screenTitleLabel);
	}

	private void importMapInfo(List<Path> mapPaths) {
		try {
			for (Path path : mapPaths) {
				var info = mapImporter.loadMapInfo(path);
				mapInfo.put(path.getFileName().toString().replace(mapFileExtension, ""), info);
			}
		} catch (IOException e) {
			throw new InvalidMapException("Unable to load map information.\n\n" + e);
		}
	}

	private void addMapPaths(List<Path> mapPaths) {
		var mapPathsVGroupArgs = new ButtonGroup.Args(500, 30);
		mapPathsVGroupArgs.paddingBetweenButtons = 5;
		Color transparent = new Color(0, 0, 0, 0);
		mapPathsVGroupArgs.backgroundColor = Color.WHITE;
		mapPathsVGroupArgs.buttonBackgroundColor = transparent;
		mapPathsVGroupArgs.borderColor = transparent;
		mapPathsVGroupArgs.buttonBorderColor = transparent;
		mapPathsVGroupArgs.buttonTextColor = Color.DARK_GRAY;
		mapPathsVGroupArgs.buttonSelectedBorderColor = Color.BLACK;
		mapPathsVGroupArgs.buttonSelectedTextColor = Color.BLACK;
		mapPathsVGroupArgs.buttonSelectedBackgroundColor = transparent;
		mapPathsVGroupArgs.buttonHoverBackgroundColor = transparent;
		mapPathsVGroupArgs.buttonHoverBorderColor = transparent;

		var layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 10);
		mapPathsGroup = new ButtonGroup(layoutArgs, mapPathsVGroupArgs);
		mapPathsGroup.setHorizontalOffset(0.1f, OffsetType.Percent, HOffsetFrom.Left);
		mapPathsGroup.setVerticalOffset(secondRowTopOffset, OffsetType.ScreenUnits, VOffsetFrom.Top);

		mapPaths.forEach(path -> mapPathsGroup.addButton(path.getFileName().toString().replace(mapFileExtension, "")));

		uiComponents.add(mapPathsGroup);
	}

	private void addMapInfoUiComponents() {
		int windowWidth = Gdx.graphics.getWidth();
		int windowHeight = Gdx.graphics.getHeight();

		LayoutArgs mapPreviewArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), mapInfoLabelPadding);
		mapPreviewImage = new Image(mapPreviewArgs, null, (int) (targetPreviewImageWidthPct * windowWidth), (int) (targetPreviewImageHeightPct * windowHeight));
		mapPreviewImage.setHorizontalOffset(0.55f, OffsetType.Percent, HOffsetFrom.Left);
		mapPreviewImage.setVerticalOffset(secondRowTopOffset, OffsetType.ScreenUnits, VOffsetFrom.Top);
		uiComponents.add(mapPreviewImage);

		LayoutArgs mapNameArgs = new LayoutArgs(windowWidth, windowHeight, mapInfoLabelPadding);
		mapNameLabel = new Label(mapNameArgs, mapNameText);
		mapNameLabel.setVerticalOffset(mapPreviewImage, VOffsetFromObjectSide.Bottom, 0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		mapNameLabel.setHorizontalOffset(mapPreviewImage, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
		uiComponents.add(mapNameLabel);

		LayoutArgs mapAuthorArgs = new LayoutArgs(windowWidth, windowHeight, mapInfoLabelPadding);
		mapAuthorLabel = new Label(mapAuthorArgs, authorNameText);
		mapAuthorLabel.setVerticalOffset(mapNameLabel, VOffsetFromObjectSide.Bottom, 0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		mapAuthorLabel.setHorizontalOffset(mapNameLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
		uiComponents.add(mapAuthorLabel);

		LayoutArgs lastUpdatedArgs = new LayoutArgs(windowWidth, windowHeight, mapInfoLabelPadding);
		mapLastUpdatedLabel = new Label(lastUpdatedArgs, lastUpdatedText);
		mapLastUpdatedLabel.setVerticalOffset(mapAuthorLabel, VOffsetFromObjectSide.Bottom, 0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		mapLastUpdatedLabel.setHorizontalOffset(mapNameLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
		uiComponents.add(mapLastUpdatedLabel);

		LayoutArgs mapSizeArgs = new LayoutArgs(windowWidth, windowHeight, mapInfoLabelPadding);
		mapSizeLabel = new Label(mapSizeArgs, mapSizeText);
		mapSizeLabel.setVerticalOffset(mapLastUpdatedLabel, VOffsetFromObjectSide.Bottom, 0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		mapSizeLabel.setHorizontalOffset(mapNameLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
		uiComponents.add(mapSizeLabel);

		LayoutArgs mapDescriptionArgs = new LayoutArgs(windowWidth, windowHeight, mapInfoLabelPadding);
		mapDescriptionLabel = new Label(mapDescriptionArgs, mapDescriptionText, Fonts.UiGeneralTextFont, Color.BLACK, true, calculateDescriptionRowSize());
		mapDescriptionLabel.setVerticalOffset(mapSizeLabel, VOffsetFromObjectSide.Bottom, 0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		mapDescriptionLabel.setHorizontalOffset(mapNameLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
		uiComponents.add(mapDescriptionLabel);
	}

	private static int calculateDescriptionRowSize() {
		float sizeFromPct = Gdx.graphics.getWidth() * targetDescriptionRowSizePct;
		return (sizeFromPct > minDescriptionRowSize) ? (int) sizeFromPct : minDescriptionRowSize;
	}

	@Override
	public Color clearColor() {
		return clearColor;
	}

	@Override
	public void draw(Graphics graphics) {
		Gdx.gl.glEnable(GL20.GL_BLEND);

		var shapeRenderer = graphics.shapeRenderer();
		shapeRenderer.setColor(backgroundDistortionColor);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.rect(0, 0, graphics.camera().viewportWidth, graphics.camera().viewportHeight);
		shapeRenderer.end();

		for (var uiComponent : uiComponents) {
			uiComponent.draw(graphics);
		}

		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	@Override
	public void onViewportResized(int newWidth, int newHeight) {
		screenTitleLabel.recalculateLayout(newWidth, newHeight);
		mapPathsGroup.recalculateLayout(newWidth, newHeight);

		mapPreviewImage.setSize((int) (targetPreviewImageWidthPct * newWidth), (int) (targetPreviewImageHeightPct * newHeight));
		mapPreviewImage.recalculateLayout(newWidth, newHeight);

		mapNameLabel.setHorizontalOffset(mapPreviewImage.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
		mapNameLabel.recalculateLayout(newWidth, newHeight);

		mapAuthorLabel.setHorizontalOffset(mapNameLabel.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
		mapAuthorLabel.recalculateLayout(newWidth, newHeight);

		mapLastUpdatedLabel.setHorizontalOffset(mapNameLabel.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
		mapLastUpdatedLabel.recalculateLayout(newWidth, newHeight);

		mapSizeLabel.setHorizontalOffset(mapNameLabel.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
		mapSizeLabel.recalculateLayout(newWidth, newHeight);

		mapDescriptionLabel.recalculateLayout(newWidth, newHeight);
		mapDescriptionLabel.setHorizontalOffset(mapNameLabel.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
		mapDescriptionLabel.setMaxRowSize(calculateDescriptionRowSize());
	}

	/**
	 * Call this when the map selection changes.
	 */
	private void onSelectedMapChanged() {
		var selectedMapFileName = mapPathsGroup.selectedButtonText();
		if (selectedMapFileName != null) {
			var selectedMapInfo = mapInfo.get(selectedMapFileName);
			mapPreviewImage.setTexture(selectedMapInfo.previewTexture());
			mapNameLabel.setText(mapNameText + selectedMapInfo.mapName());
			mapAuthorLabel.setText(authorNameText + selectedMapInfo.author());
			mapLastUpdatedLabel.setText(lastUpdatedText + selectedMapInfo.lastUpdated());
			mapSizeLabel.setText(mapSizeText + selectedMapInfo.tileColumns() + " x " + selectedMapInfo.tileRows());
			mapDescriptionLabel.setText(mapDescriptionText + selectedMapInfo.description());
		}
	}

	/**
	 * Call this when the user completes their map selection, such as by pressing the OK button.
	 */
	private void onMapActivated() {
		var selectedMapFileName = mapPathsGroup.selectedButtonText();
		if (selectedMapFileName != null) {
			app.setState(nextState, Config.MapsPath.resolve(selectedMapFileName + mapFileExtension));
		}
	}

	private void switchTabGroup() {
		activeGroup = (activeGroup == TabGroup.MapNames) ? TabGroup.OkCancel : TabGroup.MapNames;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
			case Keys.UP, Keys.W, Keys.NUMPAD_8 -> mapPathsGroup.selectPrevious();
			case Keys.DOWN, Keys.S, Keys.NUMPAD_5, Keys.NUMPAD_2 -> mapPathsGroup.selectNext();
			case Keys.SPACE, Keys.ENTER, Keys.NUMPAD_ENTER -> {
				mapPathsGroup.activateSelectedButton();
				onMapActivated();
			}
			case Keys.TAB -> switchTabGroup();
			case Keys.ESCAPE -> app.setState(State.MainMenu);
		}

		onSelectedMapChanged();

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		int buttonIndex = mapPathsGroup.onMouseClicked(screenX, screenY);
		if (buttonIndex != -1) {
			mapPathsGroup.selectButton(buttonIndex);
			onSelectedMapChanged();
			onMapActivated();
		}

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		int buttonIndex = mapPathsGroup.onMouseMoved(screenX, screenY);
		if (buttonIndex != -1) {
			mapPathsGroup.selectButton(buttonIndex);
			onSelectedMapChanged();
		}
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
