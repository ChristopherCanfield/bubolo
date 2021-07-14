package bubolo.net;

import bubolo.net.command.SendMessage.MessageType;

public class MockNetworkObserver implements NetworkObserver {
	private String clientName;
	private String serverName;
	private String message;
	private int timeUntilStart;

	String getClientName()
	{
		return clientName;
	}

	String getServerName()
	{
		return serverName;
	}

	String getMessage()
	{
		return message;
	}

	int getTimeUntilStart()
	{
		return timeUntilStart;
	}


	@Override
	public void onConnect(String clientName, String serverName)
	{
		this.clientName = clientName;
		this.serverName = serverName;
	}

	@Override
	public void onClientConnected(String clientName)
	{
		this.clientName = clientName;
	}

	@Override
	public void onClientDisconnected(String clientName)
	{
		this.clientName = clientName;
	}

	@Override
	public void onGameStart(int timeUntilStart)
	{
		this.timeUntilStart = timeUntilStart;
	}

	@Override
	public void onMessageReceived(MessageType messageType, String message)
	{
		this.message = message;
	}

	@Override
	public void onClientReady(String clientName) {
		this.clientName = clientName;
	}
}
