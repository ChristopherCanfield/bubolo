package bubolo.world;

import bubolo.Config;
import bubolo.util.Units;

/**
 * Game world objects that never move, and that do not have intelligence.
 *
 * The primary differences between StaticEntities and ActorEntities are:
 * - StaticEntities can't be moved after construction, because their position is final.
 * - StaticEntities don't have a public update method that is called by the world each game tick.
 * - StaticEntities don't have a settable rotation.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public abstract class StaticEntity extends Entity {
	private final short x;
	private final short y;

	/**
	 * @param args id != null; x >= 0 && <= Config.MaxWorldX; y >= 0 && <= Config.MaxWorldY.
	 * @param width the entity's width. > 0 && <= 127.
	 * @param height the entity's height. > 0 && <= 127.
	 */
	protected StaticEntity(ConstructionArgs args, int width, int height) {
		super(args.id(), width, height);

		assert args.x() >= 0;
		assert args.x() <= Config.MaxWorldX;
		assert args.y() >= 0;
		assert args.y() <= Config.MaxWorldY;


		this.x = (short) args.x();
		this.y = (short) args.y();
	}

	@Override
	public float x() {
		// Make unsigned.
		return x & 0xffff;
	}

	@Override
	public float y() {
		// Make unsigned.
		return y & 0xffff;
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
		return (int) x() / Units.TileToWorldScale;
	}

	/**
	 * @return the world row that the object is in.
	 */
	@Override
	public int tileRow() {
		return (int) y() / Units.TileToWorldScale;
	}
}
