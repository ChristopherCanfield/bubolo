package bubolo.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Random;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import bubolo.AbstractGameApplication;
import bubolo.audio.Audio;
import bubolo.graphics.Graphics;
import bubolo.net.Network;
import bubolo.net.NetworkObserver;
import bubolo.net.NetworkSystem;
import bubolo.net.command.CreateTank;
import bubolo.net.command.SendMessage.MessageType;
import bubolo.ui.LobbyScreen;
import bubolo.ui.Screen;
import bubolo.world.GameWorld;
import bubolo.world.entity.concrete.Tank;

/**
 * For testing only.
 *
 * @author BU CS673 - Clone Productions
 */
public class NetClientTestApplication extends AbstractGameApplication implements NetworkObserver
{
	public static void main(String[] args) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("Name: ");
		String name = br.readLine();

		System.out.print("Server IP Address: ");
		String addressString = br.readLine();
		InetAddress address = Inet4Address.getByName(addressString);

		Network net = NetworkSystem.getInstance();
		net.connect(address, name);

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "BUBOLO Net Client Integration";
		cfg.width = 1067;
		cfg.height = 600;
		new LwjglApplication(new NetClientTestApplication(1067, 600), cfg);
	}

	private int windowWidth;
	private int windowHeight;

	private Graphics graphics;
	private Network network;

	private Screen gameLobby;

	/**
	 * The number of game ticks (calls to <code>update</code>) per second.
	 */
	public static final long TICKS_PER_SECOND = 30;

	/**
	 * The number of milliseconds per game tick.
	 */
	public static final long MILLIS_PER_TICK = 1000 / TICKS_PER_SECOND;

	/**
	 * Constructs an instance of the game application. Only one instance should ever exist.
	 *
	 * @param windowWidth
	 *            the width of the window.
	 * @param windowHeight
	 *            the height of the window.
	 */
	public NetClientTestApplication(int windowWidth, int windowHeight)
	{
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
	}

	/**
	 * Create anything that relies on graphics, sound, windowing, or input devices here.
	 *
	 * @see <a
	 *      href="http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void create()
	{
		Audio.initialize();
		graphics = new Graphics(windowWidth, windowHeight);

		network = NetworkSystem.getInstance();
		network.addObserver(this);

		world = new GameWorld();

		setState(State.GAME_LOBBY);
	}

	/**
	 * Called automatically by the rendering library.
	 *
	 * @see <a
	 *      href="http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void render()
	{
		final State state = getState();
		if (state == State.NET_GAME)
		{
			gameLobby.dispose();

			graphics.draw(world);
			world.update();
			network.update(world);
		}
		else if (state == State.GAME_LOBBY || state == State.GAME_STARTING)
		{
			graphics.draw(gameLobby);
			network.update(world);
		}
	}

	@Override
	protected void onStateChanged()
	{
		if (getState() == State.NET_GAME)
		{
			gameLobby.dispose();

			Tank tank = world.addEntity(Tank.class);
			tank.setTransform(getRandomX(), 200, 0);
			tank.setLocalPlayer(true);
			network.send(new CreateTank(tank));

			setReady(true);
		}
		else if (getState() == State.GAME_LOBBY)
		{
			gameLobby = new LobbyScreen(this, world);
		}
	}

	private static int getRandomX()
	{
		int val = (new Random()).nextInt(10);
		return (1250 + (100 * val));
	}

	/**
	 * Called when the application is destroyed.
	 *
	 * @see <a
	 *      href="http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void dispose()
	{
		Audio.dispose();
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void resume()
	{
	}

	@Override
	public void onConnect(String clientName, String serverName)
	{
		System.out.println(clientName + " connected to game. The host is " + serverName + ".");
	}

	@Override
	public void onClientConnected(String clientName)
	{
		System.out.println(clientName + " joined the game.");
	}

	@Override
	public void onClientDisconnected(String clientName)
	{
		System.out.println(clientName + " left the game.");
	}

	@Override
	public void onGameStart(int timeUntilStart)
	{
		System.out.println("Game is starting.");
	}

	@Override
	public void onMessageReceived(MessageType messageType, String message)
	{
	}
}
