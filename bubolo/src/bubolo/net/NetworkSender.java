/**
 * Copyright (c) 2014 BU MET CS673 Game Engineering Team
 *
 * See the file license.txt for copying permission.
 */

package bubolo.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

import bubolo.Config;

/**
 * Sends NetworkCommands across the network.
 *
 * @author BU CS673 - Clone Productions
 */
class NetworkSender implements Runnable
{
	private final ObjectOutputStream stream;
	private final NetworkCommand command;

	/**
	 * Constructs a NetworkSender.
	 *
	 * @param stream
	 *            the object output stream to the client.
	 * @param command
	 *            the command to send.
	 */
	NetworkSender(ObjectOutputStream stream, NetworkCommand command)
	{
		this.stream = stream;
		this.command = command;
	}

	@Override
	public void run()
	{
		try
		{
			synchronized(stream)
			{
				stream.writeObject(command);
			}
		}
		catch (IOException e)
		{
			Logger.getLogger(Config.AppProgramaticTitle).severe("Exception in " + NetworkSender.class.getName() + ": " + e.toString());
			e.printStackTrace();
			throw new NetworkException(e);
		}
	}
}
