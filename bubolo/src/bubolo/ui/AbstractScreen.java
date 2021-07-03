package bubolo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

import bubolo.graphics.Graphics;
import bubolo.ui.gui.GuiGroup;
import bubolo.ui.gui.GuiGroup.ClickedObjectInfo;
import bubolo.ui.gui.GuiGroup.HoveredObjectInfo;

/**
 * Base class for screens that use UiComponents. AbstractScreen provides the following functionality:
 * <ul>
 *	<li>It implements Screen and InputProcessor.</li>
 * 	<li>It comes with a base GuiGroup, named root.</li>
 * 	<li>It sets itself as the LibGdx input processor using Gdx.input.setInputProcessor.</li>
 * 	<li>It forwards keyDown, keyType, touchUp (click), mouseMoved, and viewportResized events.</li>
 * 	<li>It draws all components attached to the root gui group.</li>
 * 	<li>It removes iteself as the InputProcessor when it is disposed, and disposes all components attached to root.</li>
 * 	<li>It provides hooks into many events.</li>
 * </ul>
 *
 * @author Christopher D. Canfield
 */
public abstract class AbstractScreen implements Screen, InputProcessor {
	protected final GuiGroup root = new GuiGroup();

	protected AbstractScreen() {
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public final boolean keyDown(int keycode) {
		root.onKeyDown(keycode);
		onKeyDown(keycode);
		return false;
	}

	protected void onKeyDown(int keycode) {}

	@Override
	public final boolean keyTyped(char character) {
		root.onKeyTyped(character);
		return false;
	}

	protected void onKeyTyped(char character) {}

	@Override
	public final boolean touchUp(int screenX, int screenY, int pointer, int button) {
		var info = root.onMouseClicked(screenX, screenY);
		if (info != null) {
			onMouseClickedObject(info);
		}
		return false;
	}

	protected void onMouseClickedObject(ClickedObjectInfo clickedObjectInfo) {}

	@Override
	public final boolean mouseMoved(int screenX, int screenY) {
		var info = root.onMouseMoved(screenX, screenY);
		if (info != null) {
			onMouseHoveredOverObject(info);
		}
		return false;
	}

	protected void onMouseHoveredOverObject(HoveredObjectInfo hoveredObjectInfo) {}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}

	@Override
	public final void draw(Graphics graphics) {
		preDraw(graphics);
		root.draw(graphics);
		postDraw(graphics);
	}

	protected abstract void preDraw(Graphics graphics);
	protected abstract void postDraw(Graphics graphics);

	@Override
	public void viewportResized(int newWidth, int newHeight) {
		root.recalculateLayout(newWidth, newHeight);
	}

	protected void onViewportResized(int newWidth, int newHeight) {
	}

	@Override
	public final void dispose() {
		if (Gdx.input.getInputProcessor() == this) {
			Gdx.input.setInputProcessor(null);
		}
		root.dispose();

		onDispose();
	}

	protected void onDispose() {
	}
}
