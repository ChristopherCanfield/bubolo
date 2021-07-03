/**
 *
 */

package bubolo.ui;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;

import bubolo.GameApplication;
import bubolo.GameApplication.State;
import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.ui.gui.ButtonGroup;
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
public class MultiplayerSetupScreen implements Screen, InputProcessor {
	public enum PlayerType {
		Server,
		Client
	}

	private final Color clearColor = Color.WHITE;

	private final GameApplication app;
	private final boolean isClient;

	// For server only.
	private InetAddress ipAddress;

	private final List<UiComponent> uiComponents = new ArrayList<>();
	private final List<TextBox> textBoxes = new ArrayList<>();
	private final List<ButtonGroup> buttonGroups = new ArrayList<>();

	private Label screenTitleLabel;

	private TextBox playerNameTextBox;

	private Line sectionDivider;
	private TextBox serverIpAddressTextBox;
	private Label ipAddressLabel;

	private Label orSelectServerLabel;
	private ButtonGroup availableGamesList;

	private ButtonGroup okCancelButtons;

	// These variables enable the screen to be updated with a message before the connection attempt
	// is made. This is useful because the connection attempt may take a few seconds, and the screen
	// will appear frozen during that time otherwise.
	private boolean connectToServer;
	private int ticksUntilConnect;

//	private float horizontalOffsetFromLeftPct = 0.385f;

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

		recalculateLayout(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Gdx.input.setInputProcessor(this);
	}

	private static TextBox.Args defaultTextBoxArgs() {
		var textBoxArgs = new TextBox.Args();
		textBoxArgs.labelWidth = 150;
		textBoxArgs.textWidth = 200;
		return textBoxArgs;
	}

	private void addComponent(UiComponent component) {
		uiComponents.add(component);
		if (component instanceof TextBox textBox) {
			textBoxes.add(textBox);
		} else if (component instanceof ButtonGroup buttonGroup) {
			buttonGroups.add(buttonGroup);
		}
	}

	private void recalculateLayout(int screenWidth, int screenHeight) {
		screenTitleLabel.recalculateLayout(screenWidth, screenHeight);
		playerNameTextBox.recalculateLayout(screenWidth, screenHeight);

		if (isClient) {
			sectionDivider.recalculateLayout(screenWidth, screenHeight);
			serverIpAddressTextBox.recalculateLayout(screenWidth, screenHeight);
			orSelectServerLabel.recalculateLayout(screenWidth, screenHeight);
			availableGamesList.recalculateLayout(screenWidth, screenHeight);
			okCancelButtons.recalculateLayout(screenWidth, screenHeight);
		} else {
			ipAddressLabel.recalculateLayout(screenWidth, screenHeight);
			okCancelButtons.recalculateLayout(screenWidth, screenHeight);
		}
	}

	private void addScreenTitleRow() {
		String title = (isClient) ? "Join Multiplayer Game" : "Multiplayer Game Server Setup";
		LayoutArgs args = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		screenTitleLabel = new Label(args, title, Fonts.UiTitleFont, Color.BLACK);
		screenTitleLabel.setVerticalOffset(20, OffsetType.ScreenUnits, VOffsetFrom.Top);
		screenTitleLabel.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		addComponent(screenTitleLabel);
	}

	private void addPlayerNameRow() {
		LayoutArgs args = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		var textBoxArgs = defaultTextBoxArgs();
		textBoxArgs.labelText = "Name:";
		playerNameTextBox = new TextBox(args, textBoxArgs);
		playerNameTextBox.setVerticalOffset(135, OffsetType.ScreenUnits, VOffsetFrom.Top);
		playerNameTextBox.setHorizontalOffset(0, OffsetType.Percent, HOffsetFrom.Center);
		addComponent(playerNameTextBox);
	}

