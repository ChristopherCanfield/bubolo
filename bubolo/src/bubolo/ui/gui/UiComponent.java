package bubolo.ui.gui;

import bubolo.graphics.Graphics;

public interface UiComponent {
	public static final int NoIndex = -1;

	public record ClickedObjectInfo(UiComponent component, int clickedItemIndex) {
	}

	public record HoveredObjectInfo(UiComponent component, int hoveredItemIndex) {
	}

	void recalculateLayout();
	void recalculateLayout(int parentWidth, int parentHeight);
	void draw(Graphics graphics);
	boolean containsPoint(float screenX, float screenY);
	void onKeyTyped(char character);
	void onKeyDown(int keycode);
	HoveredObjectInfo onMouseMoved(int screenX, int screenY);
	ClickedObjectInfo onMouseClicked(int screenX, int screenY);
	void dispose();
}
