package bubolo.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import bubolo.Config;

public abstract class Fonts {
	public static final BitmapFont Arial16 = new BitmapFont(Gdx.files.internal(Config.UiPath + "arial-16.fnt"));
	public static final BitmapFont Arial18 = new BitmapFont(Gdx.files.internal(Config.UiPath + "arial-18.fnt"));
	public static final BitmapFont Arial20 = new BitmapFont(Gdx.files.internal(Config.UiPath + "arial-20.fnt"));
}