	private void addIpAddressRow() {
		if (!isClient) {
			try {
				var ipAddressInfo = getIpAddresses();
				ipAddress = ipAddressInfo.firstIpAddress();

				LayoutArgs args = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
				ipAddressLabel = new Label(args, "IP Address:            " + ipAddressInfo.ipAddresses());
				ipAddressLabel.setVerticalOffset(playerNameTextBox, VOffsetFromObjectSide.Bottom, 25, OffsetType.ScreenUnits, VOffsetFrom.Top);
				ipAddressLabel.setHorizontalOffset(playerNameTextBox, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
				addComponent(ipAddressLabel);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
	}

	record IpAddressInfo(InetAddress firstIpAddress, String ipAddresses) {
	}

	private static IpAddressInfo getIpAddresses() throws SocketException {
		InetAddress firstIpAddress = null;
		StringBuilder ipAddresses = new StringBuilder();
		var networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			var networkInterface = networkInterfaces.nextElement();
			// Filter out loopback and VirtualBox addresses.
			if (!networkInterface.isLoopback()
					&& !networkInterface.getDisplayName().contains("VirtualBox")) {
				var addresses = networkInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					var address = addresses.nextElement();
					if (address instanceof Inet4Address) {
						if (!ipAddresses.isEmpty()) {
							ipAddresses.append(", ");
						} else {
							firstIpAddress = address;
						}
						ipAddresses.append(address.getHostAddress());
					}
				}
			}
		}
		return new IpAddressInfo(firstIpAddress, ipAddresses.toString());
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
			addComponent(sectionDivider);

			LayoutArgs args = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
			var textBoxArgs = defaultTextBoxArgs();
			textBoxArgs.labelText = "Host IP Address:";
			serverIpAddressTextBox = new TextBox(args, textBoxArgs);
			serverIpAddressTextBox.setVerticalOffset(sectionDivider, VOffsetFromObjectSide.Bottom, 50, OffsetType.ScreenUnits, VOffsetFrom.Top);
			serverIpAddressTextBox.setHorizontalOffset(playerNameTextBox, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
			addComponent(serverIpAddressTextBox);

			orSelectServerLabel = new Label(layoutArgs, "Or, select server:");
			orSelectServerLabel.setVerticalOffset(serverIpAddressTextBox, VOffsetFromObjectSide.Bottom, 25, OffsetType.ScreenUnits, VOffsetFrom.Top);
			orSelectServerLabel.setHorizontalOffset(serverIpAddressTextBox, HOffsetFromObjectSide.Left, 0, OffsetType.ScreenUnits, HOffsetFrom.Left);
			addComponent(orSelectServerLabel);

			availableGamesList = new ButtonGroup(layoutArgs, availableGamesListArgs);
			availableGamesList.addButton("Game 1 (Canfield Island)");
			availableGamesList.addButton("Plenty Fun (Old Bolo Island)");
			availableGamesList.setVerticalOffset(orSelectServerLabel, VOffsetFromObjectSide.Bottom, 50, OffsetType.ScreenUnits, VOffsetFrom.Top);
			availableGamesList.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
			addComponent(availableGamesList);
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
			System.out.println("Back clicked");
			goBackOneScreen();
		});

		if (isClient) {
			okCancelButtons.setVerticalOffset(availableGamesList, VOffsetFromObjectSide.Bottom, 50, OffsetType.ScreenUnits, VOffsetFrom.Top);
			okCancelButtons.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		} else {
			okCancelButtons.setVerticalOffset(ipAddressLabel, VOffsetFromObjectSide.Bottom, 50, OffsetType.ScreenUnits, VOffsetFrom.Top);
			okCancelButtons.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Center);
		}

		addComponent(okCancelButtons);
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
//		statusLabel1.setText("Connecting...");
//		statusLabel2.setText("");
//		connectToServer = true;
//		ticksUntilConnect = 1;
	}

	private void goBackOneScreen() {
		if (isClient) {
			app.setState(State.MainMenu);
		} else {
			app.setState(State.MultiplayerMapSelection);
		}
	}

	@Override
	public void draw(Graphics graphics) {
		for (var component : uiComponents) {
			component.draw(graphics);
		}

		if (connectToServer) {
			if (ticksUntilConnect == 0) {
				connectToServer = false;

				InetAddress ipAddress;
//				try {
////					ipAddress = InetAddress.getByName(ipAddressField.getText());
//
//					final Network network = NetworkSystem.getInstance();
////					network.connect(ipAddress, playerNameField.getText());
//				} catch (UnknownHostException | NetworkException e) {
////					statusLabel1.setText("");
////					statusLabel2.setText("Unable to connect: " + e.getMessage());
//					return;
//				}

				app.setState(State.MultiplayerLobby);
			} else {
				--ticksUntilConnect;
			}
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		textBoxes.forEach(box -> box.onKeyDown(keycode));

		return false;
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
	public boolean keyTyped(char character) {
		textBoxes.forEach(box -> box.onKeyTyped(character));

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		for (TextBox textBox : textBoxes) {
			textBox.setSelected(false);
			textBox.onMouseClicked(screenX, screenY);
		}
		for (ButtonGroup buttonGroup : buttonGroups) {
			buttonGroup.onMouseClicked(screenX, screenY);
		}

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		for (ButtonGroup buttonGroup : buttonGroups) {
			int buttonIndex = buttonGroup.onMouseMoved(screenX, screenY);
			if (buttonIndex != -1 && buttonGroup == okCancelButtons) {
				buttonGroup.selectButton(buttonIndex);
			}
		}

		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}

	@Override
	public Color clearColor() {
		return clearColor;
	}

	@Override
	public void viewportResized(int newWidth, int newHeight) {
		recalculateLayout(newWidth, newHeight);
	}

	@Override
	public void dispose() {
		if (Gdx.input.getInputProcessor() == this) {
			Gdx.input.setInputProcessor(null);
		}
	}
}
