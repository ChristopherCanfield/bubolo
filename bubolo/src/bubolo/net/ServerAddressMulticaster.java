package bubolo.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerAddressMulticaster {
	private final ServerAddressMessage message;

	private final AtomicBoolean shutDown = new AtomicBoolean();
	private boolean isStarted;

	public ServerAddressMulticaster(ServerAddressMessage message) {
		this.message = message;
	}

	public void start() {
		assert !isStarted;
		isStarted = true;

		byte[] messageBytes = message.toBytes();

		Thread thread = new Thread(() -> {
			while (!shutDown.get()) {
				try (MulticastSocket socket = new MulticastSocket(NetworkInformation.GAME_PORT)) {
					InetAddress group = InetAddress.getByName(NetworkInformation.MulticastAddress);
			        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, group, NetworkInformation.GAME_PORT);
			        socket.send(packet);
			        Thread.sleep(1_000);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "server-address-multicaster-thread");
		thread.start();
	}

	public void shutDown() {
		shutDown.set(true);
	}
}
