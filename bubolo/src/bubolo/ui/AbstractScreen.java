package bubolo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

import bubolo.graphics.Graphics;
import bubolo.ui.gui.GuiGroup;
import bubolo.ui.gui.UiComponent.ClickedObjectInfo;
import bubolo.ui.gui.UiComponent.HoveredObjectInfo;

/**
 * Base class for screens that use UiComponents. AbstractScreen provides the following functionality:
 * <ul>
 *	<li>It implements Screen and InputProcessor.</li>
 * 	<li>It comes with a base GuiGroup, named root.</li>
 * 	<li>It sets itself as the LibGdx input processor using Gdx.input.setInputProcessor.</li>
 * 	<li>It forwards keyDown, keyType, touchUp (click), mouseMoved, and viewportResized events.</li>
 * 	<li>It draws all components attached to the root GUI group.</li>
 * 	<li>It removes itself as the InputProcessor when it is disposed, and disposes all components attached to root.</li>
 * 	<li>It provides hooks into many events.</li>
 * </ul>
 *
 * @author Christopher D. Canfield
 */
public abstract class AbstractScreen implements Screen, InputProcessor {
	protected final GuiGroup root = new GuiGroup();

	private boolean handleInputEvents = true;

	protected AbstractScreen() {
		Gdx.input.setInputProcessor(this);
	}

	/**
	 * When set to true, which is the default, input events are enabled.
	 */
	protected final void setInputEventsEnabled(boolean val) {
		this.handleInputEvents = val;
	}

	@Override
	public final boolean keyDown(int keycode) {
		if (handleInputEvents) {
			root.onKeyDown(keycode);
			onKeyDown(keycode);
		}
		return false;
	}

	protected void onKeyDown(int keycode) {}

	@Override
	public final boolean keyTyped(char character) {
		if (handleInputEvents) {
			root.onKeyTyped(character);
			onKeyTyped(character);
		}
		return false;
	}

	protected void onKeyTyped(char character) {}

	@Override
	public final boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (handleInputEvents) {
			var info = root.onMouseClicked(screenX, screenY);
			if (info != null) {
				onMouseClickedObject(info);
			}
		}
		return false;
	}

	protected void onMouseClickedObject(ClickedObjectInfo clickedObjectInfo) {}

	@Override
	public final boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (handleInputEvents) {
			onMouseButtonDown(screenX, screenY, pointer, button);
		}
		return false;
	}

	protected void onMouseButtonDown(int screenX, int screenY, int pointer, int button) {
	}

	@Override
	public final boolean mouseMoved(int screenX, int screenY) {
		if (handleInputEvents) {
			var info = root.onMouseMoved(screenX, screenY);
			if (info != null) {
				onMouseHoveredOverObject(info);
			}
		}
		return false;
	}

	protected void onMouseHoveredOverObject(HoveredObjectInfo hoveredObjectInfo) {}

	@Override
	public final boolean keyUp(int keycode) {
		if (handleInputEvents) {
			onKeyUp(keycode);
		}
		return false;
	}

	protected void onKeyUp(int keycode) {
	}

	@Override
	public final boolean touchDragged(int screenX, int screenY, int pointer) {
		if (handleInputEvents) {
			onMouseDragged(screenX, screenY, pointer);
		}
		return false;
	}

	protected void onMouseDragged(int screenX, int screenY, int pointer) {
	}

	@Override
	public final boolean scrolled(float amountX, float amountY) {
		if (handleInputEvents) {
			onMouseWheelScrolled(amountX, amountY);
		}
		return false;
	}

	protected void onMouseWheelScrolled(float amountX, float amountY) {
	}

	@Override
	public final void draw(Graphics graphics) {
		preDraw(graphics);
		root.draw(graphics);
		postDraw(graphics);
	}

	/**
	 * Called before the root group is drawn, but after the screen has been cleared.
	 *
	 * @param graphics the game's graphics system.
	 */
	protected void preDraw(Graphics graphics) {
	}

	/**
	 * Called after the root group is drawn.
	 *
	 * @param graphics the game's graphics system.
	 */
	protected void postDraw(Graphics graphics) {
	}

	@Override
	public final void viewportResized(int newWidth, int newHeight) {
		root.recalculateLayout(newWidth, newHeight);
		onViewportResized(newWidth, newHeight);
	}

	protected void onViewportResized(int newWidth, int newHeight) {
	}

	@Override
	public final void dispose() {
		setInputEventsEnabled(false);
		if (Gdx.input.getInputProcessor() == this) {
			Gdx.input.setInputProcessor(null);
		}
		root.dispose();

		onDispose();
	}

	protected void onDispose() {
	}
}
