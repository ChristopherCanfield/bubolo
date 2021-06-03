package bubolo.world;

public class Building extends StaticEntity implements TerrainImprovement {
	private static final int width = 20;
	private static final int height = 20;

	protected Building(ConstructionArgs args, World world) {
		super(args, width, height);
	}

	@Override
	public boolean isValidBuildTarget() {
		return false;
	}
}
