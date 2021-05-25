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
