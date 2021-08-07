package bubolo.ui.gui;

import bubolo.graphics.Graphics;

/**
 * Provides layout functionality to user interface elements.
 *
 * @author Christopher D. Canfield
 */
public abstract class PositionableUiComponent implements UiComponent {
	public enum OffsetType {
		Percent, ScreenUnits;
	}

	public enum HOffsetFrom {
		Left, Center;
	}

	public enum VOffsetFrom {
		Top, Center;
	}

	public enum HOffsetFromObjectSide {
		Left, Right;
	}

	public enum VOffsetFromObjectSide {
		Top, Bottom;
	}

	private float horizontalOffset;
	private OffsetType horizontalOffsetType = OffsetType.ScreenUnits;
	private HOffsetFrom horizontalOffsetFrom = HOffsetFrom.Left;
	private PositionableUiComponent horizontalOffsetFromObject;
	private HOffsetFromObjectSide horizontalOffsetFromObjectSide;

	private float verticalOffset;
	private OffsetType verticalOffsetType = OffsetType.ScreenUnits;
	private VOffsetFrom verticalOffsetFrom = VOffsetFrom.Top;
	private PositionableUiComponent verticalOffsetFromObject;
	private VOffsetFromObjectSide verticalOffsetFromObjectSide;

	protected float startLeft;
	/** The starting top position, in screen coordinates (y-down). */
	protected float startTop;
	protected int parentWidth;
	protected int parentHeight;

	protected float left;
	protected float top;

	protected int padding;

	public abstract float width();
	public abstract float height();

	protected PositionableUiComponent(LayoutArgs layoutArgs) {
		this.parentWidth = layoutArgs.parentWidth();
		this.parentHeight = layoutArgs.parentHeight();
		this.padding = layoutArgs.padding();
	}

	public float left() {
		return left;
	}

	public float right() {
		return left + width();
	}

	public float top() {
		return top;
	}

	public float bottom() {
		return top + height();
	}

	/**
	 * @param offset the horizontal offset, either in screen units or as a percentage.
	 * @param offsetType the units of the horizontal offset, either screen units or percentage.
	 * @param offsetFrom whether to offset the horizontal position from the left or horizontal center.
	 */
	public void setHorizontalOffset(float offset, OffsetType offsetType, HOffsetFrom offsetFrom) {
		setHorizontalOffset(null, null, offset, offsetType, offsetFrom);
	}

	/**
	 * @param offsetFromObject the object that determines the start of the offset. Use the other overload if no object is used for this.
	 * @param offsetFromObjectSide the side of the offsetFromObject to use when determining the start of the offset.
	 * @param offset the horizontal offset, either in screen units or as a percentage.
	 * @param offsetType the units of the horizontal offset, either screen units or percentage.
	 * @param offsetFrom whether to offset the horizontal position from the left or horizontal center.
	 */
	public void setHorizontalOffset(PositionableUiComponent offsetFromObject, HOffsetFromObjectSide offsetFromObjectSide, float offset, OffsetType offsetType, HOffsetFrom offsetFrom) {
		this.horizontalOffsetFromObject = offsetFromObject;
		if (offsetFromObject != null) {
			assert offsetFromObjectSide != null;
			this.horizontalOffsetFromObjectSide = offsetFromObjectSide;
		}
		this.horizontalOffset = offset;
		this.horizontalOffsetType = offsetType;
		this.horizontalOffsetFrom = offsetFrom;
		recalculateLayout(parentWidth, parentHeight);
	}

	/**
	 * @param offset the vertical offset, either in screen units or as a percentage.
	 * @param offsetType the units of the vertical offset, either screen units or percentage.
	 * @param offsetFrom whether to offset the vertical position from the top or vertical center.
	 */
	public void setVerticalOffset(float offset, OffsetType offsetType, VOffsetFrom offsetFrom) {
		setVerticalOffset(null, null, offset, offsetType, offsetFrom);
	}

	/**
	 * @param offsetFromObject the object that determines the start of the offset. Use the other overload if no object is used for this.
	 * @param offsetFromObjectSide the side of the offsetFromObject to use when determining the start of the offset.
	 * @param offset the vertical offset, either in screen units or as a percentage.
	 * @param offsetType the units of the vertical offset, either screen units or percentage.
	 * @param offsetFrom whether to offset the vertical position from the top or vertical center.
	 */
	public void setVerticalOffset(PositionableUiComponent offsetFromObject, VOffsetFromObjectSide offsetFromObjectSide, float offset, OffsetType offsetType, VOffsetFrom offsetFrom) {
		this.verticalOffsetFromObject = offsetFromObject;
		if (offsetFromObject != null) {
			assert offsetFromObjectSide != null;
			this.verticalOffsetFromObjectSide = offsetFromObjectSide;
		}
		this.verticalOffset = offset;
		this.verticalOffsetType = offsetType;
		this.verticalOffsetFrom = offsetFrom;
		recalculateLayout(parentWidth, parentHeight);
	}

