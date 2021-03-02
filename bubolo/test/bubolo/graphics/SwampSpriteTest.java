package bubolo.graphics;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import bubolo.world.entity.concrete.Swamp;

public class SwampSpriteTest
{
	private SpriteBatch batch;
	private Camera camera;

	private boolean isComplete;
	private boolean passed;

	@Before
	public void setUp()
	{
		LibGdxAppTester.createApp();

		Gdx.app.postRunnable(new Runnable() {
			@Override public void run() {
				batch = new SpriteBatch();
				camera = new OrthographicCamera(100, 100);
				Graphics g = new Graphics(50, 500);
			}
		});
	}

	@Test
	public void constructSwampSprite() throws InterruptedException
	{
		synchronized(LibGdxAppTester.getLock())
		{
			isComplete = false;
			passed = false;

			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run()
				{
					// Fails if the constructor throws an exception.
					Sprite sprite = Sprites.getInstance().createSprite(new Swamp());

					passed = true;
					isComplete = true;
				}
			});

			while (!isComplete)
			{
				Thread.yield();
			}

			assertTrue(passed);
		}
	}

	@Test
	public void drawSwampSprite()
	{
		isComplete = false;
		passed = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				Sprite sprite = Sprites.getInstance().createSprite(new Swamp());
				batch.begin();
				sprite.draw(batch, camera);
				passed = true;
				isComplete = true;
			}
		});

		while (!isComplete)
		{
			Thread.yield();
		}

		assertTrue(passed);
	}

	@Test
	public void getRotation()
	{
		Sprite sprite = Sprites.getInstance().createSprite(new Swamp());
		boolean check;
		check = (sprite.getRotation() == 0 || sprite.getRotation() == (float) (Math.PI/2) || sprite.getRotation() == (float) (Math.PI) ||
				sprite.getRotation() == (float) (3 * Math.PI / 2));
		assertTrue(check);
	}
}
