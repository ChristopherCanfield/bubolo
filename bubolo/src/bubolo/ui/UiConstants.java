/**
 *
 */

package bubolo.ui;

import javax.swing.ImageIcon;

import bubolo.audio.Audio;

/**
 * User interface constants.
 *
 * @author Christopher D. Canfield
 */
public abstract class UiConstants {
	/**
	 * File path where UI icons are stored.
	 */
	public static final String ICONS_PATH = "res/icons/";

	/**
	 * File path where the user interface files are located.
	 */
	public static final String UI_PATH = "res/ui/";

	/**
	 * Sound Effects Volume Default
	 */
	public static final float SFXVOL_DEFAULT = Audio.soundEffectVolume();

	/**
	 * The game icon
	 */
	public static ImageIcon gameIcon = new ImageIcon(ICONS_PATH + "tank_icon.png");
}
