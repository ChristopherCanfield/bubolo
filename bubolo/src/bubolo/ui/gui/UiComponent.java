package bubolo.ui.gui;

/**
 * Provides layout functionality to user interface elements.
 *
 * @author Christopher D. Canfield
 */
public abstract class UiComponent {
	public enum OffsetType {
		Percent, ScreenUnits;
	}

	public enum HOffsetFrom {
		Left, Center;
	}

	public enum VOffsetFrom {
		Top, Center;
	}

	private float horizontalOffset;
	private OffsetType horizontalOffsetType;
	private HOffsetFrom horizontalOffsetFrom;

	private float verticalOffset;
	private OffsetType verticalOffsetType;
	private VOffsetFrom verticalOffsetFrom;

	protected abstract float width();
	protected abstract float height();

	/**
	 * @param offset the horizontal offset, either in screen units or as a percentage.
	 * @param offsetType the units of the horizontal offset, either screen units or percentage.
	 * @param offsetFrom whether to offset the horizontal position from the left or horizontal center.
	 */
	public void setHorizontalOffset(float offset, OffsetType offsetType, HOffsetFrom offsetFrom) {
		this.horizontalOffset = offset;
		this.horizontalOffsetType = offsetType;
		this.horizontalOffsetFrom = offsetFrom;
	}

	/**
	 * @param offset the vertical offset, either in screen units or as a percentage.
	 * @param offsetType the units of the vertical offset, either screen units or percentage.
	 * @param offsetFrom whether to offset the vertical position from the top or vertical center.
	 */
	public void setVerticalOffset(float offset, OffsetType offsetType, VOffsetFrom offsetFrom) {
		this.verticalOffset = offset;
		this.verticalOffsetType = offsetType;
		this.verticalOffsetFrom = offsetFrom;
	}

	/**
	 * Calculates the left position of this user interface component.
	 *
	 * @param startLeft the starting left position.
	 * @param parentWidth the parent's width.
	 * @return the left position of this user interface component.
	 */
	protected float horizontalPosition(float startLeft, float parentWidth) {
		if (horizontalOffsetType == OffsetType.Percent) {
			return horizontalPositionPct(startLeft, parentWidth);
		} else {
			return horizontalPositionScreenUnits(startLeft, parentWidth, horizontalOffset);
		}
	}

	private float horizontalPositionPct(float startLeft, float parentWidth) {
		float hOffsetScreenUnits = parentWidth * horizontalOffset;
		return horizontalPositionScreenUnits(startLeft, parentWidth, hOffsetScreenUnits);
	}

	private float horizontalPositionScreenUnits(float startLeft, float parentWidth, float hOffsetScreenUnits) {
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
	 * @param startTop the starting top position.
	 * @param parentHeight the parent's height.
	 * @return the top position of this user interface component.
	 */
	protected float verticalPosition(float startTop, float parentHeight) {
		if (verticalOffsetType == OffsetType.Percent) {
			return verticalPositionPct(startTop, parentHeight);
		} else {
			return verticalPositionScreenUnits(startTop, parentHeight, verticalOffset);
		}
	}

	private float verticalPositionPct(float startTop, float parentHeight) {
		float vOffsetScreenUnits = parentHeight * verticalOffset;
		return verticalPositionScreenUnits(startTop, parentHeight, vOffsetScreenUnits);
	}

	private float verticalPositionScreenUnits(float startTop, float parentHeight, float vOffsetScreenUnits) {
		if (verticalOffsetFrom == VOffsetFrom.Center) {
			float viewportVCenter = parentHeight / 2;
			return viewportVCenter - (height() / 2) + vOffsetScreenUnits;
		} else {
			return startTop + vOffsetScreenUnits;
		}
	}

	public abstract void recalculateLayout(int left, int startTop, int parentWidth, int parentHeight);
}
