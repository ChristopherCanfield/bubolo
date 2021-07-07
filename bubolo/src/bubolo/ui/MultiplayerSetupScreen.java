/**
 *
 */

package bubolo.ui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

import bubolo.GameApplication;
import bubolo.GameApplication.State;
import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.net.Network;
import bubolo.net.NetworkException;
import bubolo.net.NetworkSystem;
import bubolo.net.ServerAddressListener;
import bubolo.net.ServerAddressMessage;
import bubolo.ui.gui.ButtonGroup;
import bubolo.ui.gui.GuiGroup.HoveredObjectInfo;
import bubolo.ui.gui.Label;
import bubolo.ui.gui.LayoutArgs;
import bubolo.ui.gui.Line;
import bubolo.ui.gui.TextBox;
import bubolo.ui.gui.UiComponent;
import bubolo.ui.gui.UiComponent.HOffsetFrom;
import bubolo.ui.gui.UiComponent.HOffsetFromObjectSide;
import bubolo.ui.gui.UiComponent.OffsetType;
import bubolo.ui.gui.UiComponent.VOffsetFrom;
import bubolo.ui.gui.UiComponent.VOffsetFromObjectSide;

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
		addPlayerNameRow();
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

		Gdx.input.setInputProcessor(this);
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

	private void addPlayerNameRow() {
		LayoutArgs args = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		var textBoxArgs = defaultTextBoxArgs();
		textBoxArgs.labelText = "Name:";
		playerNameTextBox = new TextBox(args, textBoxArgs);
		playerNameTextBox.setVerticalOffset(screenTitleLabel, VOffsetFromObjectSide.Bottom, 100, OffsetType.ScreenUnits, VOffsetFrom.Top);
		playerNameTextBox.setHorizontalOffset(0, OffsetType.Percent, HOffsetFrom.Center);
		root.add(playerNameTextBox);
	}

	private void addIpAddressRow() {
		if (!isClient) {
			var ipAddressInfo = Network.getIpAddresses();
			ipAddress = ipAddressInfo.firstIpAddress();

			LayoutArgs args = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
			ipAddressLabel = new Label(args, "IP Address:            " + ipAddressInfo.ipAddresses());
			ipAddressLabel.setVerticalOffset(playerNameTextBox, VOffsetFromObjectSide.Bottom, 25, OffsetType.ScreenUnits, VOffsetFrom.Top);
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
			sectionDivider.setVerticalOffset(playerNameTextBox, VOffsetFromObjectSide.Bottom, 50, OffsetType.ScreenUnits, VOffsetFrom.Top);
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
//		if (isClient) {
//			okCancelButtons.setVerticalOffset(availableServersList, VOffsetFromObjectSide.Bottom, 100, OffsetType.ScreenUnits, VOffsetFrom.Top);
//		} else {
//			okCancelButtons.setVerticalOffset(ipAddressLabel, VOffsetFromObjectSide.Bottom, 100, OffsetType.ScreenUnits, VOffsetFrom.Top);
//		}
		okCancelButtons.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);

		root.add(okCancelButtons);
	}

//	private void addStatusLabels() {
//		VerticalGroup statusGroup = new VerticalGroup();
//		statusGroup.align(Align.center);
//		statusGroup.padTop(50);
//		statusGroup.padLeft(leftPadding);
//
//		statusLabel1 = new Label("Test", skin);
//		statusGroup.addActor(statusLabel1);
//
//		statusLabel2 = new Label("Test2", skin);
//		statusGroup.addActor(statusLabel2);
//
//		root.addActor(statusGroup);
//	}
//
//	private boolean textFieldsPopulated() {
//		return !playerNameField.getText().isEmpty() && !(isClient && ipAddressField.getText().isEmpty());
//	}

	private void startServer() {
		final Network network = NetworkSystem.getInstance();
		network.startServer(playerNameTextBox.text());
		app.setState(State.MultiplayerLobby, ipAddress);
	}

	private void connectToServer() {
		if (!playerNameTextBox.isEmpty()) {
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
					final Network network = NetworkSystem.getInstance();
					network.connect(serverIpAddress, playerNameTextBox.text());
				} catch (NetworkException e) {
//					statusLabel1.setText("");
//					statusLabel2.setText("Unable to connect: " + e.getMessage());
					return;
				}

				app.setState(State.MultiplayerLobby);
			} else {
				--ticksUntilConnect;
			}
		}
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER) {
//			if (textFieldsPopulated()) {
//				if (isClient) {
//					connectToServer();
//				} else {
//					startServer();
//				}
//			}
		} else if (keycode == Keys.ESCAPE) {
			goBackOneScreen();
		}

		return false;
	}

	@Override
	protected void onMouseHoveredOverObject(HoveredObjectInfo hoveredObjectInfo) {
		if (hoveredObjectInfo.component() == okCancelButtons) {
			okCancelButtons.selectButton(hoveredObjectInfo.hoveredItemIndex());
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
