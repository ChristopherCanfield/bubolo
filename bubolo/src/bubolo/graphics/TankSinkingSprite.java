package bubolo.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;

import bubolo.util.Time;
import bubolo.world.Tank;

class TankSinkingSprite extends Sprite {

	private final float x;
	private final float y;
	private final float rotation;

	private static final int drownTimeTicks = Time.secondsToTicks(1.25f);
	private int ticksRemaining = drownTimeTicks;

	private static final String textureFile = "tank.png";
	private final Color tankColor;
	private final TextureRegion bodyFrame;
	private final TextureRegion treadsFrame;

	/**
	 * Constructs a tank sinking animation, which has the tank scale down until it disappears.
	 *
	 * @param tank the tank that is drowning.
	 */
	TankSinkingSprite(Tank tank) {
		super(DrawLayer.Effects);

		this.x = tank.x();
		this.y = tank.y();
		this.rotation = tank.rotation();

		this.tankColor = tank.playerColor().color;
		TextureRegion[][] texture = Graphics.getTextureRegion2d(textureFile, 32, 32);
		this.bodyFrame = texture[0][1];
		this.treadsFrame = texture[0][0];
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public int getWidth() {
		return 32;
	}

	@Override
	public int getHeight() {
		return 32;
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	protected boolean isDisposed() {
		return ticksRemaining <= 0;
	}

	@Override
	void draw(Graphics graphics) {
		ticksRemaining--;
		float percentTimeRemaining = ticksRemaining / (float) drownTimeTicks;
		float scale = Interpolation.pow2In.apply(0, 1, percentTimeRemaining);

		setColor(tankColor);
		drawTexture(graphics, bodyFrame, scale);
		setColor(Color.WHITE);
		drawTexture(graphics, treadsFrame, scale);
	}
}
