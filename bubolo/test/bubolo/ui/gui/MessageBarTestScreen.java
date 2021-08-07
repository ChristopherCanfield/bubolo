package bubolo.ui.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import bubolo.ui.AbstractScreen;
import bubolo.ui.gui.PositionableUiComponent.HOffsetFrom;
import bubolo.ui.gui.PositionableUiComponent.OffsetType;
import bubolo.ui.gui.PositionableUiComponent.VOffsetFrom;

public class MessageBarTestScreen extends AbstractScreen {
	private final Color clearColor = Color.BLACK; //new Color(0.85f, 0.85f, 0.85f, 1);

	private final MessageBar messageBar;

	public MessageBarTestScreen() {
		LayoutArgs messageBarLayoutArgs = new LayoutArgs(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
		messageBar = new MessageBar(messageBarLayoutArgs, 15);
		messageBar.setVerticalOffset(0, OffsetType.ScreenUnits, VOffsetFrom.Top);
		messageBar.setHorizontalOffset(0, OffsetType.ScreenUnits, HOffsetFrom.Left);
		messageBar.recalculateLayout();

		root.add(messageBar);
	}

	@Override
	public Color clearColor() {
		return clearColor;
	}

	@Override
	public void onKeyTyped(char character) {
		switch (character) {
			case '1' -> messageBar.addMessage("Test message 1", Color.CYAN);
			case '2' -> messageBar.addMessage("Hello, this is test message 2.", Color.YELLOW);
			case '3' -> messageBar.addMessage("asdljf falkdfj kadlf aklfd jakldf jaksldf jklds fjakl fdjkalf jdsklf jask;ldf jskdf jask;ldf jasklf jas;dlf ja;lsk fasjd;f", Color.WHITE);
		}
	}
}
