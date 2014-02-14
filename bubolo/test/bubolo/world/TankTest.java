package bubolo.world;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import bubolo.graphics.Graphics;
import bubolo.graphics.LibGdxAppTester;
import bubolo.world.Entity;
import bubolo.world.Tank;

public class TankTest
{

	static boolean isComplete = false;
	static Tank tank;
	static final UUID TARGET_UUID = UUID.fromString("5231b533-ba17-4787-98a3-f2df37de2aD7"); // random
	// UUID
	// string
	static final float TARGET_X = 26.7f;
	static final float TARGET_Y = 72.5f;
	static final float TARGET_ROT = (float) Math.PI / 2;
	static final int TARGET_WIDTH = 50;
	static final int TARGET_HEIGHT = 100;

	/**
	 * An OpenGL context must be created so that the textures for the Tank object can load properly.
	 * Without this, all tests will crash on Tank construction.
	 */
	@BeforeClass
	public static void setUpApp()
	{
		LibGdxAppTester.createApp();
		isComplete = false;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run()
			{
				tank = new Tank(TARGET_X, TARGET_Y, TARGET_WIDTH, TARGET_HEIGHT, TARGET_ROT, TARGET_UUID);

				isComplete = true;
			}
		});

		while (!isComplete)
		{
			Thread.yield();
		}

	}
	
	@Test
	public void constructTank_NO_UUID()
	{
		Tank tank2 = new Tank();
	}

	@Test
	public void constructTank_UUID_ONLY()
	{
		Tank tank2 = new Tank(TARGET_UUID);
		assertEquals("Grass param constructor without UUID sets fields correctly,", tank.getId(), tank2.getId());
	}

	@Test
	public void constructTank_PARAM_NO_UUID()
	{
		Tank tank2 = new Tank(TARGET_X, TARGET_Y, TARGET_WIDTH, TARGET_HEIGHT, TARGET_ROT);
		assertEquals("Tank param constructor without UUID sets fields correctly,", true, EntityTestCase.matches_NO_UUID(tank, tank2));
	}
	
	@Test
	public void checkSprite(){
		assertNotEquals("Tank is not using the default entity sprite.", tank.getSpriteId(), new DummyEntity().getSpriteId());
	}
}
