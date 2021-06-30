package bubolo.ui.gui;

public record LayoutArgs(int parentWidth, int parentHeight, int padding) {
	public LayoutArgs {
		assert parentWidth >= 0;
		assert parentHeight >= 0;
		assert padding >= 0;
	}
}
