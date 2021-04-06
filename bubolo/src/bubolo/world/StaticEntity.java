package bubolo.world;

import bubolo.util.Coords;

/**
 * Game world objects that never move, and that do not have intelligence.
 *
 * The primary differences between StaticEntities and ActorEntities are:
 * - StaticEntities can't be moved after construction, because their position is final.
 * - StaticEntities don't have a public update method that is called by the world each game tick.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public abstract class StaticEntity extends Entity {
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

	/**
	 * @return the world column that the object is in.
	 */
	@Override
	public int tileColumn() {
		return (int) x() / Coords.TileToWorldScale;
	}

	/**
	 * @return the world row that the object is in.
	 */
	@Override
	public int tileRow() {
		return (int) y() / Coords.TileToWorldScale;
	}
}
