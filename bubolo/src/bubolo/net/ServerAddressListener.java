package bubolo.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Listens for UDP multicast packets from a game server, and notifies an observer when they're encountered.
 *
 * @author Christopher D. Canfield
 */
public class ServerAddressListener {
	public static interface Observer {
		void onServerAddressFound(InetAddress address, String serverName, String mapName);
	}

	private final ServerAddressListener.Observer observer;
	private final AtomicBoolean shutDown = new AtomicBoolean();

	public ServerAddressListener(Observer observer) {
		this.observer = observer;
	}

	public void start(NetworkInterface networkInterface) {
		Thread thread = new Thread(() -> {
			MulticastSocket socket;
			try {
				socket = new MulticastSocket(NetworkInformation.GAME_PORT);

				while (!shutDown.get()) {
					InetAddress address = InetAddress.getByName(NetworkInformation.MulticastAddress);
					InetSocketAddress group = new InetSocketAddress(address, NetworkInformation.GAME_PORT);
					socket.joinGroup(group, networkInterface);

					byte[] buffer = new byte[256];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					socket.receive(packet);

					String messageText = new String(packet.getData(), 0, packet.getLength());
					var message = new ServerAddressMessage(messageText);
					observer.onServerAddressFound(message.serverAddress(), message.serverName(), message.mapName());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}

	public void shutDown() {
		shutDown.set(false);
	}
}
