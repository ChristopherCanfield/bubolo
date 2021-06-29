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
import bubolo.ui.gui.Label;
import bubolo.ui.gui.LayoutArgs;
import bubolo.ui.gui.TextBox;
import bubolo.ui.gui.UiComponent;
import bubolo.ui.gui.UiComponent.HOffsetFrom;
import bubolo.ui.gui.UiComponent.OffsetType;
import bubolo.ui.gui.UiComponent.VOffsetFrom;

/**
 * The join game screen, which allows the user to enter a name and ip address.
 *
 * @author Christopher D. Canfield
 */
public class MultiplayerSetupScreen2 implements Screen, InputProcessor {
	public enum PlayerType {
		Server,
		Client
	}

	private final Color clearColor = Color.WHITE;

	private final GameApplication app;
	private final boolean isClient;

	private final List<UiComponent> uiComponents = new ArrayList<>();
	private final List<TextBox> textBoxes = new ArrayList<>();

	private TextBox playerNameTextBox;
	private TextBox serverIpAddressTextBox;
	private Label ipAddressLabel;

	// These variables enable the screen to be updated with a message before the connection attempt
	// is made. This is useful because the connection attempt may take a few seconds, and the screen
	// will appear frozen during that time otherwise.
	private boolean connectToServer;
	private int ticksUntilConnect;

	private float horizontalOffsetFromLeftPct = 0.375f;

	/**
	 * Constructs the network game lobby.
	 *
	 * @param app reference to the Game Application.
	 * @param playerType whether this is a server or client.
	 */
	public MultiplayerSetupScreen2(GameApplication app, PlayerType playerType) {
		this.app = app;
		this.isClient = (playerType == PlayerType.Client);

		addPlayerNameRow();
		addIpAddressRow();
//		addAvailableGames();
//		addButtonRow();
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
		}
	}

	private void recalculateLayout(int screenWidth, int screenHeight) {
		playerNameTextBox.setVerticalOffset(100, OffsetType.ScreenUnits, VOffsetFrom.Top);
		playerNameTextBox.setHorizontalOffset(horizontalOffsetFromLeftPct, OffsetType.Percent, HOffsetFrom.Left);
		playerNameTextBox.recalculateLayout(0, 0, screenWidth, screenHeight);

		if (isClient) {
			serverIpAddressTextBox.setVerticalOffset(playerNameTextBox.bottom() + 15, OffsetType.ScreenUnits, VOffsetFrom.Top);
			serverIpAddressTextBox.setHorizontalOffset(playerNameTextBox.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
			serverIpAddressTextBox.recalculateLayout(0, 0, screenWidth, screenHeight);
		} else {
			ipAddressLabel.setVerticalOffset(playerNameTextBox.bottom() + 15, OffsetType.ScreenUnits, VOffsetFrom.Top);
			ipAddressLabel.setHorizontalOffset(playerNameTextBox.left(), OffsetType.ScreenUnits, HOffsetFrom.Left);
			ipAddressLabel.recalculateLayout(0, 0, screenWidth, screenHeight);
		}
	}

	private void addPlayerNameRow() {
		LayoutArgs args = new LayoutArgs(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		var textBoxArgs = defaultTextBoxArgs();
		textBoxArgs.labelText = "Name:";
		playerNameTextBox = new TextBox(args, textBoxArgs);
		addComponent(playerNameTextBox);
	}

	private void addIpAddressRow() {
		if (isClient) {
			LayoutArgs args = new LayoutArgs(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
			var textBoxArgs = defaultTextBoxArgs();
			textBoxArgs.labelText = "Host IP Address:";
			serverIpAddressTextBox = new TextBox(args, textBoxArgs);
			addComponent(serverIpAddressTextBox);

		} else {
			try {
				String ipAddresses = getIpAddresses();

				LayoutArgs args = new LayoutArgs(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
				ipAddressLabel = new Label(args, Fonts.DefaultUiFont, Color.BLACK, "IP Address:       " + ipAddresses);
				uiComponents.add(ipAddressLabel);
				addComponent(ipAddressLabel);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getIpAddresses() throws SocketException {
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
						}
						ipAddresses.append(address.getHostAddress());
					}
				}
			}
		}
		return ipAddresses.toString();
	}

//	private void addAvailableGames() {
//		if (isClient) {
//			VerticalGroup layoutGroup = new VerticalGroup();
//			layoutGroup.padTop(30);
//			layoutGroup.padLeft(leftPadding);
//			layoutGroup.space(10);
//
//			Label availableGamesLabel = new Label("Available Games:", skin);
//			layoutGroup.addActor(availableGamesLabel);
//
//			availableGamesList = new List<>(skin);
//			availableGamesList.setItems("Hello", "Game 2");
//			availableGamesList.setSelectedIndex(-1);
//
//			ScrollPane scrollpane = new ScrollPane(availableGamesList, skin);
//			scrollpane.setWidth(200);
//			layoutGroup.addActor(scrollpane);
//			root.addActor(layoutGroup);
//		}
//	}
//
//	private void addButtonRow() {
//		HorizontalGroup buttonGroup = new HorizontalGroup();
//		buttonGroup.align(Align.center);
//		buttonGroup.padLeft(leftPadding);
//		buttonGroup.padTop(25);
//		buttonGroup.space(10);
//
//		TextButton okButton = new TextButton("OK", skin);
//		okButton.pad(5);
//		okButton.padLeft(30);
//		okButton.padRight(30);
//		buttonGroup.addActor(okButton);
//
//		okButton.addListener(new ClickListener() {
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				if (textFieldsPopulated()) {
//					if (isClient) {
//						connectToServer();
//					} else {
//						startServer();
//					}
//				}
//			}
//		});
//
//		TextButton cancelButton = new TextButton("Cancel", skin);
//		cancelButton.pad(5);
//		cancelButton.padLeft(15);
//		cancelButton.padRight(15);
//		buttonGroup.addActor(cancelButton);
//		cancelButton.addListener(new ClickListener() {
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				if (isClient) {
//					app.setState(State.MainMenu);
//				} else {
//					app.setState(State.MultiplayerMapSelection);
//				}
//			}
//		});
//
//		root.addActor(buttonGroup);
//	}
//
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
//
//	private void startServer() {
//		final Network network = NetworkSystem.getInstance();
//		network.startServer(playerNameField.getText());
//		app.setState(State.MultiplayerLobby);
//	}
//
//	private void connectToServer() {
//		statusLabel1.setText("Connecting...");
//		statusLabel2.setText("");
//		connectToServer = true;
//		ticksUntilConnect = 1;
//	}

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
			if (isClient) {
				app.setState(State.MainMenu);
			} else {
				app.setState(State.MultiplayerMapSelection);
			}
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
	public void onViewportResized(int newWidth, int newHeight) {
		recalculateLayout(newWidth, newHeight);
	}

	@Override
	public void dispose() {
		if (Gdx.input.getInputProcessor() == this) {
			Gdx.input.setInputProcessor(null);
		}
	}
}
