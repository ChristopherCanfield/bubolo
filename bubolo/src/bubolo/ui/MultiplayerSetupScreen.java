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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import bubolo.Config;
import bubolo.GameApplication;
import bubolo.GameApplication.State;
import bubolo.net.Network;
import bubolo.net.NetworkException;
import bubolo.net.NetworkSystem;

/**
 * The join game screen, which allows the user to enter a name and ip address.
 *
 * @author Christopher D. Canfield
 */
public class MultiplayerSetupScreen extends Stage2dScreen {
	public enum PlayerType {
		Server,
		Client
	}

	private TextField playerNameField;
	private TextField ipAddressField;
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
	public MultiplayerSetupScreen(GameApplication app, PlayerType playerType) {
		this.app = app;
		this.isClient = (playerType == PlayerType.Client);

		TextureAtlas atlas = new TextureAtlas(new FileHandle(Config.UiPath.resolve("skin.atlas").toString()));
		Skin skin = new Skin(new FileHandle(Config.UiPath.resolve("skin.json").toString()), atlas);

		createPlayerNameRow(skin);
		createIpAddressRow(skin);
		addOkButtonRow(skin);

		table.row().colspan(8).padTop(50.f).center();
		statusLabel1 = new Label("", skin);
		table.add(statusLabel1);

		table.row().colspan(8).padTop(50.f).left().padLeft(leftPadding);
		statusLabel2 = new Label("", skin);
		table.add(statusLabel2);

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
					app.setState(State.MainMenu);
				}
				return false;
			}
		});
	}

	private void createPlayerNameRow(Skin skin) {
		table.row().align(Align.left).padTop(100.f);

		table.add(new Label("Name:", skin)).padLeft(leftPadding);

		playerNameField = new TextField("", skin);
		table.add(playerNameField).width(250.f);
	}

	private void createIpAddressRow(Skin skin) {
		table.row().align(Align.left).padTop(5.f);

		if (isClient) {
			table.add(new Label("Host IP Address:", skin)).padLeft(leftPadding);

			ipAddressField = new TextField("", skin);
			table.add(ipAddressField).width(160.f);
		} else {
			try {
				table.add(new Label("IP Address:", skin)).padLeft(leftPadding);

				String ipAddresses = getIpAddresses();
				table.add(new Label(ipAddresses, skin));
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

	private void addOkButtonRow(Skin skin) {
		table.row().colspan(8).padTop(25.f);

		TextButton okButton = new TextButton("OK", skin);

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

		table.add(okButton).expandX().width(100.f);
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
	public void dispose() {
	}
}
