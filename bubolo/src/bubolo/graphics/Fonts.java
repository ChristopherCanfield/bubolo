package bubolo.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import bubolo.ui.UiConstants;

public abstract class Fonts {
	public static final BitmapFont Arial16 = new BitmapFont(Gdx.files.internal(UiConstants.UI_PATH + "arial-16.fnt"));
	public static final BitmapFont Arial18 = new BitmapFont(Gdx.files.internal(UiConstants.UI_PATH + "arial-18.fnt"));
	public static final BitmapFont Arial20 = new BitmapFont(Gdx.files.internal(UiConstants.UI_PATH + "arial-20.fnt"));
}
