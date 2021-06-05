package bubolo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import bubolo.graphics.Graphics;

/**
 * Base class for libGDX stage 2d ui screens that are drawn in the LWJGL window.
 *
 * @author Christopher D. Canfield
 * @since 0.3.0
 */
public abstract class Stage2dScreen implements Screen {
	/** The scene2d.ui stage. **/
	protected final Stage stage;

	/** The base table for the screen. **/
	protected final Table table;

	private final Color clearColor = new Color(0.45f, 0.45f, 0.45f, 1);

	/**
	 * Default constructor.
	 */
	protected Stage2dScreen() {
		stage = new Stage();
		table = new Table();

		table.setFillParent(true);
		table.top();
		stage.addActor(table);

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public Color clearColor() {
		return clearColor;
	}

	/**
	 * Updates and draws the screen.
	 */
	@Override
	public final void draw(Graphics graphics) {
		draw(false);
	}

	/**
	 * Updates and draws the screen.
	 *
	 * @param debug true if the screen should be drawn in debug mode.
	 */
	public final void draw(boolean debug) {
		if (debug) {
			table.debug();
		}

		stage.act();
		onUpdate();
		stage.draw();
	}

	/**
	 * Called once per tick. Child classes can override this if necessary.
	 */
	protected void onUpdate() {
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	/**
	 * Releases all heavy-weight resources.
	 */
	@Override
	public abstract void dispose();
}
