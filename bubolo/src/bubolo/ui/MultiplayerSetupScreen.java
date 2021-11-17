/**
 *
 */

package bubolo.ui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import bubolo.GameApplication;
import bubolo.GameApplication.State;
import bubolo.PlayerInfo;
import bubolo.Systems;
import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.graphics.TeamColor;
import bubolo.input.InputManager.Action;
import bubolo.net.Network;
import bubolo.net.NetworkException;
import bubolo.net.ServerAddressListener;
import bubolo.net.ServerAddressMessage;
import bubolo.ui.gui.ButtonGroup;
import bubolo.ui.gui.Label;
import bubolo.ui.gui.LayoutArgs;
import bubolo.ui.gui.Line;
import bubolo.ui.gui.PositionableUiComponent.HOffsetFrom;
import bubolo.ui.gui.PositionableUiComponent.HOffsetFromObjectSide;
import bubolo.ui.gui.PositionableUiComponent.OffsetType;
import bubolo.ui.gui.PositionableUiComponent.VOffsetFrom;
import bubolo.ui.gui.PositionableUiComponent.VOffsetFromObjectSide;
import bubolo.ui.gui.SelectBox;
import bubolo.ui.gui.TextBox;
import bubolo.ui.gui.UiComponent;
import bubolo.ui.gui.UiComponent.HoveredObjectInfo;

/**
 * The join game screen, which allows the user to enter a name and ip address.
 *
 * @author Christopher D. Canfield
 */
public class MultiplayerSetupScreen extends AbstractScreen implements ServerAddressListener.Observer {
	public enum PlayerType {
		Server,
		Client
	}

	private final Color clearColor = Color.WHITE;

	private final GameApplication app;
	private final boolean isClient;

	// For server only.
	private InetAddress ipAddress;

	private Label screenTitleLabel;

	private TextBox playerNameTextBox;
	private SelectBox colorSelectBox;

	private Line sectionDivider;
	private TextBox serverIpAddressTextBox;
	private Label ipAddressLabel;

	private Label orSelectServerLabel;
	private ButtonGroup availableServersList;
	private final List<ServerAddressMessage> availableServers = new ArrayList<>();

	private ButtonGroup okCancelButtons;

	// For client only.
	private ServerAddressListener serverAddressListener;

	// These variables enable the screen to be updated with a message before the connection attempt
	// is made. This is useful because the connection attempt may take a few seconds, and the screen
	// will appear frozen during that time otherwise.
	private boolean connectToServer;
	private InetAddress serverIpAddress;
	private int ticksUntilConnect;

	/**
	 * Constructs the network game lobby.
	 *
	 * @param app reference to the Game Application.
	 * @param playerType whether this is a server or client.
	 */
	public MultiplayerSetupScreen(GameApplication app, PlayerType playerType) {
		this.app = app;
		this.isClient = (playerType == PlayerType.Client);

		addScreenTitleRow();
		addPlayerInfoRows();
		addIpAddressRow();
		addAvailableGames();
		addButtonRow();
//		addStatusLabels();

		if (isClient) {
			serverAddressListener = new ServerAddressListener(this);
			var networkInterfaces = Network.getNetworkInterfaces();
			// @TODO (cdc 2021-07-04): This must be very rare, but handle it properly regardless.
			assert !networkInterfaces.isEmpty();
			serverAddressListener.start(networkInterfaces.get(0));
		}
	}

	private static TextBox.Args defaultTextBoxArgs() {
		var textBoxArgs = new TextBox.Args();
		textBoxArgs.labelWidth = 150;
		textBoxArgs.textWidth = 200;
		return textBoxArgs;
	}

	private void addScreenTitleRow() {
		String title = (isClient) ? "Join Multiplayer Game" : "Multiplayer Game Server Setup";
		LayoutArgs args = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		screenTitleLabel = new Label(args, title, Fonts.UiTitleFont, Color.BLACK);
		screenTitleLabel.setVerticalOffset(20, OffsetType.ScreenUnits, VOffsetFrom.Top);
		screenTitleLabel.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		root.add(screenTitleLabel);
	}

