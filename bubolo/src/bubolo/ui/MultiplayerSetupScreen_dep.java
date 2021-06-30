/**
 *
 */

package bubolo.ui;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import bubolo.Config;
import bubolo.GameApplication;
import bubolo.GameApplication.State;
import bubolo.graphics.Graphics;
import bubolo.net.Network;
import bubolo.net.NetworkException;
import bubolo.net.NetworkSystem;

/**
 * The join game screen, which allows the user to enter a name and ip address.
 *
 * @author Christopher D. Canfield
 */
public class MultiplayerSetupScreen_dep extends Stage2dScreen<VerticalGroup> {
	public enum PlayerType {
		Server,
		Client
	}

	private TextField playerNameField;
	private TextField ipAddressField;
	private List<String> availableGamesList;
	private Label statusLabel1;
	private Label statusLabel2;

	private final GameApplication app;

	private final boolean isClient;

	// These variables enable the screen to be updated with a message before the connection attempt
	// is made. This is useful because the connection attempt may take a few seconds, and the screen
	// will appear frozen during that time otherwise.
	private boolean connectToServer;
	private int ticksUntilConnect;

	private static final int leftPadding = 440;

	/**
	 * Constructs the network game lobby.
	 *
	 * @param app reference to the Game Application.
	 * @param playerType whether this is a server or client.
	 */
	public MultiplayerSetupScreen_dep(GameApplication app, Graphics graphics, PlayerType playerType) {
		super(graphics, new VerticalGroup());
		root.setSize(Config.TargetWindowWidth, Config.TargetWindowHeight);
		root.align(Align.topLeft);

		this.app = app;
		this.isClient = (playerType == PlayerType.Client);

		TextureAtlas atlas = new TextureAtlas(new FileHandle(Config.UiPath.resolve("skin.atlas").toString()));
		Skin skin = new Skin(new FileHandle(Config.UiPath.resolve("skin.json").toString()), atlas);

		addPlayerNameRow(skin);
		addIpAddressRow(skin);
		addAvailableGames(skin);
		addButtonRow(skin);
		addStatusLabels(skin);

		root.invalidateHierarchy();

		stage.addListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
					if (textFieldsPopulated()) {
						if (isClient) {
							connectToServer();
						} else {
							startServer();
						}
					}
				} else if (keycode == Input.Keys.ESCAPE) {
					if (isClient) {
						app.setState(State.MainMenu);
					} else {
						app.setState(State.MultiplayerMapSelection);
					}
				}
				return false;
			}
		});
	}

	private void addPlayerNameRow(Skin skin) {
		HorizontalGroup playerNameRow = new HorizontalGroup();
		playerNameRow.padTop(75);
		playerNameRow.padLeft(leftPadding);
		playerNameRow.align(Align.left);
		playerNameRow.space(100);

		var playerNameLabel = new Label("Name:", skin);
		playerNameRow.addActor(playerNameLabel);

		playerNameField = new TextField("", skin);
		playerNameField.setWidth(250);
		playerNameRow.addActor(playerNameField);

		root.addActor(playerNameRow);
	}

	private void addIpAddressRow(Skin skin) {
		var ipAddressRow = new HorizontalGroup();
		ipAddressRow.padLeft(leftPadding);
		ipAddressRow.align(Align.left).padTop(5.0f);

		if (isClient) {
			ipAddressRow.space(18);

			var hostIpAddressLabel = new Label("Host IP Address:", skin);
			ipAddressRow.addActor(hostIpAddressLabel);

			ipAddressField = new TextField("", skin);
			ipAddressField.setWidth(160.0f);
			ipAddressRow.addActor(ipAddressField);
		} else {
			try {
				ipAddressRow.space(98);

				var ipAddressLabel = new Label("IP Address:", skin);
				ipAddressRow.addActor(ipAddressLabel);

				String ipAddresses = getIpAddresses();
				ipAddressRow.addActor(new Label(ipAddresses, skin));
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}

		root.addActor(ipAddressRow);
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

	private void addAvailableGames(Skin skin) {
		if (isClient) {
			VerticalGroup layoutGroup = new VerticalGroup();
			layoutGroup.padTop(30);
			layoutGroup.padLeft(leftPadding);
			layoutGroup.space(10);

			Label availableGamesLabel = new Label("Available Games:", skin);
			layoutGroup.addActor(availableGamesLabel);

			availableGamesList = new List<>(skin);
			availableGamesList.setItems("Hello", "Game 2");
			availableGamesList.setSelectedIndex(-1);

			ScrollPane scrollpane = new ScrollPane(availableGamesList, skin);
			scrollpane.setWidth(200);
			layoutGroup.addActor(scrollpane);
			root.addActor(layoutGroup);
		}
	}

	private void addButtonRow(Skin skin) {
		HorizontalGroup buttonGroup = new HorizontalGroup();
		buttonGroup.align(Align.center);
		buttonGroup.padLeft(leftPadding);
		buttonGroup.padTop(25);
		buttonGroup.space(10);

		TextButton okButton = new TextButton("OK", skin);
		okButton.pad(5);
		okButton.padLeft(30);
		okButton.padRight(30);
		buttonGroup.addActor(okButton);

		okButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (textFieldsPopulated()) {
					if (isClient) {
						connectToServer();
					} else {
						startServer();
					}
				}
			}
		});

		TextButton cancelButton = new TextButton("Cancel", skin);
		cancelButton.pad(5);
		cancelButton.padLeft(15);
		cancelButton.padRight(15);
		buttonGroup.addActor(cancelButton);
		cancelButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (isClient) {
					app.setState(State.MainMenu);
				} else {
					app.setState(State.MultiplayerMapSelection);
				}
			}
		});

		root.addActor(buttonGroup);
	}

	private void addStatusLabels(Skin skin) {
		VerticalGroup statusGroup = new VerticalGroup();
		statusGroup.align(Align.center);
		statusGroup.padTop(50);
		statusGroup.padLeft(leftPadding);

		statusLabel1 = new Label("Test", skin);
		statusGroup.addActor(statusLabel1);

		statusLabel2 = new Label("Test2", skin);
		statusGroup.addActor(statusLabel2);

		root.addActor(statusGroup);
	}

	private boolean textFieldsPopulated() {
		return !playerNameField.getText().isEmpty() && !(isClient && ipAddressField.getText().isEmpty());
	}

	private void startServer() {
		final Network network = NetworkSystem.getInstance();
		network.startServer(playerNameField.getText());
		app.setState(State.MultiplayerLobby);
	}

	private void connectToServer() {
		statusLabel1.setText("Connecting...");
		statusLabel2.setText("");
		connectToServer = true;
		ticksUntilConnect = 1;
	}

	@Override
	public void onUpdate() {
		if (connectToServer) {
			if (ticksUntilConnect == 0) {
				connectToServer = false;

				InetAddress ipAddress;
				try {
					ipAddress = InetAddress.getByName(ipAddressField.getText());

					final Network network = NetworkSystem.getInstance();
					network.connect(ipAddress, playerNameField.getText());
				} catch (UnknownHostException | NetworkException e) {
					statusLabel1.setText("");
					statusLabel2.setText("Unable to connect: " + e.getMessage());
					return;
				}

				app.setState(State.MultiplayerLobby);
			} else {
				--ticksUntilConnect;
			}
		}
	}

	@Override
	protected void onDispose() {
	}
}
