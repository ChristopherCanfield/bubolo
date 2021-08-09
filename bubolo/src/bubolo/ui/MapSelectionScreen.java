package bubolo.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import bubolo.BuboloApplication;
import bubolo.Config;
import bubolo.GameApplication.State;
import bubolo.graphics.Fonts;
import bubolo.input.InputManager.Action;
import bubolo.map.InvalidMapException;
import bubolo.map.MapImporter;
import bubolo.map.MapImporter.MapInfo;
import bubolo.ui.gui.ButtonGroup;
import bubolo.ui.gui.Image;
import bubolo.ui.gui.Label;
import bubolo.ui.gui.LayoutArgs;
import bubolo.ui.gui.PositionableUiComponent;
import bubolo.ui.gui.PositionableUiComponent.HOffsetFrom;
import bubolo.ui.gui.PositionableUiComponent.HOffsetFromObjectSide;
import bubolo.ui.gui.PositionableUiComponent.OffsetType;
import bubolo.ui.gui.PositionableUiComponent.VOffsetFrom;
import bubolo.ui.gui.PositionableUiComponent.VOffsetFromObjectSide;
import bubolo.ui.gui.UiComponent.HoveredObjectInfo;
import bubolo.util.GameRuntimeException;

public class MapSelectionScreen extends AbstractScreen {
	private final Color clearColor = Color.WHITE;

	private final BuboloApplication app;
	private final State nextState;

	private final MapImporter mapImporter = new MapImporter();

	private Map<String, MapInfo> mapInfo = new HashMap<>();

	private ButtonGroup mapPathsGroup;
	private Image mapPreviewImage;
	private Label screenTitleLabel;
	private Label mapNameLabel;
	private Label mapAuthorLabel;
	private Label mapLastUpdatedLabel;
	private Label mapSizeLabel;
	private Label mapDescriptionLabel;

	private ButtonGroup okCancelButtons;

	private final String mapNameText = "Name: ";
	private final String authorNameText = "Author: ";
	private final String mapDescriptionText = "Description: ";
	private final String mapSizeText = "Size: ";
	private final String lastUpdatedText = "Last Updated: ";

	private static final String mapFileExtension = Config.MapFileExtension;

	private static final int secondRowTopOffset = 135;
	private static final int mapInfoLabelPadding = 10;

	private static final int minDescriptionRowSize = 450;
	private static final float targetDescriptionRowSizePct = 0.4f;

	private static final float targetPreviewImageWidthPct = 0.4f;
	private static final float targetPreviewImageHeightPct = targetPreviewImageWidthPct * 0.57f;

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
		addButtonRow();

