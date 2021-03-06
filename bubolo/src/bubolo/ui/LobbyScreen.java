package bubolo.ui;

import java.net.InetAddress;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import bubolo.BuboloApplication;
import bubolo.Config;
import bubolo.GameApplication.State;
import bubolo.graphics.Graphics;
import bubolo.net.Network;
import bubolo.net.NetworkObserver;
import bubolo.net.NetworkSystem;
import bubolo.net.PlayerInfo;
import bubolo.net.ServerAddressMessage;
import bubolo.net.ServerAddressMulticaster;
import bubolo.net.command.SendMap;
import bubolo.net.command.SendMessage;
import bubolo.net.command.SendMessage.MessageType;
import bubolo.world.World;

/**
 * The network game lobby, which allows users to message each other before starting the game.
 *
 * @author Christopher D. Canfield
 */
public class LobbyScreen extends Stage2dScreen<Table> implements NetworkObserver {
	private Label messageHistory;
	private TextButton sendMessageButton;
	private TextField sendMessageField;
	private TextButton startGameButton;

	private final BuboloApplication app;
	private final World world;

	private final PlayerInfo playerInfo;

	private long startTime;
	private long lastSecondsRemaining;

	// The number of clients who are connected.
	private int clientCount;

	// The number of clients who have finished downloading the map and are ready to start.
	private int clientsReadyToStart;

	// True if the game is starting.
	private boolean startingGame;

	private boolean messageHistoryReceivedFromServer;

	private ServerAddressMulticaster serverAddressMulticaster;

	/**
	 * Constructs the network game lobby.
	 *
	 * @param app reference to the Game Application.
	 * @param graphics reference to the graphics system.
	 * @param world reference to the game world.
	 */
	public LobbyScreen(BuboloApplication app, Graphics graphics, World world, PlayerInfo playerInfo) {
		super(graphics, new Table());
		root.setFillParent(true);
		root.top();

		this.app = app;
		this.world = world;
		this.playerInfo = playerInfo;

		TextureAtlas atlas = new TextureAtlas(new FileHandle(Config.UiPath.resolve("skin.atlas").toString()));
		Skin skin = new Skin(new FileHandle(Config.UiPath.resolve("skin.json").toString()), atlas);

		createMessageHistoryBox(skin);
		createSendMessageRow(skin);

		Network net = NetworkSystem.getInstance();
		net.addObserver(this);
		// If this is the server, the message history was already received.
		messageHistoryReceivedFromServer = net.isServer();

		if (net.isServer()) {
			InetAddress ipAddress;
			if (playerInfo.ipAddress() != null) {
				ipAddress = playerInfo.ipAddress();
			} else {
				var ipAddresses = Network.getIpAddresses();
				ipAddress = ipAddresses.firstIpAddress();
			}

			ServerAddressMessage message = new ServerAddressMessage(ipAddress, playerInfo.name(), app.mapName());
			serverAddressMulticaster = new ServerAddressMulticaster(message);
			serverAddressMulticaster.start();
		}
	}

	private void createMessageHistoryBox(Skin skin) {
		root.row().colspan(3).width(Gdx.graphics.getWidth() - 20.f).height(Gdx.graphics.getHeight() - 100.f);

		messageHistory = new Label("", skin);
		messageHistory.setWrap(true);
		messageHistory.setAlignment(Align.top + Align.left);

		ScrollPane scrollpane = new ScrollPane(messageHistory, skin);
		scrollpane.setFadeScrollBars(false);
		root.add(scrollpane).expand();
	}

