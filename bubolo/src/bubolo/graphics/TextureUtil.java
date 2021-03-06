package bubolo.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bubolo.util.Units;

/**
 * Used for processing standard format textures of different kinds for use in adaptive tiling and animations.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
abstract class TextureUtil {
	/**
	 * Splits a row of images within a texture into frames.
	 *
	 * @param texture the texture that contains the frames.
	 * @param frameCount the number of frames in the file.
	 * @param frameWidth the width of each frame.
	 * @param paddingWidth the horizontal padding between each frame.
	 * @return the frames.
	 */
	public static TextureRegion[] splitFramesInRow(Texture texture, int frameCount, int frameWidth, int paddingWidth) {
		TextureRegion[] frames = new TextureRegion[frameCount];
		for (int frame = 0; frame < frameCount; frame++) {
			frames[frame] = new TextureRegion(texture, frame * frameWidth + (paddingWidth * frame), 0,
					frameWidth, texture.getHeight());
		}
		return frames;
	}

	/**
	 * Splits a column of images within a texture into frames.
	 *
	 * @param texture the texture that contains the frames.
	 * @param frameCount the number of frames in the file.
	 * @param frameLeftX the left position of the frames.
	 * @param frameWidth the width of each frame.
	 * @param frameHeight the height of each frame.
	 * @return the frames.
	 */
	public static TextureRegion[] splitFramesInColumn(Texture texture, int frameCount, int frameLeftX, int frameWidth, int frameHeight) {
		TextureRegion[] frames = new TextureRegion[frameCount];
		for (int frame = 0; frame < frameCount; frame++) {
			frames[frame] = new TextureRegion(texture, frameLeftX, frame * frameHeight, frameWidth, frameHeight);
		}
		return frames;
	}

	/**
	 * Splits a texture into frames, using the height and width of each frame to determine start and end points of each frame.
	 * All frames must be the same size. The frames are in column-row order.
	 *
	 * @param tex the texture to be split into frames.
	 * @param frameWidth the width of each frame.
	 * @param frameHeight the height of each frame.
	 * @param framePaddingWidth the padding width between frames.
	 * @param framePaddingHeight the padding height between frames.
	 * @return a two-dimensional array of TextureRegions, in [column][row] order.
	 */
	public static TextureRegion[][] splitFrames(Texture tex, int frameWidth, int frameHeight, int framePaddingWidth, int framePaddingHeight)
	{
		int rows = tex.getHeight() / frameHeight;
		int columns = tex.getWidth() / frameWidth;

		TextureRegion[][] frameSets = new TextureRegion[columns][rows];
		for (int col = 0; col < columns; col++) {
			for (int row = 0; row < rows; row++) {
				frameSets[col][row] = new TextureRegion(tex, col * frameWidth + (col * framePaddingWidth), row * frameHeight + (row * framePaddingHeight),
						frameWidth, frameHeight);
			}
		}
		return frameSets;
	}

	public static TextureRegion[] adaptiveSplit(Texture texture, Class<? extends Sprite> spriteType) {
		if (spriteType.equals(DeepWaterSprite.class) || spriteType.equals(WaterSprite.class)) {
			return adaptiveSplit_water(texture);
		} else {
			return adaptiveSplit_16(texture);
		}
	}

	/**
	 * Convert a single .png representing multiple tiling states into an Array of 16
	 * different texture regions, according to the established 4x4 standard layout.
	 *
	 * @param tex
	 *            is a 4x4 input texture to be split. Must be at 128 x 128 resolution.
	 * @return an array of TextureRegions representing textures for each of the 16
	 *         adaptive tiling states.
	 */
	private static TextureRegion[] adaptiveSplit_16(Texture tex)
	{
		if (tex.getHeight() != Units.TileToWorldScale * 4
				&& tex.getWidth() != Units.TileToWorldScale * 4)
		{
			throw new TextureDimensionException("Cannot split texture into 16 tiles, wrong size!");
		}

		TextureRegion[] adapt = new TextureRegion[16];

		// Grab the 16 texture frames for a standard 4x4 layout
		TextureRegion[][] allFrames = splitFrames(tex, Units.TileToWorldScale, Units.TileToWorldScale, 0, 0);

		// Assign each texture frame to the correct index
		adapt[0] = allFrames[0][0];
		adapt[1] = allFrames[0][3];
		adapt[2] = allFrames[0][1];
		adapt[3] = allFrames[0][2];
		adapt[4] = allFrames[3][0];
		adapt[5] = allFrames[3][3];
		adapt[6] = allFrames[3][1];
		adapt[7] = allFrames[3][2];
		adapt[8] = allFrames[1][0];
		adapt[9] = allFrames[1][3];
		adapt[10] = allFrames[1][1];
		adapt[11] = allFrames[1][2];
		adapt[12] = allFrames[2][0];
		adapt[13] = allFrames[2][3];
		adapt[14] = allFrames[2][1];
		adapt[15] = allFrames[2][2];

		return adapt;
	}

	/**
	 * Convert a single .png representing multiple tiling states into an Array of 34
	 * different texture regions, according to the established 4x4 + 3x3 + 3x3 standard
	 * layout. Primarily used for the Water Terrain.
	 *
	 * @param tex
	 *            is a 4x4 + 3x3 + 3x3 input texture to be split. Must be at 224 x 192
	 *            resolution.
	 * @return an array of TextureRegions representing textures for each of the 9 adaptive
	 *         tiling states.
	 */
	private static TextureRegion[] adaptiveSplit_water(Texture tex)
	{
		if (tex.getHeight() != Units.TileToWorldScale * 4
				&& tex.getWidth() != Units.TileToWorldScale * 6) {
			throw new TextureDimensionException("Cannot split texture into 16x9x9 tiles, wrong size!");
		}

		TextureRegion[] adapt = new TextureRegion[24];

		// Grab the 34 texture frames for a standard 4x4 + 3x3 + 3x3 layout

		TextureRegion[][] allFrames = splitFrames(tex, Units.TileToWorldScale, Units.TileToWorldScale, 0, 0);

		// Assign each texture frame to the correct index...

		// rivers
		adapt[0] = allFrames[0][0];
		adapt[1] = allFrames[0][3];
		adapt[2] = allFrames[0][1];
		adapt[3] = allFrames[0][2];
		adapt[4] = allFrames[3][0];
		adapt[5] = allFrames[3][3];
		adapt[6] = allFrames[3][1];
		// (cdc - 2021-05-06): Should be [3][2], but [0][2] fixes a texture bleeding issue without any ill effects.
		adapt[7] = allFrames[0][2];
		adapt[8] = allFrames[1][0];
		adapt[9] = allFrames[1][3];
		adapt[10] = allFrames[1][1];
		adapt[11] = allFrames[1][2];
		adapt[12] = allFrames[2][0];
		adapt[13] = allFrames[2][3];
		adapt[14] = allFrames[2][1];
		adapt[15] = allFrames[2][2];

		// open water
		adapt[16] = allFrames[4][0];
		adapt[17] = allFrames[5][0];
		adapt[18] = allFrames[4][1];
		adapt[19] = allFrames[5][1];

		adapt[20] = allFrames[4][2];
		adapt[21] = allFrames[5][2];
//		adapt[22] = allFrames[4][3];
		adapt[23] = allFrames[5][3];

		// @Hack (cdc 2021-05-06): Hack to fix texture bleeding issue. This could be fixed by resizing the texture, and adding padding around each tile.
		adapt[22] = new TextureRegion(tex, 4 * Units.TileToWorldScale + 1, 3 * Units.TileToWorldScale, Units.TileToWorldScale, Units.TileToWorldScale);
		return adapt;
	}
}
