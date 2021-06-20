package bubolo.ui.gui;

public record LayoutArgs(int startLeft, int startTop, int parentWidth, int parentHeight, int padding) {
	public LayoutArgs {
		assert startLeft >= 0;
		assert startTop >= 0;
		assert parentWidth >= 0;
		assert parentHeight >= 0;
		assert padding >= 0;
	}
}