	/**
	 * Calculates the left position of this user interface component.
	 *
	 * @param parentWidth the parent's width.
	 * @return the left position of this user interface component.
	 */
	protected float horizontalPosition(float parentWidth) {
		if (horizontalOffsetFromObject != null) {
			if (horizontalOffsetFromObjectSide == HOffsetFromObjectSide.Left) {
				this.startLeft = horizontalOffsetFromObject.left();
			} else {
				this.startLeft = horizontalOffsetFromObject.right();
			}
		}

		if (horizontalOffsetType == OffsetType.Percent) {
			return horizontalPositionPct(parentWidth);
		} else {
			return horizontalPositionScreenUnits(parentWidth, horizontalOffset);
		}
	}

	private float horizontalPositionPct(float parentWidth) {
		float hOffsetScreenUnits = parentWidth * horizontalOffset;
		return horizontalPositionScreenUnits(parentWidth, hOffsetScreenUnits);
	}

	private float horizontalPositionScreenUnits(float parentWidth, float hOffsetScreenUnits) {
		if (horizontalOffsetFrom == HOffsetFrom.Center) {
			float viewportHCenter = parentWidth / 2;
			return viewportHCenter - (width() / 2) + hOffsetScreenUnits;
		} else {
			return startLeft + hOffsetScreenUnits;
		}
	}

	/**
	 * Calculates the top position of this user interface component.
	 *
	 * @param parentHeight the parent's height.
	 * @return the top position of this user interface component.
	 */
	protected float verticalPosition(float parentHeight) {
		if (verticalOffsetFromObject != null) {
			if (verticalOffsetFromObjectSide == VOffsetFromObjectSide.Bottom) {
				this.startTop = verticalOffsetFromObject.bottom();
			} else {
				this.startTop = verticalOffsetFromObject.top();
			}
		}

		if (verticalOffsetType == OffsetType.Percent) {
			return verticalPositionPct(parentHeight);
		} else {
			return verticalPositionScreenUnits(parentHeight, verticalOffset);
		}
	}

	private float verticalPositionPct(float parentHeight) {
		float vOffsetScreenUnits = parentHeight * verticalOffset;
		return verticalPositionScreenUnits(parentHeight, vOffsetScreenUnits);
	}

	private float verticalPositionScreenUnits(float parentHeight, float vOffsetScreenUnits) {
		if (verticalOffsetFrom == VOffsetFrom.Center) {
			float viewportVCenter = parentHeight / 2;
			return viewportVCenter - (height() / 2) + vOffsetScreenUnits;
		} else {
			return startTop + vOffsetScreenUnits;
		}
	}

	/**
	 * Recalculates the layout without updating the stored parent width or height.
	 */
	@Override
	public final void recalculateLayout() {
		recalculateLayout(parentWidth, parentHeight);
	}

	@Override
	public final void recalculateLayout(int parentWidth, int parentHeight) {
		this.parentWidth = parentWidth;
		this.parentHeight = parentHeight;

		this.left = horizontalPosition(parentWidth);
		this.top = verticalPosition(parentHeight);

		onRecalculateLayout();
	}

	/**
	 * Called after recalculateLayout is called. The parentWidth, parentHeight, startLeft, startTop, left, and top variables
	 * are updated before this is called.
	 */
	protected abstract void onRecalculateLayout();

	@Override
	public abstract void draw(Graphics graphics);

	@Override
	public boolean containsPoint(float screenX, float screenY) {
		return screenX >= left && screenX <= right()
				&& screenY >= top && screenY <= bottom();
	}

	/**
	 * Override to receive key typed events.
	 *
	 * @param character the character that was typed.
	 */
	@Override
	public void onKeyTyped(char character) {
	}

	/**
	 * Override to receive key down events.
	 *
	 * @param keycode the keycode that was typed.
	 */
	@Override
	public void onKeyDown(int keycode) {
	}

	/**
	 * Override to receive mouse clicked events.
	 *
	 * @param screenX the click's screen x position.
	 * @param screenY the click's screen y position.
	 * @return One of:
	 * <ul>
	 * 	<li>The selected item's index, if applicable.</li>
	 * 	<li>Zero, if the entire object was selected.</li>
	 * 	<li>NoIndex (-1) if the object was not selected.</li>
	 * </ul>
	 */
	@Override
	public ClickedObjectInfo onMouseClicked(int screenX, int screenY) {
		return null;
	}

	/**
	 * Override to receive mouse moved events.
	 *
	 * @param screenX the mouse's screen x position.
	 * @param screenY the mouse's screen y position.
	 * @return One of:
	 * <ul>
	 * 	<li>The hovered item's index, if applicable.</li>
	 * 	<li>Zero, if the entire object was hovered.</li>
	 * 	<li>NoIndex (-1) if the object was not hovered.</li>
	 * </ul>
	 */
	@Override
	public HoveredObjectInfo onMouseMoved(int screenX, int screenY) {
		return null;
	}

	/**
	 * Override to receive dispose events.
	 */
	@Override
	public void dispose() {
	}
}
