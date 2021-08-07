package bubolo.ui.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import bubolo.graphics.Graphics;

public class GuiGroup implements UiComponent {
	private	final List<UiComponent> components = new ArrayList<>();

	private final List<Focusable> focusables = new ArrayList<>();
	private int focusedComponentIndex = PositionableUiComponent.NoIndex;

	public void add(UiComponent component) {
		components.add(component);

		if (component instanceof Focusable focusable) {
			focusables.add(focusable);
			if (focusedComponentIndex == PositionableUiComponent.NoIndex) {
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

	@Override
	public void draw(Graphics graphics) {
		components.forEach(c -> c.draw(graphics));
	}

	@Override
	public void onKeyTyped(char character) {
		components.forEach(c -> c.onKeyTyped(character));
	}

	@Override
	public void onKeyDown(int keycode) {
		if (keycode == Keys.TAB) {
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
				focusOnPreviousFocusable();
			} else {
				focusOnNextFocusable();
			}
		} else {
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

		return null;
	}

	@Override
	public HoveredObjectInfo onMouseMoved(int screenX, int screenY) {
		for (UiComponent component : components) {
			var hoveredObjectInfo = component.onMouseMoved(screenX, screenY);
			if (hoveredObjectInfo != null && hoveredObjectInfo.hoveredItemIndex() != NoIndex) {
				return hoveredObjectInfo;
			}
		}
		return null;
	}

	@Override
	public boolean containsPoint(float screenX, float screenY) {
		for (UiComponent c : components) {
			if (c.containsPoint(screenX, screenY)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void dispose() {
		components.forEach(c -> c.dispose());
	}
}
