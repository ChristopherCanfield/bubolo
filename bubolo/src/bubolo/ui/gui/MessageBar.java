package bubolo.ui.gui;

import java.util.ArrayDeque;
import java.util.Queue;

import com.badlogic.gdx.graphics.Color;

import bubolo.graphics.Graphics;
import bubolo.util.Time;

public class MessageBar extends UiComponent {
	private static class Message {
		final String text;
		final Color color;

		private static final int maxTicks = Time.secondsToTicks(30);
		int ticksRemaining;

		Message(String text, Color color) {
			this.text = text;
			this.color = color;
		}
	}

	private final Label[] messageLabels;

	private final Queue<Message> messages = new ArrayDeque<Message>();

	public MessageBar(LayoutArgs layoutArgs, int maxMessages) {
		super(layoutArgs);

		messageLabels = new Label[maxMessages];
		for (int i = 0; i < maxMessages; i++) {
			var label = new Label(layoutArgs, "");
			messageLabels[i] = label;
			if (i == 0) {
				label.setVerticalOffset(0, OffsetType.ScreenUnits, VOffsetFrom.Top);
			} else if (i > 0) {
				label.setVerticalOffset(messageLabels[i-1], VOffsetFromObjectSide.Bottom, 20, OffsetType.ScreenUnits, VOffsetFrom.Top);
			}
		}
		recalculateLayout();
	}

	public void addMessage(String message, Color textColor) {
		if (messages.size() < messageLabels.length) {
			messageLabels[messages.size()].setText(message);
			messageLabels[messages.size()].setTextColor(textColor);
			recalculateLayout();
		}

		messages.add(new Message(message, textColor));
	}

	@Override
	public float width() {
		return 300;
	}

	@Override
	public float height() {
		return parentHeight - 50;
	}

	@Override
	protected void onRecalculateLayout() {
//		for (int i = 0; i < messageLabels.length; i++) {
//			int height = (i == 0) ? parentHeight : (int) (parentHeight - messageLabels[i-1].height() - 10);
//			messageLabels[i].recalculateLayout((int) width(), height);
//		}
	}

	@Override
	public void draw(Graphics graphics) {
		for (int i = 0; i < messages.size(); i++) {
			messageLabels[i].draw(graphics);
		}

		moveMessagesUpIfTopIsExpired();
	}

	private void moveMessagesUpIfTopIsExpired() {
		var topMessage = messages.peek();
		if (topMessage != null && topMessage.ticksRemaining < 0) {
			messages.poll();

			int labelIndex = 0;
			for (Message message : messages) {
				messageLabels[labelIndex].setText(message.text);
				messageLabels[labelIndex].setTextColor(message.color);

				labelIndex++;
				if (labelIndex >= messages.size()) {
					return;
				}
			}

			for (; labelIndex < messageLabels.length; labelIndex++) {
				messageLabels[labelIndex].setText("");
			}

			recalculateLayout();
		}
	}
}
