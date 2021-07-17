package bubolo.audio;

public interface Audio {
	/**
	 * Sets the the audio listener's position, in world units.
	 *
	 * @param x the x position to move the audio listener to.
	 * @param y the y position to move the audio listener to.
	 */
	void setListenerPosition(float x, float y);

	/**
	 * Sets the audio listener's distance to the nearest deep water.
	 *
	 * @param distanceWorldUnits the audio listener's distance to the nearest deep water, in world units.
	 */
	void setListenerDistanceToDeepWater(float distanceWorldUnits);

	/**
	 * Plays a sound effect. This should be called in the following way:<br>
	 * <br>
	 * <code>Audio.play(Sfx.TankExplosion, sourceWorldX, sourceWorldY);<br>
	 * Audio.play(Sfx.TankHit, sourceWorldX, sourceWorldY);</code>
	 *
	 * @param soundEffect the sound effect to play.
	 * @param x the source's x world position.
	 * @param y the source's y world position.
	 */
	void play(Sfx soundEffect, float x, float y);

	/**
	 * Sets the sound effect volume, from 0 (mute) to 1 (max volume).
	 *
	 * @param volume the new sound effect volume, ranging from 0 to 1.
	 * @throws IllegalArgumentException if volume is less than 0 or greater than 1.
	 */
	void setSoundEffectVolume(float volume);

	/**
	 * Gets the sound effect volume, in the range [0, 1].
	 *
	 * @return the sound effect volume.
	 */
	float soundEffectVolume();

	/**
	 * Shuts down and cleans up the audio system.
	 */
	void dispose();
}