		if (!paths.isEmpty()) {
			mapPathsGroup.selectButton(0);
			onSelectedMapChanged();
		}
	}

	private void addTitle() {
		var layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		screenTitleLabel = new Label(layoutArgs, "Map Selection", Fonts.UiTitleFont, Color.BLACK);
		screenTitleLabel.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		screenTitleLabel.setVerticalOffset(20, OffsetType.ScreenUnits, VOffsetFrom.Top);
		root.add(screenTitleLabel);
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
		mapPathsVGroupArgs.selectOnHover = true;

		var layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 10);
		mapPathsGroup = new ButtonGroup(layoutArgs, mapPathsVGroupArgs);
		mapPathsGroup.setHorizontalOffset(0.1f, OffsetType.Percent, HOffsetFrom.Left);
		mapPathsGroup.setVerticalOffset(secondRowTopOffset, OffsetType.ScreenUnits, VOffsetFrom.Top);

		mapPaths.forEach(path -> mapPathsGroup.addButton(path.getFileName().toString().replace(mapFileExtension, ""),
				button -> { onMapActivated(); }
		));

		root.add(mapPathsGroup);
	}

	private void addMapInfoUiComponents() {
		int windowWidth = Gdx.graphics.getWidth();
		int windowHeight = Gdx.graphics.getHeight();

		LayoutArgs mapPreviewArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), mapInfoLabelPadding);
		mapPreviewImage = new Image(mapPreviewArgs, null, (int) (targetPreviewImageWidthPct * windowWidth), (int) (targetPreviewImageHeightPct * windowHeight));
		mapPreviewImage.setHorizontalOffset(0.55f, OffsetType.Percent, HOffsetFrom.Left);
		mapPreviewImage.setVerticalOffset(secondRowTopOffset, OffsetType.ScreenUnits, VOffsetFrom.Top);
		root.add(mapPreviewImage);

		LayoutArgs mapNameArgs = new LayoutArgs(windowWidth, windowHeight, mapInfoLabelPadding);
		mapNameLabel = new Label(mapNameArgs, mapNameText);
		mapNameLabel.setVerticalOffset(mapPreviewImage, VOffsetFromObjectSide.Bottom, 0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		mapNameLabel.setHorizontalOffset(mapPreviewImage, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
		root.add(mapNameLabel);

		LayoutArgs mapAuthorArgs = new LayoutArgs(windowWidth, windowHeight, mapInfoLabelPadding);
		mapAuthorLabel = new Label(mapAuthorArgs, authorNameText);
		mapAuthorLabel.setVerticalOffset(mapNameLabel, VOffsetFromObjectSide.Bottom, 0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		mapAuthorLabel.setHorizontalOffset(mapNameLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
		root.add(mapAuthorLabel);

		LayoutArgs lastUpdatedArgs = new LayoutArgs(windowWidth, windowHeight, mapInfoLabelPadding);
		mapLastUpdatedLabel = new Label(lastUpdatedArgs, lastUpdatedText);
		mapLastUpdatedLabel.setVerticalOffset(mapAuthorLabel, VOffsetFromObjectSide.Bottom, 0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		mapLastUpdatedLabel.setHorizontalOffset(mapNameLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
		root.add(mapLastUpdatedLabel);

		LayoutArgs mapSizeArgs = new LayoutArgs(windowWidth, windowHeight, mapInfoLabelPadding);
		mapSizeLabel = new Label(mapSizeArgs, mapSizeText);
		mapSizeLabel.setVerticalOffset(mapLastUpdatedLabel, VOffsetFromObjectSide.Bottom, 0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		mapSizeLabel.setHorizontalOffset(mapNameLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
		root.add(mapSizeLabel);

		LayoutArgs mapDescriptionArgs = new LayoutArgs(windowWidth, windowHeight, mapInfoLabelPadding);
		mapDescriptionLabel = new Label(mapDescriptionArgs, mapDescriptionText, Fonts.UiGeneralTextFont, Color.BLACK, true, calculateDescriptionRowSize());
		mapDescriptionLabel.setVerticalOffset(mapSizeLabel, VOffsetFromObjectSide.Bottom, 0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		mapDescriptionLabel.setHorizontalOffset(mapNameLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
		root.add(mapDescriptionLabel);
	}

	private static int calculateDescriptionRowSize() {
		float sizeFromPct = Gdx.graphics.getWidth() * targetDescriptionRowSizePct;
		return (sizeFromPct > minDescriptionRowSize) ? (int) sizeFromPct : minDescriptionRowSize;
	}

	private void addButtonRow() {
		var layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 10);
		var buttonArgs = new ButtonGroup.Args(140, 35);
		buttonArgs.buttonListLayout = ButtonGroup.Layout.Horizontal;
		buttonArgs.paddingBetweenButtons = 10;
		buttonArgs.borderColor = ButtonGroup.Args.Transparent;
		buttonArgs.selectOnHover = true;

		okCancelButtons = new ButtonGroup(layoutArgs, buttonArgs);
		String okText = "OK";
		okCancelButtons.addButton(okText, button -> {
			if (mapPathsGroup.selectedButtonIndex() != PositionableUiComponent.NoIndex) {
				onMapActivated();
			}
		});
		okCancelButtons.addButton("Back", button -> {
			setInputEventsEnabled(false);
			app.setState(State.MainMenu);
		});

		okCancelButtons.setVerticalOffset(0.9f, OffsetType.Percent, VOffsetFrom.Top);
		okCancelButtons.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);

		root.add(okCancelButtons);
	}

	@Override
	public Color clearColor() {
		return clearColor;
	}

	@Override
	protected void onViewportResized(int newWidth, int newHeight) {
		mapPreviewImage.setSize((int) (targetPreviewImageWidthPct * newWidth), (int) (targetPreviewImageHeightPct * newHeight));
		mapPreviewImage.recalculateLayout(newWidth, newHeight);

		mapNameLabel.recalculateLayout(newWidth, newHeight);
		mapAuthorLabel.recalculateLayout(newWidth, newHeight);

		mapLastUpdatedLabel.recalculateLayout(newWidth, newHeight);
		mapSizeLabel.recalculateLayout(newWidth, newHeight);

		mapDescriptionLabel.recalculateLayout(newWidth, newHeight);
		mapDescriptionLabel.setMaxLineWidth(calculateDescriptionRowSize());
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
			setInputEventsEnabled(false);
			app.setState(nextState, Config.MapsPath.resolve(selectedMapFileName + mapFileExtension));
		}
	}

	@Override
	protected void onInputActionReceived(Action action) {
		if (action == Action.Cancel) {
			setInputEventsEnabled(false);
			app.setState(State.MainMenu);
		}

		if (action == Action.MenuUp || action == Action.MenuDown) {
			onSelectedMapChanged();
		}
	}

	@Override
	protected void onMouseHoveredOverObject(HoveredObjectInfo hoveredObjectInfo) {
		if (hoveredObjectInfo.component() == mapPathsGroup) {
			mapPathsGroup.selectButton(hoveredObjectInfo.hoveredItemIndex());
			onSelectedMapChanged();
		}
	}

	@Override
	public void onDispose() {
		for (var info : mapInfo.values()) {
			info.dispose();
		}
	}
}
