package bubolo.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.badlogic.gdx.Gdx;
import com.google.common.base.Charsets;

/**
 * Listens for UDP multicast packets from a game server, and notifies an observer when they're encountered. The
 * observer is notified on the main thread.
 *
 * The {@code shutDown()} method must be called when this object is no longer needed.
 *
 * @author Christopher D. Canfield
 */
public class ServerAddressListener {
	public static interface Observer {
		void onServerAddressFound(ServerAddressMessage message);
	}

	private final ServerAddressListener.Observer observer;
	private final AtomicBoolean shutDown = new AtomicBoolean();
	private boolean isStarted;

	public ServerAddressListener(Observer observer) {
		this.observer = observer;
	}

	/**
	 * Starts this ServerAddressListener. This method must only be called once. The {@code shutDown()} method must be called
	 * when this object is not longer needed.
	 *
	 * @param networkInterface the network interface to listen to.
	 */
	public void start(NetworkInterface networkInterface) {
		assert !isStarted;
		isStarted = true;

		Thread thread = new Thread(() -> {
			MulticastSocket socket;
			try {
				socket = new MulticastSocket(NetworkInformation.GAME_PORT);
				socket.setSoTimeout(2_500);

				InetAddress address = InetAddress.getByName(NetworkInformation.MulticastAddress);
				InetSocketAddress group = new InetSocketAddress(address, NetworkInformation.GAME_PORT);
				socket.joinGroup(group, networkInterface);

				while (!shutDown.get()) {
					byte[] buffer = new byte[ServerAddressMessage.MaxSizeBytes];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					socket.receive(packet);

					String messageText = new String(packet.getData(), 0, packet.getLength(), Charsets.UTF_8);
					var message = new ServerAddressMessage(messageText);
					// Call the observer on the main thread.
					Gdx.app.postRunnable(() -> {
						observer.onServerAddressFound(message);
					});
				}
			} catch (SocketTimeoutException e) {
				// Timeouts are fine. They're enabled to allow the thread to be shut down.
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "server-address-listener-thread");
		thread.start();
	}

	/**
	 * Shuts down the thread associated with this ServerAddressListener.
	 */
	public void shutDown() {
		shutDown.set(false);
	}
}
