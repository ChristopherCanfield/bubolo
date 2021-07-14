package bubolo.util;

/**
 * A rectangle made up of integer components.
 *
 * @param left the rectangle's left position.
 * @param bottom the rectangle's bottom position. The rectangle grows from the bottom up.
 * @param width the rectangle's width.
 * @param height the rectangle's height.
 *
 * @author Christopher D. Canfield
 */
public record Rect(int left, int bottom, int width, int height) {
	public Rect {
		assert left >= 0;
		assert bottom >= 0;
		assert width > 0;
		assert height > 0;
	}

	public int right() {
		return left() + width();
	}

	public int top() {
		return bottom() + height();
	}

	public boolean contains(float x, float y) {
		return contains((int) x, (int) y);
	}

	public boolean contains(int x, int y) {
		return x >= left() && x <= right()
				&& y >= bottom() && y <= top();
	}
}
