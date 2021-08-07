package bubolo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

import bubolo.ui.gui.Button;
import bubolo.ui.gui.ButtonGroup;
import bubolo.ui.gui.GuiGroup;
import bubolo.ui.gui.LayoutArgs;
import bubolo.ui.gui.PositionableUiComponent.HOffsetFrom;
import bubolo.ui.gui.PositionableUiComponent.OffsetType;
import bubolo.ui.gui.PositionableUiComponent.VOffsetFrom;

/**
 * The diplomacy screen is a pop-up window that overlays the game screen.
 *
 * @author Christopher D. Canfield
 */
class DiplomacyScreen extends GuiGroup {
	private final GuiGroup diplomacyScreenTop = new GuiGroup();
	private final GuiGroup diplomacyScreenRequestAlliance = new GuiGroup();
	private final GuiGroup diplomacyScreenEndAlliance = new GuiGroup();
	private final GuiGroup diplomacyScreenPendingRequests = new GuiGroup();

	private final GuiGroup[] screens = {
			diplomacyScreenTop,
			diplomacyScreenPendingRequests,
			diplomacyScreenRequestAlliance,
			diplomacyScreenEndAlliance
	};

	/**
	 * Constructs the diplomacy screens.
	 */
	DiplomacyScreen() {
		createDiplomacyScreenTop();
		createDiplomacyScreenRequestAlliance();
		createDiplomacyScreenEndAlliance();
		createDiplomacyScreenPendingRequests();

		hideAll();
	}

	private void createDiplomacyScreenTop() {
		var buttonGroupArgs = new ButtonGroup.Args(300, 50);
		buttonGroupArgs.selectOnHover = true;
		buttonGroupArgs.paddingBetweenButtons = 10;
		buttonGroupArgs.backgroundColor = new Color(0.5f, 0.5f, 0.5f, 0.75f);
		buttonGroupArgs.buttonBackgroundColor = new Color(1, 1, 1, 0.75f);

		var layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 55);
		var buttonGroup = new ButtonGroup(layoutArgs, buttonGroupArgs);
		buttonGroup.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		buttonGroup.setVerticalOffset(0, OffsetType.ScreenUnits, VOffsetFrom.Center);
		buttonGroup.addButton("Request Alliance", this::goToAllianceScreen);
		buttonGroup.addButton("End Alliance", this::goToEndAllianceScreen);
		buttonGroup.addButton("Respond to Alliance Request", this::goToRespondToAllianceScreen);
		buttonGroup.addButton("Back to Game", this::hideAll);

		add(diplomacyScreenTop);
		diplomacyScreenTop.add(buttonGroup);
	}

	private void goToAllianceScreen(Button button) {
		hideAll();
		diplomacyScreenRequestAlliance.setVisible(true);
	}

	private void goToEndAllianceScreen(Button button) {
		hideAll();
		diplomacyScreenEndAlliance.setVisible(true);
	}

	private void goToRespondToAllianceScreen(Button button) {
		hideAll();
		diplomacyScreenPendingRequests.setVisible(true);
	}

	private void createDiplomacyScreenRequestAlliance() {
		add(diplomacyScreenRequestAlliance);
	}

	private void createDiplomacyScreenEndAlliance() {
		add(diplomacyScreenEndAlliance);
	}

	private void createDiplomacyScreenPendingRequests() {
		add(diplomacyScreenPendingRequests);
	}

	public void show() {
		hideAll();
		diplomacyScreenTop.setVisible(true);
	}

	private void hideAll(Button unused) {
		hideAll();
	}

	private void hideAll() {
		for (GuiGroup screen : screens) {
			screen.setVisible(false);
		}
	}

	@Override
	public void onKeyDown(int keycode) {
		if (isVisible()) {
			if (keycode == Keys.ESCAPE) {
				hideAll();
			} else {
				super.onKeyDown(keycode);
			}
		}
	}
}
