package bubolo.world;

/**
 * Affects the tank's acceleration and max speed.
 *
 * @author Christopher D. Canfield
 */
public enum TerrainTravelSpeed {
	VerySlow(0.25f, 0.3f),
	Slow(0.55f, 0.45f),
	Normal(1.0f, 0.8f),
	Fast(1.5f, 1.0f);

	final float accelerationModifier;
	final float maxSpeedModifier;

	private TerrainTravelSpeed(float accelerationModifier, float maxSpeedModifier) {
		this.accelerationModifier = accelerationModifier;
		this.maxSpeedModifier = maxSpeedModifier;
	}
}
