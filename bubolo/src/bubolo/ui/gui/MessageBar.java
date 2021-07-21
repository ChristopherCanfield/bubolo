package bubolo.ui.gui;

import java.util.ArrayDeque;
import java.util.Queue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

import bubolo.graphics.Fonts;
import bubolo.graphics.Graphics;
import bubolo.util.Time;

/**
 * Displays messages in a column. The messages fade out after a few seconds.
 *
 * @author Christopher D. Canfield
 */
public class MessageBar extends UiComponent {

	private static class Message {
		final String text;
		final Color color;

		/** Whether this message is visible. */
		boolean isVisible;

		private static final int timeVisibleTicks = Time.secondsToTicks(10);
		int ticksRemaining = timeVisibleTicks;

		Message(String text, Color color, boolean isVisible) {
			this.text = text;
			this.color = color;
			this.isVisible = isVisible;
		}

		float alpha() {
			return Interpolation.exp10In.apply(1, 0.3f, (timeVisibleTicks - ticksRemaining) / (float) timeVisibleTicks);
		}
	}

	private final Label[] messageLabels;

	private final Queue<Message> messages = new ArrayDeque<Message>();

	public MessageBar(LayoutArgs layoutArgs, int maxMessagesDisplayed) {
		super(layoutArgs);

		messageLabels = new Label[maxMessagesDisplayed];
		for (int i = 0; i < maxMessagesDisplayed; i++) {
			var label = new Label(layoutArgs, "", Fonts.Arial16, Color.BLACK, true, 250);
			messageLabels[i] = label;
			label.setHorizontalOffset(20, OffsetType.ScreenUnits, HOffsetFrom.Left);
			if (i == 0) {
				label.setVerticalOffset(40, OffsetType.ScreenUnits, VOffsetFrom.Top);
			} else if (i > 0) {
				label.setVerticalOffset(messageLabels[i-1], VOffsetFromObjectSide.Bottom, 20, OffsetType.ScreenUnits, VOffsetFrom.Top);
			}
		}
		recalculateLayout();
	}

	public void addMessage(String message, Color textColor) {
		boolean isVisible = false;
		if (messages.size() < messageLabels.length) {
			messageLabels[messages.size()].setText(message);
			messageLabels[messages.size()].setTextColor(textColor);
			recalculateLayout();
			isVisible = true;
		}

		messages.add(new Message(message, textColor, isVisible));
	}

	@Override
	public float width() {
		return parentWidth;
	}

	@Override
	public float height() {
		return parentHeight;
	}

	@Override
	protected void onRecalculateLayout() {
		for (int i = 0; i < messageLabels.length; i++) {
			messageLabels[i].recalculateLayout(parentWidth, parentHeight);
		}
	}

	@Override
	public void draw(Graphics graphics) {
		int i = 0;
		for (Message message : messages) {
			messageLabels[i].setTextAlpha(message.alpha());
			messageLabels[i].draw(graphics);

			i++;
			if (i == messageLabels.length) {
				break;
			}
		}

		reduceMessageTimeRemaining();
		moveMessagesUpIfTopIsExpired();
	}

	private void reduceMessageTimeRemaining() {
		for (var message : messages) {
			if (message.isVisible) {
				message.ticksRemaining--;
			}
		}
	}

	/**
	 * Removes the top message, if it has expired, and moves all other messages up a slot.
	 */
	private void moveMessagesUpIfTopIsExpired() {
		var topMessage = messages.peek();
		if (topMessage != null && topMessage.ticksRemaining < 0) {
			messages.poll();

			int labelIndex = 0;
			for (Message message : messages) {
				// Stop processing if there are no remaining labels.
				if (labelIndex == messageLabels.length) {
					break;
				}
				messageLabels[labelIndex].setText(message.text);
				messageLabels[labelIndex].setTextColor(message.color);
				message.isVisible = true;

				labelIndex++;
				if (labelIndex >= messages.size()) {
					break;
				}
			}

			for (; labelIndex < messageLabels.length; labelIndex++) {
				messageLabels[labelIndex].setText("");
			}

			recalculateLayout();
		}
	}
}
