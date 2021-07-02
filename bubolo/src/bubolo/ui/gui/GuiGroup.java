package bubolo.ui.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bubolo.graphics.Graphics;

public class GuiGroup {
	private	List<UiComponent> components = new ArrayList<>();
	private List<ButtonGroup> buttonGroups = new ArrayList<>();
	private List<TextBox> textBoxes = new ArrayList<>();

	public void add(UiComponent component) {
		components.add(component);
		if (component instanceof ButtonGroup buttonGroup) {
			buttonGroups.add(buttonGroup);
		}
		if (component instanceof TextBox textBox) {
			textBoxes.add(textBox);
		}
	}

	public List<UiComponent> components() {
		return Collections.unmodifiableList(components);
	}

	public List<ButtonGroup> buttonGroups() {
		return Collections.unmodifiableList(buttonGroups);
	}

	public List<TextBox> textBoxes() {
		return Collections.unmodifiableList(textBoxes);
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
		components.forEach(c -> c.onKeyDown(keycode));
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
		int hoveredItemIndex = -1;
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
