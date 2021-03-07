/**
 *
 */

package bubolo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import bubolo.GameApplication;
import bubolo.GameApplication.State;
import bubolo.net.Network;
import bubolo.net.NetworkObserver;
import bubolo.net.NetworkSystem;
import bubolo.net.command.SendMessage;
import bubolo.net.command.SendMessage.MessageType;
import bubolo.world.World;

/**
 * The network game lobby, which allows users to message each other before starting the game.
 *
 * @author BU CS673 - Clone Productions
 */
public class LobbyScreen extends Screen implements NetworkObserver
{
	private Label messageHistory;
	private TextButton sendMessageButton;
	private TextField sendMessageField;
	private TextButton startGameButton;

	private final GameApplication app;
	private final World world;

	private long startTime;
	private long lastSecondsRemaining;

	// The number of clients who are connected.
	private int clientCount;

	// True if the game is starting.
	private boolean startingGame;

	private boolean messageHistoryReceivedFromServer;

	/**
	 * Constructs the network game lobby.
	 *
	 * @param app
	 *            reference to the Game Application.
	 * @param world
	 *            reference to the game world.
	 */
	public LobbyScreen(GameApplication app, World world)
	{
		this.app = app;
		this.world = world;

		TextureAtlas atlas = new TextureAtlas(
				new FileHandle(UserInterface.UI_PATH + "uiskin.atlas"));
		Skin skin = new Skin(new FileHandle(UserInterface.UI_PATH + "uiskin.json"), atlas);

		createMessageHistoryBox(skin);
		createSendMessageRow(skin);

		Network net = NetworkSystem.getInstance();
		net.addObserver(this);
		// If this is the server, the message history was already received.
		messageHistoryReceivedFromServer = net.isServer();
	}

	private void createMessageHistoryBox(Skin skin)
	{
		table.row().colspan(3)
				.width(Gdx.graphics.getWidth() - 20.f)
				.height(Gdx.graphics.getHeight() - 100.f);

		messageHistory = new Label("", skin);
		messageHistory.setWrap(true);
		messageHistory.setAlignment(Align.top + Align.left);

		ScrollPane scrollpane = new ScrollPane(messageHistory, skin);
		table.add(scrollpane).expand();
	}

	private void createSendMessageRow(Skin skin)
	{
		table.row().padBottom(15.f);

		final Network net = NetworkSystem.getInstance();

		sendMessageButton = new TextButton("Send", skin);

		sendMessageButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				sendMessage();
			}
		});

		table.add(sendMessageButton).expandX().width(100.f);

		sendMessageField = new TextField("", skin);
		final float width = net.isServer() ? Gdx.graphics.getWidth() - 250.f :
				Gdx.graphics.getWidth() - 150.f;
		table.add(sendMessageField).expandX().width(width);

		stage.addListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode)
			{
				if (keycode == Input.Keys.ENTER)
				{
					sendMessage();
				}
				return false;
			}
		});

		if (net.isServer())
		{
			startGameButton = new TextButton("Start", skin);
			table.add(startGameButton).expandX().width(100.f);

			startGameButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y)
				{
					if (clientCount > 0)
					{
						if (!startingGame)
						{
							startingGame = true;
							net.startGame(world);
						}
					}
					else
					{
						appendToMessageHistory(messageHistory, "Unable to start game: No clients are connected.");
					}
				}
			});
		}
	}

	@Override
	protected void onUpdate()
	{
		if (app.getState() == State.GAME_STARTING)
		{
			final long currentTime = System.currentTimeMillis();
			final long secondsRemaining = (startTime - currentTime) / 1000L;

			if (currentTime < startTime)
			{
				if (secondsRemaining < lastSecondsRemaining)
				{
					appendToMessageHistory(messageHistory, secondsRemaining + "...");
					lastSecondsRemaining = secondsRemaining;
				}
			}
			else
			{
				app.setState(State.NET_GAME);
			}
		}
	}

	private void sendMessage()
	{
		if (!sendMessageField.getText().isEmpty())
		{
			Network net = NetworkSystem.getInstance();
			net.send(new SendMessage(MessageType.Message, sendMessageField.getText()));
			appendToMessageHistory(messageHistory, net.getPlayerName() + ": " + sendMessageField.getText());
			sendMessageField.setText("");
		}
	}

	@Override
	public void onConnect(String clientName, String serverName)
	{
		appendToMessageHistory(messageHistory, "Welcome " + clientName + ". The host is " + serverName + ".");
	}

	@Override
	public void onClientConnected(String clientName)
	{
		++clientCount;

		Network net = NetworkSystem.getInstance();
		net.send(new SendMessage(MessageType.LobbyMessageHistory, messageHistory.getText().toString()));

		appendToMessageHistory(messageHistory, clientName + " joined the game.");
	}

	@Override
	public void onClientDisconnected(String clientName)
	{
		--clientCount;
		appendToMessageHistory(messageHistory, clientName + " left the game.");
	}

	@Override
	public void onGameStart(int secondsUntilStart)
	{
		startingGame = true;
		appendToMessageHistory(messageHistory, "Get ready: The game is starting!\n\n");

		long currentTime = System.currentTimeMillis();
		startTime = currentTime + (secondsUntilStart * 1000);
		lastSecondsRemaining = secondsUntilStart;

		app.setState(State.GAME_STARTING);
	}

	@Override
	public void onMessageReceived(SendMessage.MessageType messageType, String message)
	{
		if (!messageHistoryReceivedFromServer && messageType == MessageType.LobbyMessageHistory) {
			messageHistoryReceivedFromServer = true;
			if (!message.isEmpty()) {
				appendToMessageHistory(messageHistory, message);
			}
		} else if (messageHistoryReceivedFromServer && messageType == MessageType.Message) {
			appendToMessageHistory(messageHistory, message);
		}
	}

	private static void appendToMessageHistory(Label messageHistory, String message)
	{
		messageHistory.setText(message + "\n" + messageHistory.getText());
	}

	@Override
	public void dispose()
	{
		Network net = NetworkSystem.getInstance();
		net.removeObserver(this);
	}
}
