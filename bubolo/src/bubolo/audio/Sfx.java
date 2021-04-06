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
	CANNON_FIRED("cannon_fired.ogg"),
	MINE_EXPLOSION("mine_explosion.wav"),
	TANK_EXPLOSION("tank_explosion.wav"),
	TANK_HIT("tank_explosion.wav"),
	PILLBOX_HIT("pillbox_hit.wav"),
	TREE_HIT("tree_hit.wav"),
	WALL_HIT("wall_hit.wav");

	final String fileName;

	private Sfx(String fileName) {
		this.fileName = fileName;
	}
}
