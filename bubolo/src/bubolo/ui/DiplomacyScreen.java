package bubolo.ui;

import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

import bubolo.Messenger.MessageObserver;
import bubolo.Systems;
import bubolo.graphics.Fonts;
import bubolo.net.command.RequestAlliance;
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
class DiplomacyScreen extends GuiGroup implements MessageObserver {
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

	private SelectBox allyWithSelectBox;
	private SelectBox pendingAllianceRequests;
	private SelectBox alliancesSelectBox;

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

		Systems.messenger().addObserver(this);
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
		allyWithSelectBox = new SelectBox(layoutArgs, selectBoxArgs);
		allyWithSelectBox.setVerticalOffset(headerLabel, VOffsetFromObjectSide.Bottom, 80, OffsetType.ScreenUnits, VOffsetFrom.Top);
		allyWithSelectBox.setHorizontalOffset(headerLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Center);
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
		buttonGroup.addButton("Send Request", this::buttonPressed_SendAllianceRequest);
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
		alliancesSelectBox = new SelectBox(layoutArgs, selectBoxArgs);
		alliancesSelectBox.setVerticalOffset(headerLabel, VOffsetFromObjectSide.Bottom, 80, OffsetType.ScreenUnits, VOffsetFrom.Top);
		alliancesSelectBox.setHorizontalOffset(headerLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		diplomacyScreenEndAlliance.add(alliancesSelectBox);

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
		buttonGroup.addButton("End Alliance", this::buttonPressed_EndAlliance);
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
		pendingAllianceRequests = new SelectBox(layoutArgs, selectBoxArgs);
		pendingAllianceRequests.setVerticalOffset(headerLabel, VOffsetFromObjectSide.Bottom, 80, OffsetType.ScreenUnits, VOffsetFrom.Top);
		pendingAllianceRequests.setHorizontalOffset(headerLabel, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Center);
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
		buttonGroup.addButton("Accept Request", this::buttonPressed_AcceptAllianceRequest);
		buttonGroup.addButton("Reject Request", this::buttonPressed_RejectAllianceRequest);
		buttonGroup.addButton("Back", this::goToDiplomacyTopScreen);
		diplomacyScreenPendingRequests.add(buttonGroup);

		add(diplomacyScreenPendingRequests);
	}


	/* Button callbacks */

	private void goToDiplomacyTopScreen(Button button) {
		hideSubscreens();
		diplomacyScreenTop.setVisible(true);
	}

	// The player id that corresponds to the currently selected player name.
	private UUID selectedPlayerId;

	private void goToAllianceScreen(Button button) {
		hideSubscreens();

		allyWithSelectBox.removeAllItems();
		var enemies = player.getEnemyPlayers();
		for (Player enemy : enemies) {
			allyWithSelectBox.addItem(enemy.name(), sb -> {
				sb.setTextColor(enemy.color().color);
				selectedPlayerId = enemy.id();
			});
		}

		diplomacyScreenRequestAlliance.setVisible(true);
	}

	private void goToEndAllianceScreen(Button button) {
		hideSubscreens();

		alliancesSelectBox.removeAllItems();
		var allies = player.getAlliedPlayers();
		for (Player ally : allies) {
			alliancesSelectBox.addItem(ally.name(), sb -> {
				sb.setTextColor(ally.color().color);
				selectedPlayerId = ally.id();
			});
		}

		diplomacyScreenEndAlliance.setVisible(true);
	}

	private void goToRespondToAllianceScreen(Button button) {
		hideSubscreens();

		pendingAllianceRequests.removeAllItems();
		var pendingRequests = player.getPendingAllianceRequests();
		for (Player pendingRequest : pendingRequests) {
			pendingAllianceRequests.addItem(pendingRequest.name(), sb -> {
				sb.setTextColor(pendingRequest.color().color);
				selectedPlayerId = pendingRequest.id();
			});
		}

		diplomacyScreenPendingRequests.setVisible(true);
	}

	private void buttonPressed_SendAllianceRequest(Button button) {
		Systems.network().send(new RequestAlliance(selectedPlayerId, player.id(), player.name()));
		Systems.messenger().notifyAllianceRequestSent(allyWithSelectBox.selectedItem());
	}

	private void buttonPressed_EndAlliance(Button button) {

	}

	private void buttonPressed_AcceptAllianceRequest(Button button) {

	}

	private void buttonPressed_RejectAllianceRequest(Button button) {

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

	@Override
	public void dispose() {
		super.dispose();
		Systems.messenger().removeObserver(this);
	}


	/* Message callbacks. Not all of these are used. */

	@Override
	public void messageObjectUnderAttack(String message) {
	}

	@Override
	public void messageObjectCaptured(String message, boolean thisPlayerLostObject, boolean thisPlayerCapturedObject) {
	}

	@Override
	public void messagePlayerDied(String message, boolean thisPlayerDied) {
	}

	@Override
	public void messagePlayerDisconnected(String message) {
	}

	@Override
	public void messageAllianceRequestReceived(String message, Player thisPlayer, UUID requesterId, String requesterName) {
	}

	@Override
	public void messageAllianceRequestSent(String message) {
	}

	/* End message callbacks. */
}
