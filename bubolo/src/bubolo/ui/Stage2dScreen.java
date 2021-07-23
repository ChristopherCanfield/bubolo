package bubolo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import bubolo.graphics.Graphics;

/**
 * Base class for libGDX stage 2d ui screens that are drawn in the LWJGL window.
 *
 * @author Christopher D. Canfield
 * @param <T> the screen's root UI component type.
 * @since 0.3.0
 */
public abstract class Stage2dScreen<T extends WidgetGroup> implements Screen {
	/** The scene2d.ui stage. **/
	protected final Stage stage;

	protected final T root;

	private final Color clearColor = new Color(0.45f, 0.45f, 0.45f, 1);

	/**
	 * Default constructor.
	 */
	protected Stage2dScreen(Graphics graphics, T root) {
		Viewport viewport = new ScalingViewport(Scaling.none, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera());

		stage = new Stage(viewport, graphics.nonScalingBatch());

		this.root = root;
		stage.addActor(root);
		root.setFillParent(true);
		stage.getRoot().setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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
			root.debug();
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
	public void viewportResized(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	/**
	 * Releases all heavy-weight resources.
	 */
	@Override
	public final void dispose() {
		if (Gdx.input.getInputProcessor() == this) {
			Gdx.input.setInputProcessor(null);
		}
		onDispose();
	}

	protected abstract void onDispose();
}
