package bubolo;

import bubolo.util.Units;

/**
 * Configuration constants.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public class Config {
	public static final String AppTitle = "Bubolo";
	public static final String AppProgramaticTitle = "bubolo";
	public static final String AppAuthor = "Clone Productions";

	public static final String IconPath = "res/icons/";
	public static final String AppIcon16x16 = IconPath + "tank_icon16x16.png";
	public static final String AppIcon32x32 = IconPath + "tank_icon32x32.png";
	public static final String AppIcon48x48 = IconPath + "tank_icon48x48.png";

	public static final String UiPath = "res/ui/";

	public static final int FPS = 60;
	public static final double SecondsPerFrame = 1.0 / FPS;
	public static final double MillisPerFrame = 1000.0 / FPS;

	public static final int TargetWindowHeight = 720;
	public static final int TargetWindowWidth = 1280;

	public static final float DefaultPixelsPerWorldUnit = 1;

	public static final int MaxWorldColumns = (Short.MAX_VALUE * 2 + 2) / Units.TileToWorldScale;
	public static final int MaxWorldRows = MaxWorldColumns;
	public static final int MaxWorldX = MaxWorldColumns * Units.TileToWorldScale - 1;
	public static final int MaxWorldY = MaxWorldRows * Units.TileToWorldScale - 1;
}
