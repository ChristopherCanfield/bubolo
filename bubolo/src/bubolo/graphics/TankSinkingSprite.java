package bubolo.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;

import bubolo.util.Time;
import bubolo.world.Tank;

public class TankSinkingSprite extends Sprite {

	private float x;
	private float y;
	private float rotation;

	private static final int drownTimeTicks = Time.secondsToTicks(1.25f);
	private int ticksRemaining = drownTimeTicks;

	private static final String textureFile = "tank.png";
	private final Texture texture;
	private final TextureRegion[] frames;

	/**
	 * Constructs a tank sinking animation, which has the tank scale down until it disappears.
	 *
	 * @param tank the tank that is drowning.
	 */
	TankSinkingSprite(Tank tank) {
		super(DrawLayer.EFFECTS);

		this.x = tank.x();
		this.y = tank.y();
		this.rotation = tank.rotation();

		this.texture = Graphics.getTexture(textureFile);
		this.frames = TextureUtil.splitFrames(texture, 32, 32)[0];
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

		drawTexture(graphics, frames[0], scale);
	}
}
