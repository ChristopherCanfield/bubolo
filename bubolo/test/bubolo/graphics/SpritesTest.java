package bubolo.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.Gdx;

import bubolo.world.entity.concrete.Grass;
import bubolo.world.entity.concrete.Road;
import bubolo.world.entity.concrete.Tank;
import bubolo.world.entity.concrete.Tree;

public class SpritesTest
{
	private static boolean isComplete;
	private static boolean hadException;

	@Before
	public void setUp()
	{
		LibGdxAppTester.createApp();
	}

	@Test
	public void spriteCreateTank() throws InterruptedException
	{
		isComplete = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					SpriteSystem spriteSystem = SpriteSystem.getInstance();
					int spriteCount = spriteSystem.getSprites().size();

					Sprite sprite1 = spriteSystem.createSprite(new Tank());
					Sprite sprite2 = spriteSystem.getSprites().get(spriteCount);
					assertNotNull(sprite1);
					assertNotNull(sprite2);
					assertSame(sprite1, sprite2);
					hadException = false;
				}
				catch (Exception e)
				{
					hadException = true;
				}
				isComplete = true;
			}
		});

		while (!isComplete)
		{
			Thread.yield();
		}
		assertFalse("Exception thrown when creating sprite.", hadException);
	}

	@Test
	public void spriteRemove() throws InterruptedException
	{
		isComplete = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					SpriteSystem spriteSystem = SpriteSystem.getInstance();
					int spriteCount = spriteSystem.getSprites().size();

					spriteSystem.createSprite(new Tank());
					Sprite sprite = spriteSystem.getSprites().get(spriteCount);
					assertNotNull(sprite);

					spriteSystem.removeSprite(sprite);
					assertEquals(spriteSystem.getSprites().size(), spriteSystem.getSprites().size());

					hadException = false;
				}
				catch (Exception e)
				{
					hadException = true;
				}
				isComplete = true;
			}
		});

		while (!isComplete)
		{
			Thread.yield();
		}
		assertFalse("Exception thrown when creating sprite.", hadException);
	}

	@Test
	public void spritesRemove() throws InterruptedException
	{
		isComplete = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					SpriteSystem spriteSystem = SpriteSystem.getInstance();
					int spriteCount = spriteSystem.getSprites().size();

					spriteSystem.addSprite(new BulletExplosionSprite(0, 0));
					Sprite sprite = spriteSystem.getSprites().get(spriteCount);
					assertNotNull(sprite);

					hadException = false;
				}
				catch (Exception e)
				{
					hadException = true;
				}
				isComplete = true;
			}
		});

		while (!isComplete)
		{
			Thread.yield();
		}
		assertFalse("Exception thrown when creating sprite.", hadException);
	}

	@Test
	public void spriteCreateTree() throws InterruptedException
	{
		isComplete = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					Sprite sprite = SpriteSystem.getInstance().createSprite(new Tree());
					assertNotNull(sprite);
					hadException = false;
				}
				catch (Exception e)
				{
					hadException = true;
				}
				isComplete = true;
			}
		});

		while (!isComplete)
		{
			Thread.yield();
		}
		assertFalse("Exception thrown when creating sprite.", hadException);
	}

	@Test
	public void spriteCreateGrass() throws InterruptedException
	{
		isComplete = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					Sprite sprite = SpriteSystem.getInstance().createSprite(new Grass());
					assertNotNull(sprite);
					hadException = false;
				}
				catch (Exception e)
				{
					hadException = true;
				}
				isComplete = true;
			}
		});

		while (!isComplete)
		{
			Thread.yield();
		}
		assertFalse("Exception thrown when creating sprite.", hadException);
	}

	@Test
	public void spriteCreateRoad() throws InterruptedException
	{
		isComplete = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					Sprite sprite = SpriteSystem.getInstance().createSprite(new Road());
					assertNotNull(sprite);
					hadException = false;
				}
				catch (Exception e)
				{
					hadException = true;
				}
				isComplete = true;
			}
		});

		while (!isComplete)
		{
			Thread.yield();
		}
		assertFalse("Exception thrown when creating sprite.", hadException);
	}
}