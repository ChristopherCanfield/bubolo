package bubolo.audio;

/**
 * Limits the rate that that sound effects are played. Most useful for damage sounds, to prevent cascading duplicate sound effects.
 *
 * @author Christopher D. Canfield
 */
public class SfxRateLimiter {
	private final int rateLimitMillis;
	private long nextSfxPlayTime;

	public SfxRateLimiter(int rateLimitMillis) {
		this.rateLimitMillis = rateLimitMillis;
	}

	/**
	 * Plays the sound effect if the current time > (last play time + rate limit millis).
	 *
	 * @param soundEffect the sound effect to play.
	 * @param x the x position in the world.
	 * @param y the y position in the world.
	 * @return whether the sound effect was played or not.
	 */
	public boolean play(Sfx soundEffect, float x, float y) {
		if (nextSfxPlayTime < System.currentTimeMillis()) {
			Audio.play(soundEffect, x, y);
			nextSfxPlayTime = System.currentTimeMillis() + rateLimitMillis;
			return true;
		}
		return false;
	}
}
