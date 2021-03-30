package bubolo.world;

/**
 * Game world objects that never move, and that do not have intelligence.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public class StaticEntity extends Entity {
	private final float x;
	private final float y;

	protected StaticEntity(ConstructionArgs args, int width, int height) {
		super(args.id(), width, height);

		this.x = args.x();
		this.y = args.y();
	}

	@Override
	public float x() {
		return x;
	}

	@Override
	public float y() {
		return y;
	}

	@Override
	public float rotation() {
		return (float) (Math.PI / 2.0);
	}
}
