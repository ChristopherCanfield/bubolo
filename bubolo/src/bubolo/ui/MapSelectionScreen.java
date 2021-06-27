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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import bubolo.BuboloApplication;
import bubolo.Config;
import bubolo.GameApplication.State;
import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.map.InvalidMapException;
import bubolo.map.MapImporter;
import bubolo.map.MapImporter.MapInfo;
import bubolo.ui.gui.Label;
import bubolo.ui.gui.LayoutArgs;
import bubolo.ui.gui.UiComponent;
import bubolo.ui.gui.UiComponent.HOffsetFrom;
import bubolo.ui.gui.UiComponent.OffsetType;
import bubolo.ui.gui.UiComponent.VOffsetFrom;
import bubolo.ui.gui.VButtonGroup;
import bubolo.util.GameRuntimeException;

public class MapSelectionScreen implements Screen, InputProcessor {
	private final Color clearColor = Color.WHITE;

	private final BuboloApplication app;

	private final Color backgroundDistortionColor = new Color(1, 1, 1, 0f);

	private final MapImporter mapImporter = new MapImporter();

	private Map<String, MapInfo> mapInfo = new HashMap<>();

	private final List<UiComponent> uiComponents = new ArrayList<>();

	private VButtonGroup mapPathsGroup;
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

	private static final int secondRowTopOffset = 200;
	private static final BitmapFont primaryFont = Fonts.Arial20;
	private static final int mapInfoLabelPadding = 10;
	private static final int minDescriptionRowSize = 450;
	private static final float targetDescriptionRowSizePct = 0.4f;

	public MapSelectionScreen(BuboloApplication app) {
		this.app = app;

		addTitle();
		List<Path> paths;
		try {
			paths = mapImporter.loadMapFilePaths();
		} catch (IOException e) {
			throw new GameRuntimeException("Unable to load map file names.\n\n" + e.toString());
		}
		addMapPaths(paths);

		importMapInfo(paths);
		addMapInfoLabels();

		Gdx.input.setInputProcessor(this);
	}

	private void addTitle() {
		var layoutArgs = new LayoutArgs(0, 0, Config.TargetWindowWidth, Config.TargetWindowWidth, 0);
		screenTitleLabel = new Label(layoutArgs, Fonts.Arial32, Color.BLACK, "Map Selection");
		screenTitleLabel.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		screenTitleLabel.setVerticalOffset(20, OffsetType.ScreenUnits, VOffsetFrom.Top);
		screenTitleLabel.recalculateLayout(0, 0, Config.TargetWindowWidth, Config.TargetWindowHeight);
		uiComponents.add(screenTitleLabel);
	}

	private void importMapInfo(List<Path> mapPaths) {
		try {
			for (Path path : mapPaths) {
				var info = mapImporter.loadMapInfo(path);
				mapInfo.put(path.getFileName().toString().replace(".json", ""), info);
			}
		} catch (IOException e) {
			throw new InvalidMapException("Unable to load map information.\n\n" + e);
		}
	}

	private void addMapPaths(List<Path> mapPaths) {
		var mapPathsVGroupArgs = new VButtonGroup.Args(500, 30);
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

		var layoutArgs = new LayoutArgs(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 10);

		mapPathsGroup = new VButtonGroup(layoutArgs, mapPathsVGroupArgs);
		mapPathsGroup.setHorizontalOffset(0.1f, OffsetType.Percent, HOffsetFrom.Left);
		mapPathsGroup.setVerticalOffset(secondRowTopOffset, OffsetType.ScreenUnits, VOffsetFrom.Top);

		mapPaths.forEach(path -> mapPathsGroup.addButton(path.getFileName().toString().replace(".json", "")));

		uiComponents.add(mapPathsGroup);
	}

	private void addMapInfoLabels() {
		LayoutArgs mapNameArgs = new LayoutArgs(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), mapInfoLabelPadding);
		mapNameLabel = new Label(mapNameArgs, primaryFont, Color.BLACK, mapNameText);
		mapNameLabel.setHorizontalOffset(0.55f, OffsetType.Percent, HOffsetFrom.Left);
		mapNameLabel.setVerticalOffset(secondRowTopOffset, OffsetType.ScreenUnits, VOffsetFrom.Top);
		uiComponents.add(mapNameLabel);

