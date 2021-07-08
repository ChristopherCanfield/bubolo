package bubolo.ui.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Input.Keys;

import bubolo.graphics.Graphics;

public class GuiGroup {
	private	final List<UiComponent> components = new ArrayList<>();

	private final List<Focusable> focusables = new ArrayList<>();
	private int focusedComponentIndex = UiComponent.NoIndex;

	public void add(UiComponent component) {
		components.add(component);

		if (component instanceof Focusable focusable) {
			focusables.add(focusable);
			if (focusedComponentIndex == UiComponent.NoIndex) {
				focusedComponentIndex = focusables.size() - 1;
				focusable.gainFocus();
			}
		}
	}

	public List<UiComponent> components() {
		return Collections.unmodifiableList(components);
	}

	public void recalculateLayout() {
		components.forEach(c -> c.recalculateLayout());
	}

	public void recalculateLayout(int parentWidth, int parentHeight) {
		components.forEach(c -> c.recalculateLayout(parentWidth, parentHeight));
	}

	public void draw(Graphics graphics) {
		components.forEach(c -> c.draw(graphics));
	}

	public void onKeyTyped(char character) {
		components.forEach(c -> c.onKeyTyped(character));
	}

	public void onKeyDown(int keycode) {
		if (keycode == Keys.TAB) {
			focusOnNextFocusable();
		} else {
			components.forEach(c -> c.onKeyDown(keycode));
		}
	}

	private void focusOnNextFocusable() {
		if (!focusables.isEmpty()) {
			focusedComponentIndex++;
			if (focusedComponentIndex >= focusables.size()) {
				focusedComponentIndex = 0;
			}
			focusables.get(focusedComponentIndex).gainFocus();
			System.out.println("Focusable at index " + focusedComponentIndex + " selected.");
		}
	}

	public record ClickedObjectInfo(UiComponent component, int clickedItemIndex) {
	}

	public ClickedObjectInfo onMouseClicked(int screenX, int screenY) {
		UiComponent clickedComponent = null;
		int clickedItemIndex = -1;
		for (UiComponent component : components) {
			var itemIndex = component.onMouseClicked(screenX, screenY);
			if (itemIndex != UiComponent.NoIndex) {
				clickedItemIndex = itemIndex;
				clickedComponent = component;
				if (component instanceof Focusable focusable) {
					focusable.gainFocus();
				}
			} else if (component instanceof Focusable focusable) {
				focusable.lostFocus();
			}
		}

		if (clickedComponent != null) {
			return new ClickedObjectInfo(clickedComponent, clickedItemIndex);
		} else {
			return null;
		}
	}

	public record HoveredObjectInfo(UiComponent component, int hoveredItemIndex) {
	}

	public HoveredObjectInfo onMouseMoved(int screenX, int screenY) {
		UiComponent hoveredComponent = null;
		int hoveredItemIndex = UiComponent.NoIndex;
		for (UiComponent component : components) {
			var itemIndex = component.onMouseMoved(screenX, screenY);
			if (itemIndex != UiComponent.NoIndex) {
				hoveredItemIndex = itemIndex;
				hoveredComponent = component;
			}
		}

		if (hoveredComponent != null) {
			return new HoveredObjectInfo(hoveredComponent, hoveredItemIndex);
		} else {
			return null;
		}
	}

	public void dispose() {
		components.forEach(c -> c.dispose());
	}
}
