package bubolo.graphics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import bubolo.world.entity.concrete.Spawn;

public class SpawnSpriteTest
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
					Sprite sprite = Sprites.getInstance().createSprite(new Spawn());

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
	public void drawSpawnSprite()
	{
		isComplete = false;
		passed = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				Sprite sprite = Sprites.getInstance().createSprite(new Spawn());
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
	public void getSetVisible()
	{
		SpawnSprite sprite = new SpawnSprite(null);
		assertFalse(sprite.getVisible());

		sprite.setVisible(true);
		assertTrue(sprite.getVisible());

		sprite.setVisible(false);
		assertFalse(sprite.getVisible());
	}
}
