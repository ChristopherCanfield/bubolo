package bubolo.input;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.InputProcessor;

import bubolo.util.GameLogicException;

public class InputManager implements InputProcessor {
	public enum Action {
		Accelerate,
		Decelerate,
		RotateClockwise,
		RotateCounterclockwise,
		FireCannon,
		LayMine,
		Build,

		LastGameActionIndex,

		MenuUp,
		MenuDown,
		MenuLeft,
		MenuRight,
		MenuMoveToNextGroup,
		MenuMoveToPreviousGroup,

		ShowDiplomacyMenu,

		/** A request to change either to fullscreen (if graphics.isFullscreen() is false) or windowed (if graphics.isFullscreen() is true). */
		FullscreenStatusChangeRequested,

		Activate,
		Cancel,
	}

	// Whether an action was pressed or not.
	private boolean[] actions = new boolean[Action.values().length];
	// Used to store actions that are received in the form of an event callback, rather than checked each frame.
	private boolean[] actionsBackBuffer = new boolean[actions.length];

	private final Action[] actionsEnumArray = Action.values();
	// If false, only menu-related actions are processed. Intended for in-game overlay menus.
	private boolean gameActionsEnabled = true;

	private final KeyboardInputManager keyboardInputManager = new KeyboardInputManager();
	private final GamepadInputManager gamepadInputManager = new GamepadInputManager();

	private final List<InputActionObserver> actionObservers = new CopyOnWriteArrayList<>();
	private final List<InputProcessor> inputEventObservers = new CopyOnWriteArrayList<>();

	public InputManager() {
		inputEventObservers.add(keyboardInputManager);
	}

	/**
	 * Processes input. Must be called each frame.
	 */
	public void update() {
		swapActionArrays();
		Arrays.fill(actionsBackBuffer, false);

		keyboardInputManager.update(actions);
		gamepadInputManager.update(actions);

		// If game actions are disabled, set all game actions to false.
		if (!gameActionsEnabled) {
			Arrays.fill(actions, 0, Action.LastGameActionIndex.ordinal(), false);
		}

		notifyActionObservers(actions);

		// Clear the actions array at the end of the update.
		Arrays.fill(actionsBackBuffer, false);
	}

	/**
	 * Swaps the backing arrays that the actions and actionsBackBuffer point to. The back buffer is used to store
	 * actions that arrive in the form of event callbacks.
	 */
	private void swapActionArrays() {
		boolean[] swap = actions;
		actions = actionsBackBuffer;
		actionsBackBuffer = swap;
	}

	boolean[] actionsBackBuffer() {
		return actionsBackBuffer;
	}

	public boolean isPressed(Action action) {
		return actions[action.ordinal()];
	}

	public void enableGameActions() {
		gameActionsEnabled = true;
	}

	public void disableGameActions() {
		gameActionsEnabled = false;
	}

	private void notifyActionObservers(boolean[] actions) {
		for (int observerIndex = 0; observerIndex < actionObservers.size(); observerIndex++) {
			for (int actionIndex = 0; actionIndex < actions.length; actionIndex++) {
				if (actions[actionIndex]) {
					actionObservers.get(observerIndex).onInputAction(actionsEnumArray[actionIndex]);
				}
			}
		}
	}

	public void addActionObserver(InputActionObserver observer) {
		assert !actionObservers.contains(observer) : "Attempted to add input action observer " + observer.toString() + ", but it is already observing the input manager.";
		actionObservers.add(observer);
	}

	public void removeActionObserver(InputActionObserver observer) {
		if (!actionObservers.remove(observer)) {
			throw new GameLogicException("Attempted to remove input action observer " + observer.toString() + ", but it was not found.");
		}
	}

	public void addInputEventObserver(InputProcessor observer) {
		assert !inputEventObservers.contains(observer) : "Attempted to add input event observer " + observer.toString() + ", but it is already observing the input manager.";
		inputEventObservers.add(observer);
	}

	public void removeInputEventObserver(InputProcessor observer) {
		if (!inputEventObservers.remove(observer)) {
			throw new GameLogicException("Attempted to remove input event observer " + observer.toString() + ", but it was not found.");
		}
	}


	/* InputProcessor methods */

	@Override
	public boolean keyDown(int keycode) {
		for (var observer : inputEventObservers) {
			observer.keyDown(keycode);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		for (var observer : inputEventObservers) {
			observer.keyUp(keycode);
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		for (var observer : inputEventObservers) {
			observer.keyTyped(character);
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		for (var observer : inputEventObservers) {
			observer.touchDown(screenX, screenY, pointer, button);
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		for (var observer : inputEventObservers) {
			observer.touchUp(screenX, screenY, pointer, button);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		for (var observer : inputEventObservers) {
			observer.touchDragged(screenX, screenY, pointer);
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		for (var observer : inputEventObservers) {
			observer.mouseMoved(screenX, screenY);
		}
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		for (var observer : inputEventObservers) {
			observer.scrolled(amountX, amountY);
		}
		return false;
	}

	/* End InputProcessor methods */
}
