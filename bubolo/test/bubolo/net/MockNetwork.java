/**
 *
 */

package bubolo.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.List;

import bubolo.GameApplication;
import bubolo.world.Spawn;

/**
 * @author BU CS673 - Clone Productions
 */
public class MockNetwork implements Network
{
	ObjectOutputStream oos = null;

	MockNetwork()
	{
		try
		{
			oos = new ObjectOutputStream(System.out);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void connect(InetAddress serverIpAddress, String playerName) throws NetworkException, IllegalStateException
	{
	}

	@Override
	public void send(NetworkCommand command)
	{
	}

	@Override
	public void postToGameThread(NetworkCommand command)
	{
	}

	@Override
	public void dispose()
	{
	}

	@Override
	public void startServer(String playerName) throws NetworkException, IllegalStateException
	{
	}

	@Override
	public void addObserver(NetworkObserver observer)
	{
	}

	@Override
	public void removeObserver(NetworkObserver observer)
	{
	}

	@Override
	public boolean isServer()
	{
		return false;
	}

	@Override
	public String getPlayerName()
	{
		return null;
	}

	@Override
	public void sendToClient(int playerIndex, NetworkCommand command) {
	}

	@Override
	public void startGame(List<Spawn> initialSpawnPositions) {
	}

	@Override
	public void update(GameApplication app) {
	}
}
