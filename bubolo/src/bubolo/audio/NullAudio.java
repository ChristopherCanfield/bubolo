package bubolo.audio;

/**
 * An audio implementation which contains no-ops. Intended for testing.
 *
 * @author Christopher D. Canfield
 */
public class NullAudio implements Audio {

	@Override
	public void setListenerPosition(float x, float y) {
	}

	@Override
	public void setListenerDistanceToDeepWater(float distanceWorldUnits) {
	}

	@Override
	public void play(Sfx soundEffect, float x, float y) {
	}

	@Override
	public void setSoundEffectVolume(float volume) {
	}

	@Override
	public float soundEffectVolume() {
		return 0;
	}

	@Override
	public void dispose() {
	}

}
