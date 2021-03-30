package bubolo.world;

import java.util.UUID;

/**
 * Game world objects
 *
 * @author Christopher D. Canfield
 */
public class Terrain extends StaticEntity {

	private final float speedModifier;
	private TerrainImprovement improvement;

	protected Terrain(ConstructionArgs args, int width, int height, float speedModifier) {
		this(args.id(), args.x(), args.y(), width, height, speedModifier);
	}

	protected Terrain(UUID id, float x, float y, int width, int height, float speedModifier) {
		super(id, x, y, width, height);
		this.speedModifier = speedModifier;
	}

	public float speedModifier() {
		return speedModifier;
	}

	public TerrainImprovement improvement() {
		return improvement;
	}

	public void setImprovement(TerrainImprovement improvement) {
		this.improvement = improvement;
	}
}
