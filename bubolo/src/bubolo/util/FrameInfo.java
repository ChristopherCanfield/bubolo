package bubolo.util;

import bubolo.graphics.Graphics;

public class FrameInfo {
	private final Graphics graphics;

	private long frameStartNanos;
	private long frameEndNanos;
	private long nanosSinceLastFrame;

	public FrameInfo(Graphics graphics) {
		this.graphics = graphics;
	}

	public void beginFrame() {
		frameStartNanos = System.nanoTime();
		nanosSinceLastFrame = (frameEndNanos != 0) ? frameStartNanos - frameEndNanos : 0;
	}

	public void endFrame() {
		frameEndNanos = System.nanoTime();
	}

	@Override
	public String toString() {
		return "Frame Info:\n"
				+ "   Frame Time (ms):            " + ((frameEndNanos - frameStartNanos) / 1_000_000.0) + '\n'
				+ "   Time since last frame (ms): " + (nanosSinceLastFrame / 1_000_000.0) + '\n'
				+ "   Batched draw calls:         " + graphics.getBatchedRenderCalls() + '\n'
				+ "   Max sprites in batch:       " + graphics.getMaxSpritesInScalingBatch();
	}
}
