package bubolo.graphics;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Test for BackgroundSprite
 */
public class BackgroundSpriteTest
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
	public void drawSprite()
	{
		isComplete = false;
		passed = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				Sprite sprite = new BackgroundSprite(1, 1);

				try {
					batch.begin();
					sprite.draw(batch, camera);
					passed = true;
				} catch (Exception e) {
					e.printStackTrace();
					passed = false;
				} finally {
					batch.end();
					isComplete = true;
				}
			}
		});

		while (!isComplete)
		{
			Thread.yield();
		}

		assertTrue(passed);
	}

	@Test
	public void getX()
	{
		isComplete = false;
		passed = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				Sprite sprite = new BackgroundSprite(1, 1);
				passed = (sprite.getX() == 1.f);
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
	public void getY()
	{
		isComplete = false;
		passed = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				Sprite sprite = new BackgroundSprite(1, 2);
				passed = (sprite.getY() == 2.f);
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
	public void getHeight()
	{
		isComplete = false;
		passed = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				Sprite sprite = new BackgroundSprite(1, 1);
				passed = (sprite.getHeight() == BackgroundSprite.HEIGHT);
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
		isComplete = false;
		passed = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				Sprite sprite = new BackgroundSprite(1, 1);
				passed = (sprite.getRotation() == 0.f);
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
	public void getWidth()
	{
		isComplete = false;
		passed = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				Sprite sprite = new BackgroundSprite(1, 1);
				passed = (sprite.getWidth() == BackgroundSprite.WIDTH);
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
	public void isEntityDisposed()
	{
		isComplete = false;
		passed = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				Sprite sprite = new BackgroundSprite(1, 1);
				passed = (sprite.isDisposed() == false);
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
