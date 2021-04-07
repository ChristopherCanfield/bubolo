package bubolo.audio;


import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import bubolo.Config;
import bubolo.util.GameLogicException;

/**
 * The top-level class for the Sound system.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Audio implements Music.OnCompletionListener
{
	private static final Logger logger = Logger.getLogger(Config.AppProgramaticTitle);

	/**
	 * Instances of this class should not be directly constructed.
	 */
	private Audio() {}

	/**
	 * The path to the music files.
	 */
	public static final String MUSIC_PATH = "res/music/";

	/**
	 * The path to the sound effect files.
	 */
	public static final String SFX_PATH = "res/sfx/";

	// The sound effects volume. The default is 75%.
	private static float soundEffectVolume = 0.75f;
	// The music volume. The default is 75%.
	private static float musicVolume = 0.75f;

	// A list of all music files.
	private static List<Music> music;
	// The index of the currently playing music file, or -1 if no music is playing.
	private static int currentMusicFile = -1;

	// The music on completion listener. This is used when a song has finished playing.
	private static Music.OnCompletionListener musicOnCompletionListener = new Audio();

	private static final Map<Sfx, Sound> soundEffects = new HashMap<>();

	private static boolean initialized = false;

	private static final Random random = new Random();

	/**
	 * Initializes the sound system.
	 */
	public static void initialize()
	{
		initialized = true;
		preloadCoreSoundEffects();
	}

	/**
	 * Plays a sound effect. This should be called in the following way:<br><br>
	 * <code>Audio.play(Sfx.EXPLOSION);<br>
	 * Audio.play(Sfx.TANK_HIT);</code>
	 * @param soundEffect the sound effect to play.
	 */
	public static void play(Sfx soundEffect)
	{
		if (initialized) {
			Sound sound = getSoundEffect(soundEffect);
			long id = sound.play(soundEffectVolume);
			sound.setPitch(id, getRandomPitch(0.8f, 1.2f));
		} else {
			logger.warning("Audio.play called before audio system was initialized.");
		}
	}

	private static float getRandomPitch(float min, float max) {
		float adjustment = random.nextFloat() * (max - min);
		return max - adjustment;
	}

	/**
	 * Starts the music. The audio system will continuously loop through all songs
	 * until <code>Audio.stopMusic()</code> is called.
	 */
	public static void startMusic()
	{
		if (initialized) {
			if (music == null)
			{
				loadMusic();
				if (music.size() < 2)
				{
					throw new GameLogicException("At least two songs must be specified.");
				}
			}

			currentMusicFile = 0;
			music.get(currentMusicFile).setVolume(musicVolume);
			music.get(currentMusicFile).play();
			music.get(currentMusicFile).setOnCompletionListener(musicOnCompletionListener);
		} else {
			logger.warning("Audio.startMusic called before audio system was initialized.");
		}
	}

	/**
	 * Loads all music files. Note that music files aren't actually stored in memory, unlike
	 * sound effect files. Instead, they are streamed from disk as needed.
	 */
	private static void loadMusic()
	{
		music = new ArrayList<Music>();

		try
		{
			music.add(Gdx.audio.newMusic(new FileHandle(new File(MUSIC_PATH + "Lessons-8bit.mp3"))));
			music.add(Gdx.audio.newMusic(new FileHandle(new File(MUSIC_PATH + "bolo_menu.mp3"))));
		}
		catch (Exception e)
		{

			System.out.println(e);
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * ***Testing only***
	 * Provides a hook to the Music OnCompletionListener for testing.
	 */
	static void testMusicOnCompletionListener()
	{
		if (music == null)
		{
			loadMusic();
		}

		musicOnCompletionListener.onCompletion(null);
	}

	/**
	 * Callback that is invoked when a music stream has completed.
	 */
	@Override
	public void onCompletion(Music completedMusic)
	{
		// Plays a randomly selected next song. The completed song will not be played multiple
		// times in a row. Note that this will not work with only one song.
		int nextSong = -1;
		while (nextSong == -1 || nextSong == currentMusicFile)
		{
			nextSong = (new Random()).nextInt(music.size());
		}

		music.get(nextSong).setVolume(musicVolume / 100.f);
		music.get(nextSong).play();

		setMusicFileIndex(nextSong);
	}

	private static void setMusicFileIndex(int index)
	{
		checkArgument(index >= 0, "Song index must be greater than zero: %s", index);
		checkArgument(index < music.size(), "song index exceeds music file count: %s", index);

		currentMusicFile = index;
	}

	/**
	 * Stops the music. There is no effect is no music is currently being played.
	 */
	public static void stopMusic()
	{
		if (music != null && currentMusicFile != -1)
		{
			music.get(currentMusicFile).stop();
			currentMusicFile = -1;
		}
	}

	/**
	 * Sets the sound effect volume, from 0 (mute) to 1 (max volume).
	 * @param volume the new sound effect volume, ranging from 0 to 1.
	 * @throws IllegalArgumentException if volume is less than 0 or greater than 1.
	 */
	public static void setSoundEffectVolume(float volume)
	{
		checkArgument(volume >= 0, "Sound effect volume was less than zero: %s", volume);
		checkArgument(volume <= 1, "Sound effect volume was greater than one: %s", volume);

		soundEffectVolume = volume;
	}

	/**
	 * Gets the sound effect volume, in the range [0, 1].
	 * @return the sound effect volume.
	 */
	public static float getSoundEffectVolume()
	{
		return soundEffectVolume;
	}

	/**
	 * Sets the music volume, from 0 (mute) to 1 (max volume).
	 * @param volume the new music volume, ranging from 0 to 1.
	 * @throws IllegalArgumentException if volume is less than 0 or greater than 1.
	 */
	public static void setMusicVolume(float volume)
	{
		checkArgument(volume >= 0, "Music volume was less than zero: %s", volume);
		checkArgument(volume <= 1, "Music volume was greater than one: %s", volume);

		musicVolume = volume;
	}

	/**
	 * Gets the music volume, in the range [0, 1].
	 * @return the music volume.
	 */
	public static float getMusicVolume()
	{
		return musicVolume;
	}

	/**
	 * Preloads the core sound effects, to prevent slight hickups that can occur when a sound is first used.
	 */
	private static void preloadCoreSoundEffects() {
		getSoundEffect(Sfx.CANNON_FIRED);
		getSoundEffect(Sfx.MINE_EXPLOSION);
		getSoundEffect(Sfx.TANK_EXPLOSION);
		getSoundEffect(Sfx.PILLBOX_HIT);
		getSoundEffect(Sfx.TREE_HIT);
		getSoundEffect(Sfx.WALL_HIT);
		getSoundEffect(Sfx.TANK_HIT);
	}

	/**
	 * Gets the specified sound effect. Loads the sound file and stores it in memory if needed.
	 * @param sfx the sound effect to get.
	 * @return reference to the loaded sound effect.
	 */
	private static Sound getSoundEffect(Sfx sfx) {
		Sound soundEffect = soundEffects.get(sfx);
		if (soundEffect == null) {
			soundEffect = loadSoundEffect(sfx);
			soundEffects.put(sfx, soundEffect);
		}
		return soundEffect;
	}

	private static Sound loadSoundEffect(Sfx sfx) {
		FileHandle soundFile = new FileHandle(new File(Audio.SFX_PATH + sfx.fileName));
		Sound sound = Gdx.audio.newSound(soundFile);
		return sound;
	}

	/**
	 * Disposes all sound effects and music files.
	 */
	public static void dispose()
	{
		for (Sound soundEffect : soundEffects.values()) {
			soundEffect.dispose();
		}

		if (music != null)
		{
			for (Music m : music)
			{
				m.stop();
				m.dispose();
			}
		}
	}
}
