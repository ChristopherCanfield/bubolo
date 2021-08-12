package bubolo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

import bubolo.Systems;
import bubolo.graphics.Fonts;
import bubolo.ui.gui.Button;
import bubolo.ui.gui.ButtonGroup;
import bubolo.ui.gui.ButtonGroup.Layout;
import bubolo.ui.gui.GuiGroup;
import bubolo.ui.gui.Label;
import bubolo.ui.gui.LayoutArgs;
import bubolo.ui.gui.PositionableUiComponent.HOffsetFrom;
import bubolo.ui.gui.PositionableUiComponent.HOffsetFromObjectSide;
import bubolo.ui.gui.PositionableUiComponent.OffsetType;
import bubolo.ui.gui.PositionableUiComponent.VOffsetFrom;
import bubolo.ui.gui.PositionableUiComponent.VOffsetFromObjectSide;
import bubolo.ui.gui.Rectangle;
import bubolo.ui.gui.SelectBox;
import bubolo.world.Player;

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

	private final Player player;

	/**
	 * Constructs the diplomacy screens.
	 *
	 * @param localPlayer reference to the local player. Used for alliance functionality.
	 */
	DiplomacyScreen(Player localPlayer) {
		this.player = localPlayer;

		createDiplomacyScreenTop();
		createDiplomacyScreenRequestAlliance();
		createDiplomacyScreenEndAlliance();
		createDiplomacyScreenPendingRequests();

		hide();
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
		buttonGroup.setVerticalOffset(0.2f, OffsetType.Percent, VOffsetFrom.Top);
		buttonGroup.addButton("Request Alliance", this::goToAllianceScreen);
		buttonGroup.addButton("End Alliance", this::goToEndAllianceScreen);
		buttonGroup.addButton("Respond to Alliance Request", this::goToRespondToAllianceScreen);
		buttonGroup.addButton("Back to Game", this::hide);

		add(diplomacyScreenTop);
		diplomacyScreenTop.add(buttonGroup);
	}

	private void createDiplomacyScreenRequestAlliance() {
		var layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		Rectangle background = new Rectangle(layoutArgs, new Color(0.6f, 0.6f, 0.6f, 0.85f), Color.BLACK, 550, 350);
		background.setVerticalOffset(0.2f, OffsetType.Percent, VOffsetFrom.Top);
		background.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		diplomacyScreenRequestAlliance.add(background);

		var headerLabel = new Label(layoutArgs, "Send Alliance Request", Fonts.UiTitleFont, Color.BLACK);
		headerLabel.setVerticalOffset(background, VOffsetFromObjectSide.Top, 20, OffsetType.ScreenUnits, VOffsetFrom.Top);
		headerLabel.setHorizontalOffset(background, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		diplomacyScreenRequestAlliance.add(headerLabel);

		var selectBoxArgs = new SelectBox.Args();
		selectBoxArgs.textWidth = 300;
		selectBoxArgs.labelText = "Player:";
		selectBoxArgs.labelWidth = 100;
		var allyWithSelectBox = new SelectBox(layoutArgs, selectBoxArgs);
		allyWithSelectBox.setVerticalOffset(headerLabel, VOffsetFromObjectSide.Bottom, 80, OffsetType.ScreenUnits, VOffsetFrom.Top);
		allyWithSelectBox.setHorizontalOffset(headerLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		// @TODO (cdc 2021-08-07): Add other players.
		allyWithSelectBox.addItem("Player 2");
		allyWithSelectBox.addItem("Test Person");
		diplomacyScreenRequestAlliance.add(allyWithSelectBox);

		var buttonGroupArgs = new ButtonGroup.Args(200, 50);
		buttonGroupArgs.selectOnHover = true;
		buttonGroupArgs.paddingBetweenButtons = 10;
		buttonGroupArgs.buttonBackgroundColor = new Color(1, 1, 1, 0.75f);
		buttonGroupArgs.borderColor = ButtonGroup.Args.Transparent;
		buttonGroupArgs.buttonListLayout = Layout.Horizontal;

		var buttonGroupLayoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 55);
		var buttonGroup = new ButtonGroup(buttonGroupLayoutArgs, buttonGroupArgs);
		buttonGroup.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		buttonGroup.setVerticalOffset(background, VOffsetFromObjectSide.Bottom, -140, OffsetType.ScreenUnits, VOffsetFrom.Top);
		buttonGroup.addButton("Send Request");
		buttonGroup.addButton("Back", this::goToDiplomacyTopScreen);
		diplomacyScreenRequestAlliance.add(buttonGroup);

		add(diplomacyScreenRequestAlliance);
	}

	private void createDiplomacyScreenEndAlliance() {
		var layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		Rectangle background = new Rectangle(layoutArgs, new Color(0.6f, 0.6f, 0.6f, 0.85f), Color.BLACK, 550, 350);
		background.setVerticalOffset(0.2f, OffsetType.Percent, VOffsetFrom.Top);
		background.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		diplomacyScreenEndAlliance.add(background);

		var headerLabel = new Label(layoutArgs, "End Alliance", Fonts.UiTitleFont, Color.BLACK);
		headerLabel.setVerticalOffset(background, VOffsetFromObjectSide.Top, 20, OffsetType.ScreenUnits, VOffsetFrom.Top);
		headerLabel.setHorizontalOffset(background, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		diplomacyScreenEndAlliance.add(headerLabel);

		var selectBoxArgs = new SelectBox.Args();
		selectBoxArgs.textWidth = 300;
		selectBoxArgs.labelText = "Ally:";
		selectBoxArgs.labelWidth = 100;
		var pendingAllianceRequests = new SelectBox(layoutArgs, selectBoxArgs);
		pendingAllianceRequests.setVerticalOffset(headerLabel, VOffsetFromObjectSide.Bottom, 80, OffsetType.ScreenUnits, VOffsetFrom.Top);
		pendingAllianceRequests.setHorizontalOffset(headerLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		// @TODO (cdc 2021-08-07): Add alliances.
		pendingAllianceRequests.addItem("Player 2");
		pendingAllianceRequests.addItem("Test Person");
		diplomacyScreenEndAlliance.add(pendingAllianceRequests);

		var buttonGroupArgs = new ButtonGroup.Args(200, 50);
		buttonGroupArgs.selectOnHover = true;
		buttonGroupArgs.paddingBetweenButtons = 10;
		buttonGroupArgs.buttonBackgroundColor = new Color(1, 1, 1, 0.75f);
		buttonGroupArgs.borderColor = ButtonGroup.Args.Transparent;
		buttonGroupArgs.buttonListLayout = Layout.Horizontal;

		var buttonGroupLayoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 55);
		var buttonGroup = new ButtonGroup(buttonGroupLayoutArgs, buttonGroupArgs);
		buttonGroup.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		buttonGroup.setVerticalOffset(background, VOffsetFromObjectSide.Bottom, -140, OffsetType.ScreenUnits, VOffsetFrom.Top);
		buttonGroup.addButton("End Alliance");
		buttonGroup.addButton("Back", this::goToDiplomacyTopScreen);
		diplomacyScreenEndAlliance.add(buttonGroup);

		add(diplomacyScreenEndAlliance);
	}

	private void createDiplomacyScreenPendingRequests() {
		var layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		Rectangle background = new Rectangle(layoutArgs, new Color(0.6f, 0.6f, 0.6f, 0.85f), Color.BLACK, 650, 350);
		background.setVerticalOffset(0.2f, OffsetType.Percent, VOffsetFrom.Top);
		background.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		diplomacyScreenPendingRequests.add(background);

		var headerLabel = new Label(layoutArgs, "Accept/Reject Alliance Request", Fonts.UiTitleFont, Color.BLACK);
		headerLabel.setVerticalOffset(background, VOffsetFromObjectSide.Top, 20, OffsetType.ScreenUnits, VOffsetFrom.Top);
		headerLabel.setHorizontalOffset(background, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		diplomacyScreenPendingRequests.add(headerLabel);

		var selectBoxArgs = new SelectBox.Args();
		selectBoxArgs.textWidth = 300;
		selectBoxArgs.labelText = "Pending Request:";
		selectBoxArgs.labelWidth = 175;
		var pendingAllianceRequests = new SelectBox(layoutArgs, selectBoxArgs);
		pendingAllianceRequests.setVerticalOffset(headerLabel, VOffsetFromObjectSide.Bottom, 80, OffsetType.ScreenUnits, VOffsetFrom.Top);
		pendingAllianceRequests.setHorizontalOffset(headerLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		// @TODO (cdc 2021-08-07): Add pending requests.
		pendingAllianceRequests.addItem("Player 2");
		pendingAllianceRequests.addItem("Test Person");
		diplomacyScreenPendingRequests.add(pendingAllianceRequests);

		var buttonGroupArgs = new ButtonGroup.Args(200, 50);
		buttonGroupArgs.selectOnHover = true;
		buttonGroupArgs.paddingBetweenButtons = 10;
		buttonGroupArgs.buttonBackgroundColor = new Color(1, 1, 1, 0.75f);
		buttonGroupArgs.borderColor = ButtonGroup.Args.Transparent;
		buttonGroupArgs.buttonListLayout = Layout.Horizontal;

		var buttonGroupLayoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 55);
		var buttonGroup = new ButtonGroup(buttonGroupLayoutArgs, buttonGroupArgs);
		buttonGroup.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		buttonGroup.setVerticalOffset(background, VOffsetFromObjectSide.Bottom, -140, OffsetType.ScreenUnits, VOffsetFrom.Top);
		buttonGroup.addButton("Accept Request");
		buttonGroup.addButton("Reject Request");
		buttonGroup.addButton("Back", this::goToDiplomacyTopScreen);
		diplomacyScreenPendingRequests.add(buttonGroup);

		add(diplomacyScreenPendingRequests);
	}


	/* Button callbacks */

	private void goToDiplomacyTopScreen(Button button) {
		hideSubscreens();
		diplomacyScreenTop.setVisible(true);
	}

	private void goToAllianceScreen(Button button) {
		hideSubscreens();
		diplomacyScreenRequestAlliance.setVisible(true);
	}

	private void goToEndAllianceScreen(Button button) {
		hideSubscreens();
		diplomacyScreenEndAlliance.setVisible(true);
	}

	private void goToRespondToAllianceScreen(Button button) {
		hideSubscreens();
		diplomacyScreenPendingRequests.setVisible(true);
	}

	/* End button callbacks */


	public void show() {
		hideSubscreens();
		setVisible(true);
		diplomacyScreenTop.setVisible(true);
		Systems.input().disableGameActions();
	}

	private void hide(Button unused) {
		hide();
	}

	private void hide() {
		setVisible(false);
		hideSubscreens();
		Systems.input().enableGameActions();
	}

	private void hideSubscreens() {
		for (GuiGroup screen : screens) {
			screen.setVisible(false);
		}
	}

	@Override
	public void onKeyDown(int keycode) {
		if (isVisible()) {
			if (keycode == Keys.ESCAPE) {
				hide();
			} else {
				super.onKeyDown(keycode);
			}
		}
	}
}
