package bubolo.world;

/**
 * Affects the tank's acceleration and max speed.
 *
 * @author Christopher D. Canfield
 */
public enum TerrainTravelSpeed {
	VerySlow(0.25f),
	Slow(0.5f),
	Normal(1.0f),
	Fast(1.5f);

	final float speedModifier;

	private TerrainTravelSpeed(float modifier) {
		this.speedModifier = modifier;
	}
}
