package bubolo.audio;


/**
 * The game's sound effects. Sound effects can be played in the follow way:<br><br>
 * <code>
 * Audio.play(Sfx.CANNON_FIRED);
 * </code>
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public enum Sfx
{
	CannonFired("cannon_fired.ogg", 1, 0.75f, 1.25f),
	MineExplosion("mine_explosion.ogg", 1, 0.75f, 1.25f),
	TankExplosion("tank_explosion.wav", 1, 0.75f, 1.25f),
	TankHit("tank_explosion.wav", 1, 0.75f, 1.25f),
	TankDrowned("bubbles.ogg", 1, 1, 1),
	PillboxHit("pillbox_hit.ogg", 1, 0.75f, 1.25f),
	TreeHit("tree_hit.ogg", 1, 0.75f, 1.25f),
	WallHit("wall_hit.ogg", 1, 0.75f, 1.25f);

	final String fileName;
	// Multiplied by the main volume.
	final float volumeAdjustment;
	final float pitchRangeMin;
	final float pitchRangeMax;

	private Sfx(String fileName, float volumeAdjustment, float pitchRangeMin, float pitchRangeMax) {
		this.fileName = fileName;
		this.volumeAdjustment = volumeAdjustment;
		this.pitchRangeMin = pitchRangeMin;
		this.pitchRangeMax = pitchRangeMax;
	}
}
