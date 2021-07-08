package bubolo.ui.gui;

/**
 * Specifies that an object is focusable, such as when the tab key is pressed.
 *
 * @author Christopher D. Canfield
 */
public interface Focusable {

	/**
	 * Specifies whether this is a valid target on which to call {@code gainFocus()}. By default, this is set to true,
	 * but it can be overridden if the implementing object shouldn't receive focus. This is primarily intended to
	 * allow a focusable component to be enabled and disabled.
	 *
	 * @return true if the object can receive focus events, or false otherwise.
	 */
	default boolean isValidFocusTarget() {
		return true;
	}

	/**
	 * Called when this object should receive focus.
	 */
	void gainFocus();

	/**
	 * Called when this object has lost focus.
	 */
	void lostFocus();
}
