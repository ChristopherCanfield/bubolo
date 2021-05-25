package bubolo.world;

/**
 * Movement speed on grass is normal.
 *
 * @author BU CS673 - Clone Productions
 */
public class Grass extends Terrain {
	private static final TerrainTravelSpeed terrainTravelSpeed = TerrainTravelSpeed.Normal;

	private static final int width = 32;
	private static final int height = 32;

	protected Grass(ConstructionArgs args, World world) {
		super(args, width, height, terrainTravelSpeed);
	}
}
