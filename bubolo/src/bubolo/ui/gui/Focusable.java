package bubolo.ui.gui;

/**
 * Specifies that an object is focusable, such as when the tab key is pressed.
 *
 * @author Christopher D. Canfield
 */
public interface Focusable {

	/**
	 * Called when this object should receive focus.
	 */
	void gainFocus();

	/**
	 * Called when this object has lost focus.
	 */
	void lostFocus();
}
