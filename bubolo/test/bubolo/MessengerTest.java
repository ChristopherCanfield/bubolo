package bubolo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bubolo.world.Pillbox;
import bubolo.world.Tank;

class MessengerTest {
	private Messenger messenger;
	private TestObserver observer;

	@BeforeEach
	void setUp() throws Exception {
		messenger = new Messenger();
		observer = new TestObserver();
	}

	@Test
	void addRemoveObserver() {
		assertEquals(0, messenger.observerCount());

		messenger.addObserver(observer);
		assertEquals(1, messenger.observerCount());

		messenger.removeObserver(observer);
		assertEquals(0, messenger.observerCount());
	}

	@Test
	void notifyObjectUnderAttack() {
		messenger.addObserver(observer);
		assertFalse(observer.attackMessageReceived);

		messenger.notifyObjectUnderAttack(Pillbox.class, "Northwest", "Player");
		assertTrue(observer.attackMessageReceived);
	}

	@Test
	void notifyObjectCaptured() {
		messenger.addObserver(observer);
		assertFalse(observer.capturedMessageReceived);

		messenger.notifyObjectCaptured(Pillbox.class, "Northwest", false, "Player", false, "Player 2");
		assertTrue(observer.capturedMessageReceived);
	}

	@Test
	void notifyPlayerDied() {
		messenger.addObserver(observer);
		assertFalse(observer.playerDiedMessageReceived);

		messenger.notifyPlayerDied("Player", false, Tank.class, "Player 2");
		assertTrue(observer.playerDiedMessageReceived);
	}


	private static class TestObserver implements Messenger.MessageObserver {
		boolean attackMessageReceived;
		boolean capturedMessageReceived;
		boolean playerDiedMessageReceived;

		@Override
		public void messageObjectUnderAttack(String message) {
			attackMessageReceived = true;
		}

		@Override
		public void messageObjectCaptured(String message, boolean thisPlayerLostObject, boolean thisPlayerCapturedObject) {
			capturedMessageReceived = true;
		}

		@Override
		public void messagePlayerDied(String message, boolean thisPlayerDied) {
			playerDiedMessageReceived = true;
		}
	}
}