	private void addPlayerInfoRows() {
		LayoutArgs layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		var textBoxArgs = defaultTextBoxArgs();
		textBoxArgs.labelText = "Name:";
		playerNameTextBox = new TextBox(layoutArgs, textBoxArgs);
		playerNameTextBox.setVerticalOffset(screenTitleLabel, VOffsetFromObjectSide.Bottom, 100, OffsetType.ScreenUnits, VOffsetFrom.Top);
		playerNameTextBox.setHorizontalOffset(0, OffsetType.Percent, HOffsetFrom.Center);
		root.add(playerNameTextBox);

		var selectBoxArgs = new SelectBox.Args();
		selectBoxArgs.textWidth = textBoxArgs.textWidth;
		selectBoxArgs.labelText = "Color:";
		selectBoxArgs.labelWidth = textBoxArgs.labelWidth;
		colorSelectBox = new SelectBox(layoutArgs, selectBoxArgs);
		colorSelectBox.setVerticalOffset(playerNameTextBox, VOffsetFromObjectSide.Bottom, 20, OffsetType.ScreenUnits, VOffsetFrom.Top);
		colorSelectBox.setHorizontalOffset(playerNameTextBox, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
		for (var playerColor : TeamColor.values()) {
			if (playerColor.selectableByPlayers) {
				colorSelectBox.addItem(playerColor.toString(), selectBox -> selectBox.setTextColor(playerColor.color));
			}
		}
		// Default to a random color, to encourage players to not all select the same color.
		colorSelectBox.setSelectedIndex(MathUtils.random.nextInt(colorSelectBox.itemCount()));
		root.add(colorSelectBox);
	}

	private void addIpAddressRow() {
		if (!isClient) {
			var ipAddressInfo = Network.getIpAddresses();
			ipAddress = ipAddressInfo.firstIpAddress();

			LayoutArgs args = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
			ipAddressLabel = new Label(args, "IP Address:            " + ipAddressInfo.ipAddresses());
			ipAddressLabel.setVerticalOffset(colorSelectBox, VOffsetFromObjectSide.Bottom, 25, OffsetType.ScreenUnits, VOffsetFrom.Top);
			ipAddressLabel.setHorizontalOffset(playerNameTextBox, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
			root.add(ipAddressLabel);
		}
	}

	private void addAvailableGames() {
		if (isClient) {
			var layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
			var availableGamesListArgs = new ButtonGroup.Args(500, 30);
			availableGamesListArgs.paddingBetweenButtons = 5;
			Color transparent = new Color(0, 0, 0, 0);
			availableGamesListArgs.backgroundColor = Color.WHITE;
			availableGamesListArgs.buttonBackgroundColor = transparent;
			availableGamesListArgs.borderColor = transparent;
			availableGamesListArgs.buttonBorderColor = transparent;
			availableGamesListArgs.buttonTextColor = Color.DARK_GRAY;
			availableGamesListArgs.buttonSelectedBorderColor = Color.BLACK;
			availableGamesListArgs.buttonSelectedTextColor = Color.BLACK;
			availableGamesListArgs.buttonSelectedBackgroundColor = transparent;
			availableGamesListArgs.buttonHoverBackgroundColor = transparent;
			availableGamesListArgs.buttonHoverBorderColor = transparent;

			sectionDivider = new Line(layoutArgs, Color.GRAY, (int) playerNameTextBox.width(), 3);
			sectionDivider.setVerticalOffset(colorSelectBox, VOffsetFromObjectSide.Bottom, 50, OffsetType.ScreenUnits, VOffsetFrom.Top);
			sectionDivider.setHorizontalOffset(playerNameTextBox, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
			root.add(sectionDivider);

			LayoutArgs args = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
			var textBoxArgs = defaultTextBoxArgs();
			textBoxArgs.labelText = "Host IP Address:";
			serverIpAddressTextBox = new TextBox(args, textBoxArgs);
			serverIpAddressTextBox.setVerticalOffset(sectionDivider, VOffsetFromObjectSide.Bottom, 50, OffsetType.ScreenUnits, VOffsetFrom.Top);
			serverIpAddressTextBox.setHorizontalOffset(playerNameTextBox, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
			root.add(serverIpAddressTextBox);

			orSelectServerLabel = new Label(layoutArgs, "Or, select server:");
			orSelectServerLabel.setVerticalOffset(serverIpAddressTextBox, VOffsetFromObjectSide.Bottom, 25, OffsetType.ScreenUnits, VOffsetFrom.Top);
			orSelectServerLabel.setHorizontalOffset(serverIpAddressTextBox, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
			root.add(orSelectServerLabel);

			availableServersList = new ButtonGroup(layoutArgs, availableGamesListArgs);
			availableServersList.setVerticalOffset(orSelectServerLabel, VOffsetFromObjectSide.Bottom, 50, OffsetType.ScreenUnits, VOffsetFrom.Top);
			availableServersList.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
			root.add(availableServersList);
		}
	}

	private void addButtonRow() {
		var layoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 10);
		var buttonArgs = new ButtonGroup.Args(140, 35);
		buttonArgs.buttonListLayout = ButtonGroup.Layout.Horizontal;
		buttonArgs.paddingBetweenButtons = 10;
		buttonArgs.borderColor = ButtonGroup.Args.Transparent;
		buttonArgs.selectOnHover = true;

		okCancelButtons = new ButtonGroup(layoutArgs, buttonArgs);
		String okText = isClient ? "Join" : "Create Game";
		okCancelButtons.addButton(okText, button -> {
			if (isClient) {
				connectToServer();
			} else {
				startServer();
			}
		});
		okCancelButtons.addButton("Back", button -> {
			goBackOneScreen();
		});

		okCancelButtons.setVerticalOffset(0.9f, OffsetType.Percent, VOffsetFrom.Top);
		okCancelButtons.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);

		root.add(okCancelButtons);
	}

	private boolean startingServer = false;

	private void startServer() {
		if (!startingServer && !playerNameTextBox.isEmpty()) {
			startingServer = true;
			setInputEventsEnabled(false);

			final Network network = Systems.network();
			network.startServer(playerNameTextBox.text());

			var playerInfo = new PlayerInfo(playerNameTextBox.text(), TeamColor.valueOf(colorSelectBox.selectedItem()), ipAddress);
			app.setState(State.MultiplayerLobby, playerInfo);
		}
	}

	private void connectToServer() {
		if (!connectToServer && !playerNameTextBox.isEmpty()) {
			int selectedServerIndex = availableServersList.selectedButtonIndex();
			if (selectedServerIndex != UiComponent.NoIndex) {
				serverIpAddress = availableServers.get(selectedServerIndex).serverAddress();
			} else if (!serverIpAddressTextBox.isEmpty()) {
				try {
					serverIpAddress = InetAddress.getByName(serverIpAddressTextBox.text());
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}

			if (serverIpAddress != null) {
				connectToServer = true;
				ticksUntilConnect = 1;
			}
		}
	}

	private void goBackOneScreen() {
		setInputEventsEnabled(false);
		if (isClient) {
			app.setState(State.MainMenu);
		} else {
			app.setState(State.MultiplayerMapSelection);
		}
	}

	@Override
	public void postDraw(Graphics graphics) {
		if (connectToServer) {
			if (ticksUntilConnect == 0) {
				connectToServer = false;

				try {
					final Network network = Systems.network();
					network.connect(serverIpAddress, playerNameTextBox.text());
				} catch (NetworkException e) {
//					statusLabel1.setText("");
//					statusLabel2.setText("Unable to connect: " + e.getMessage());
					return;
				}

				setInputEventsEnabled(false);
				var playerInfo = new PlayerInfo(playerNameTextBox.text(), TeamColor.valueOf(colorSelectBox.selectedItem()), null);
				app.setState(State.MultiplayerLobby, playerInfo);
			} else {
				--ticksUntilConnect;
			}
		}
	}

	@Override
	protected void onInputActionReceived(Action action) {
		if (action == Action.Activate) {
			if (isClient) {
				connectToServer();
			} else {
				startServer();
			}
		} else if (action == Action.Cancel) {
			goBackOneScreen();
		}
	}

	@Override
	protected void onMouseHoveredOverObject(HoveredObjectInfo hoveredObjectInfo) {
		if (hoveredObjectInfo.component() == okCancelButtons) {
			okCancelButtons.selectButton(hoveredObjectInfo.hoveredItemIndex());
		} else {
			okCancelButtons.lostFocus();
		}
	}

	@Override
	public Color clearColor() {
		return clearColor;
	}

	@Override
	public void onServerAddressFound(ServerAddressMessage message) {
		if (!availableServers.contains(message)) {
			availableServers.add(message);
			availableServersList.addButton(message.serverName() + " (" + message.mapName() + ")");
			root.recalculateLayout();
		}
	}

	@Override
	protected void onDispose() {
		if (serverAddressListener != null) {
			serverAddressListener.shutDown();
		}
	}
}
