package bubolo.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import bubolo.Config;

public abstract class Fonts {
	public static final BitmapFont Arial16 = new BitmapFont(Gdx.files.internal(Config.UiPath.resolve("arial-16.fnt").toString()));
	public static final BitmapFont Arial18 = new BitmapFont(Gdx.files.internal(Config.UiPath.resolve("arial-18.fnt").toString()));
	public static final BitmapFont Arial20 = new BitmapFont(Gdx.files.internal(Config.UiPath.resolve("arial-20.fnt").toString()));
	public static final BitmapFont Arial32 = new BitmapFont(Gdx.files.internal(Config.UiPath.resolve("arial-32.fnt").toString()));

	public static final BitmapFont UiGeneralTextFont = Arial18;
	public static final BitmapFont UiTitleFont = Arial32;
}