		LayoutArgs mapAuthorArgs = new LayoutArgs(0, (int) mapNameLabel.bottom(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), mapInfoLabelPadding);
		mapAuthorLabel = new Label(mapAuthorArgs, primaryFont, Color.BLACK, authorNameText);
		mapAuthorLabel.setHorizontalOffset(mapNameLabel.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
		mapAuthorLabel.setVerticalOffset(0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		uiComponents.add(mapAuthorLabel);

		LayoutArgs lastUpdatedArgs = new LayoutArgs(0, (int) mapAuthorLabel.bottom(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), mapInfoLabelPadding);
		mapLastUpdatedLabel = new Label(lastUpdatedArgs, primaryFont, Color.BLACK, lastUpdatedText);
		mapLastUpdatedLabel.setHorizontalOffset(mapNameLabel.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
		mapLastUpdatedLabel.setVerticalOffset(0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		uiComponents.add(mapLastUpdatedLabel);

		LayoutArgs mapSizeArgs = new LayoutArgs(0, (int) mapLastUpdatedLabel.bottom(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), mapInfoLabelPadding);
		mapSizeLabel = new Label(mapSizeArgs, primaryFont, Color.BLACK, mapSizeText);
		mapSizeLabel.setHorizontalOffset(mapNameLabel.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
		mapSizeLabel.setVerticalOffset(0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		uiComponents.add(mapSizeLabel);

		LayoutArgs mapDescriptionArgs = new LayoutArgs(0, (int) mapSizeLabel.bottom(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), mapInfoLabelPadding);
		mapDescriptionLabel = new Label(mapDescriptionArgs, primaryFont, Color.BLACK, mapDescriptionText, true, calculateDescriptionRowSize());
		mapDescriptionLabel.setHorizontalOffset(mapNameLabel.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
		mapDescriptionLabel.setVerticalOffset(0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		uiComponents.add(mapDescriptionLabel);
	}

	private int calculateDescriptionRowSize() {
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
			// Rendering fonts seems to disable blending.
			Gdx.gl.glEnable(GL20.GL_BLEND);
			uiComponent.draw(graphics);
		}

		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	@Override
	public void onViewportResized(int newWidth, int newHeight) {
		screenTitleLabel.recalculateLayout(0, 0, newWidth, newHeight);
		mapPathsGroup.recalculateLayout(0, 0, newWidth, newHeight);

		mapNameLabel.recalculateLayout(0, 0, newWidth, newHeight);
		mapAuthorLabel.setHorizontalOffset(mapNameLabel.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
		mapLastUpdatedLabel.setHorizontalOffset(mapNameLabel.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
		mapSizeLabel.setHorizontalOffset(mapNameLabel.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
		mapDescriptionLabel.setHorizontalOffset(mapNameLabel.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
		mapDescriptionLabel.setMaxRowSize(calculateDescriptionRowSize());
	}

	private void onSelectedMapChanged() {
		var selectedMapFileName = mapPathsGroup.selectedButtonText();
		if (selectedMapFileName != null) {
			var selectedMapInfo = mapInfo.get(selectedMapFileName);
			mapNameLabel.setText(mapNameText + selectedMapInfo.mapName());
			mapAuthorLabel.setText(authorNameText + selectedMapInfo.author());
			mapLastUpdatedLabel.setText(lastUpdatedText + selectedMapInfo.lastUpdated());
			mapSizeLabel.setText(mapSizeText + selectedMapInfo.tileColumns() + " x " + selectedMapInfo.tileRows());
			mapDescriptionLabel.setText(mapDescriptionText + selectedMapInfo.description());
		}
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.UP || keycode == Keys.W || keycode == Keys.NUMPAD_8) {
			mapPathsGroup.selectPrevious();
		} else if (keycode == Keys.DOWN || keycode == Keys.S || keycode == Keys.NUMPAD_5 || keycode == Keys.NUMPAD_2) {
			mapPathsGroup.selectNext();
		} else if (keycode == Keys.SPACE || keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER) {
			mapPathsGroup.activateSelectedButton();
		} else if (keycode == Keys.ESCAPE) {
			app.setState(State.MainMenu);
		}

		onSelectedMapChanged();

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// @TODO (cdc 2021-06-27): Load the selected map.
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		int buttonIndex = mapPathsGroup.onMouseMoved(screenX, screenY);
		mapPathsGroup.selectButton(buttonIndex);
		onSelectedMapChanged();
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
