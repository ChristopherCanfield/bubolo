package bubolo.ui.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bubolo.graphics.Graphics;
import bubolo.input.InputManager.Action;

public class GuiGroup implements UiComponent {
	private	final List<UiComponent> components = new ArrayList<>();

	private final List<Focusable> focusables = new ArrayList<>();
	private int focusedComponentIndex = NoIndex;

	private boolean visible = true;

	public void add(UiComponent component) {
		components.add(component);

		if (component instanceof Focusable focusable) {
			focusables.add(focusable);
			if (focusedComponentIndex == NoIndex) {
				focusedComponentIndex = focusables.size() - 1;
				focusable.gainFocus();
			}
		}
	}

	public List<UiComponent> components() {
		return Collections.unmodifiableList(components);
	}

	@Override
	public void recalculateLayout() {
		components.forEach(c -> c.recalculateLayout());
	}

	@Override
	public void recalculateLayout(int parentWidth, int parentHeight) {
		components.forEach(c -> c.recalculateLayout(parentWidth, parentHeight));
	}

	/**
	 * Sets the visibility of the gui group. If the group is not visible, none of its children will be drawn, and no
	 * user input events will be passed to the children.
	 *
	 * @param visible true to make the group visible, false otherwise.
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public void draw(Graphics graphics) {
		if (visible) {
			components.forEach(c -> c.draw(graphics));
		}
	}

	@Override
	public void onInputAction(Action action) {
		if (visible) {
			if (action == Action.MenuMoveToNextGroup) {
				focusOnNextFocusable();
			} else if (action == Action.MenuMoveToPreviousGroup) {
				focusOnPreviousFocusable();
			} else {
				components.forEach(c -> c.onInputAction(action));
			}
		}
	}

	@Override
	public void onKeyTyped(char character) {
		if (visible) {
			components.forEach(c -> c.onKeyTyped(character));
		}
	}

	@Override
	public void onKeyDown(int keycode) {
		if (visible) {
			components.forEach(c -> c.onKeyDown(keycode));
		}
	}

	private void focusOnNextFocusable() {
		focusOnFocusable(1);
	}

	private void focusOnPreviousFocusable() {
		focusOnFocusable(-1);
	}

	private void focusOnFocusable(int direction) {
		assert direction == -1 || direction == 1;

		if (!focusables.isEmpty()) {
			focusables.forEach(f -> f.lostFocus());

			for (int i = 0; i < focusables.size(); i++) {
				focusedComponentIndex += direction;
				if (focusedComponentIndex < 0) {
					focusedComponentIndex = focusables.size() - 1;
				} else if (focusedComponentIndex >= focusables.size()) {
					focusedComponentIndex = 0;
				}
				var focusable = focusables.get(focusedComponentIndex);
				if (focusable.isValidFocusTarget()) {
					focusable.gainFocus();
					break;
				}
			}
		}
	}

	@Override
	public ClickedObjectInfo onMouseClicked(int screenX, int screenY) {
		if (visible) {
			for (UiComponent component : components) {
				var clickedObjectInfo = component.onMouseClicked(screenX, screenY);
				if (clickedObjectInfo != null && clickedObjectInfo.component() instanceof Focusable focusable) {
					if (clickedObjectInfo.clickedItemIndex() != NoIndex) {
						focusable.gainFocus();
						return clickedObjectInfo;
					} else {
						focusable.lostFocus();
					}
				}
			}
		}

		return null;
	}

	@Override
	public HoveredObjectInfo onMouseMoved(int screenX, int screenY) {
		if (visible) {
			for (UiComponent component : components) {
				var hoveredObjectInfo = component.onMouseMoved(screenX, screenY);
				if (hoveredObjectInfo != null && hoveredObjectInfo.hoveredItemIndex() != NoIndex) {
					return hoveredObjectInfo;
				}
			}
		}
		return null;
	}

	@Override
	public boolean containsPoint(float screenX, float screenY) {
		if (visible) {
			for (UiComponent c : components) {
				if (c.containsPoint(screenX, screenY)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void dispose() {
		components.forEach(c -> c.dispose());
	}
}
