package bubolo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import bubolo.GameApplication;

public class MainMenuScreen extends Screen {
	private final GameApplication app;

	public MainMenuScreen(GameApplication app) {
		this.app = app;

		TextureAtlas atlas = new TextureAtlas(new FileHandle(UiConstants.UI_PATH + "skin.atlas"));
		Skin skin = new Skin(new FileHandle(UiConstants.UI_PATH + "skin.json"), atlas);

		table.row().colspan(8).padTop(25.f);
		TextButton singlePlayerButton = new TextButton("Single Player Game", skin);
		table.add(singlePlayerButton).expandX().width(225);
		singlePlayerButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

			}
		});

		table.row().colspan(8).padTop(25.f);
		TextButton multiplayerHostButton = new TextButton("Host Multiplayer Game", skin);
		table.add(multiplayerHostButton).expandX().width(225);
		multiplayerHostButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

			}
		});

		table.row().colspan(8).padTop(25.f);
		TextButton multiplayerJoinButton = new TextButton("Join Multiplayer Game", skin);
		table.add(multiplayerJoinButton).expandX().width(225);
		multiplayerJoinButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

			}
		});

		table.row().colspan(8).padTop(25.f);
		TextButton exitButton = new TextButton("Exit", skin);
		table.add(exitButton).expandX().width(225);
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
	}

	@Override
	public void onUpdate() {
	}

	@Override
	public void dispose() {
	}
}