	private void createSendMessageRow(Skin skin) {
		root.row().padBottom(15.f);

		final Network net = NetworkSystem.getInstance();

		sendMessageButton = new TextButton("Send", skin);

		sendMessageButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				sendMessage();
			}
		});

		root.add(sendMessageButton).expandX().width(100.f);

		sendMessageField = new TextField("", skin);
		final float width = net.isServer() ? Gdx.graphics.getWidth() - 250.f : Gdx.graphics.getWidth() - 150.f;
		root.add(sendMessageField).expandX().width(width);

		stage.addListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
					sendMessage();
				}
				return false;
			}
		});

		if (net.isServer()) {
			startGameButton = new TextButton("Start", skin);
			root.add(startGameButton).expandX().width(100.f);

			startGameButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (clientCount > 0) {
						if (!startingGame) {
							startingGame = true;
							appendToMessageHistory(messageHistory, "Sending map data...\n");
							net.send(new SendMessage("Sending map data...\n"));
							net.send(new SendMap(world));
						}
					} else {
						appendToMessageHistory(messageHistory, "Unable to start game: No clients are connected.");
					}
				}
			});
		}
	}

	@Override
	protected void onUpdate() {
		if (app.getState() == State.MultiplayerStarting) {
			final long currentTime = System.currentTimeMillis();
			final long secondsRemaining = (startTime - currentTime) / 1000L;

			if (currentTime < startTime) {
				if (secondsRemaining < lastSecondsRemaining) {
					appendToMessageHistory(messageHistory, secondsRemaining + "...");
					lastSecondsRemaining = secondsRemaining;
				}
			} else {
				app.setState(State.MultiplayerGame);
			}
		}
	}

	private void sendMessage() {
		if (!sendMessageField.getText().isEmpty()) {
			Network net = NetworkSystem.getInstance();
			net.send(new SendMessage(sendMessageField.getText()));
			appendToMessageHistory(messageHistory, playerInfo.name() + ": " + sendMessageField.getText());
			sendMessageField.setText("");
		}
	}

	@Override
	public void onConnect(String clientName, String serverName) {
		appendToMessageHistory(messageHistory, "Welcome " + clientName + ". The host is " + serverName + ". We're playing map " + app.mapName() + ".");
	}

	@Override
	public void onClientConnected(String clientName) {
		++clientCount;

		Network net = NetworkSystem.getInstance();
		net.send(new SendMessage(MessageType.LobbyMessageHistory, messageHistory.getText().toString()));

		appendToMessageHistory(messageHistory, clientName + " joined the game.");
	}

	@Override
	public void onClientDisconnected(String clientName) {
		--clientCount;
		appendToMessageHistory(messageHistory, clientName + " left the game.");
	}

	@Override
	public void onClientReady(String clientName) {
		appendToMessageHistory(messageHistory, clientName + " has finished downloading the map and is ready to play.");

		++clientsReadyToStart;
		if (clientsReadyToStart == clientCount) {
			Network net = NetworkSystem.getInstance();
			net.startGame();
		}
	}

	@Override
	public void onGameStart(int secondsUntilStart) {
		startingGame = true;
		appendToMessageHistory(messageHistory, "Get ready: The game is starting!");

		long currentTime = System.currentTimeMillis();
		startTime = currentTime + (secondsUntilStart * 1000);
		lastSecondsRemaining = secondsUntilStart;

		app.setState(State.MultiplayerStarting);
	}

	@Override
	public void onMessageReceived(SendMessage.MessageType messageType, String message) {
		if (!messageHistoryReceivedFromServer && messageType == MessageType.LobbyMessageHistory) {
			messageHistoryReceivedFromServer = true;
			if (!message.isEmpty()) {
				appendToMessageHistory(messageHistory, message);
			}
		} else if (messageHistoryReceivedFromServer && messageType == MessageType.Message) {
			appendToMessageHistory(messageHistory, message);
		}
	}

	private static void appendToMessageHistory(Label messageHistory, String message) {
		messageHistory.setText(message + "\n" + messageHistory.getText());
	}

	@Override
	protected void onDispose() {
		if (serverAddressMulticaster != null) {
			serverAddressMulticaster.shutDown();
		}
		Network net = NetworkSystem.getInstance();
		net.removeObserver(this);
	}
}
