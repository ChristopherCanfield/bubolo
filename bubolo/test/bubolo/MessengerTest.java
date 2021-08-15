package bubolo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import bubolo.mock.MockPlayerAttributes;
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

	@Test
	void notifyPlayerDisconnected() {
		messenger.addObserver(observer);
		assertFalse(observer.playerDisconnectedMessageReceived);

		messenger.notifyPlayerDisconnected("Player");
		assertTrue(observer.playerDisconnectedMessageReceived);
	}

	@Test
	void notifyAllianceRequestSent() {
		messenger.addObserver(observer);
		assertFalse(observer.allianceRequestSent);

		messenger.notifyAllianceRequestSent("Player");
		assertTrue(observer.allianceRequestSent);
	}

	@Test
	void notifyAllianceRequestReceived() {
		messenger.addObserver(observer);
		assertFalse(observer.allianceRequestReceived);

		messenger.notifyAllianceRequestReceived("Player");
		assertTrue(observer.allianceRequestReceived);
	}

	/**
	 * Tests notifying that an alliance request was accepted, first with a local requester and a remote accepter, then a remote requester and a local accepter.
	 */
	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void notifyAllianceRequestAccepted(boolean localRequester) {
		boolean localAccepter = !localRequester;

		messenger.addObserver(observer);
		assertFalse(observer.allianceRequestAccepted);

		messenger.notifyAllianceRequestAccepted(new MockPlayerAttributes(localRequester), new MockPlayerAttributes(localAccepter));
		assertTrue(observer.allianceRequestAccepted);
	}

	/**
	 * Tests notifying that an alliance request was rejected, first with a local requester and a remote rejecter, then a remote requester and a local rejecter.
	 */
	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void notifyAllianceRequestRejected(boolean localRequester) {
		boolean localAccepter = !localRequester;

		messenger.addObserver(observer);
		assertFalse(observer.allianceRequestRejected);

		messenger.notifyAllianceRequestRejected(new MockPlayerAttributes(localRequester), new MockPlayerAttributes(localAccepter));
		assertTrue(observer.allianceRequestRejected);
	}

	private static class TestObserver implements Messenger.MessageObserver {
		boolean attackMessageReceived;
		boolean capturedMessageReceived;
		boolean playerDiedMessageReceived;
		boolean playerDisconnectedMessageReceived;
		boolean allianceRequestSent;
		boolean allianceRequestReceived;
		boolean allianceRequestAccepted;
		boolean allianceRequestRejected;

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

		@Override
		public void messagePlayerDisconnected(String message) {
			playerDisconnectedMessageReceived = true;
		}

		@Override
		public void messageAllianceRequestReceived(String message) {
			allianceRequestReceived = true;
		}

		@Override
		public void messageAllianceRequestSent(String message) {
			allianceRequestSent = true;
		}

		@Override
		public void messageAllianceRequestAccepted(String message) {
			allianceRequestAccepted = true;
		}

		@Override
		public void messageAllianceRequestRejected(String message) {
			allianceRequestRejected = true;
		}
	}
}
