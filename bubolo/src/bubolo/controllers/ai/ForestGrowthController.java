package bubolo.controllers.ai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;

import bubolo.Systems;
import bubolo.controllers.Controller;
import bubolo.net.command.CreateEntity;
import bubolo.util.Units;
import bubolo.world.Entity;
import bubolo.world.EntityLifetimeObserver;
import bubolo.world.Grass;
import bubolo.world.TerrainImprovement;
import bubolo.world.Tree;
import bubolo.world.World;

/**
 * Slowly adds new forest tiles to the world.
 *
 * @author Christopher D. Canfield
 */
public class ForestGrowthController implements Controller, EntityLifetimeObserver {
	private static final float initialScheduleTimeSeconds = 10;
	private static final float treesPerMinute = 3;
	private static final float secondsPerTreeGrowth = 60f / treesPerMinute;

	private static final byte grassIndicator = 0b0100_0000;
	private static final byte terrainImprovementIndicator = (byte) 0b1000_0000;

	// The forest growth for each tile, in column-row order.
	private byte[][] terrainGrowthScores;
	private final Deque<TerrainGrowthScore> nextGrowthTargets = new ArrayDeque<>();

	private static final int adjacentTreeGrowthFactor = 2;
	private static final int cornerTreeGrowthFactor = 1;

	@Override
	public void onObserverAddedToWorld(World world) {
		terrainGrowthScores = new byte[world.getTileColumns()][world.getTileRows()];
		world.timer().scheduleSeconds(initialScheduleTimeSeconds, this::findHighestScores);
	}

	@Override
	public void onEntityAdded(Entity entity) {
		int column = entity.tileColumn();
		int row = entity.tileRow();

		if (entity instanceof Tree) {
			addGrowthFactorToNeighbors(column, row, 1);
		} else if (entity instanceof Grass) {
			terrainGrowthScores[column][row] &= 0b0011_1111;
			terrainGrowthScores[column][row] |= grassIndicator;
		}

		if (entity instanceof TerrainImprovement) {
			terrainGrowthScores[column][row] |= terrainImprovementIndicator;
		}
	}

	@Override
	public void onEntityRemoved(Entity entity) {
		int column = entity.tileColumn();
		int row = entity.tileRow();

		if (entity instanceof Tree) {
			addGrowthFactorToNeighbors(column, row, -1);
		} else if (entity instanceof Grass) {
			terrainGrowthScores[column][row] &= 0b1011_1111;
			terrainGrowthScores[column][row] |= 0b1000_0000;
		}

		if (entity instanceof TerrainImprovement) {
			// Remove the terrain improvement indicator from the score.
			terrainGrowthScores[column][row] &= 0b0111_1111;
		}
	}

	@Override
	public void update(World world) {
	}

	/**
	 * Adds the forest growth factor to all neighbor tiles.
	 *
	 * @param column the forest's column.
	 * @param row the forest's row.
	 * @param growthFactorModifier 1 or -1, depending on whether the forest was added or removed.
	 */
	private void addGrowthFactorToNeighbors(int column, int row, int growthFactorModifier) {
		// West
		addGrowthFactorIfTileExists(column - 1, row, adjacentTreeGrowthFactor * growthFactorModifier);
		// North
		addGrowthFactorIfTileExists(column, row + 1, adjacentTreeGrowthFactor * growthFactorModifier);
		// East
		addGrowthFactorIfTileExists(column + 1, row, adjacentTreeGrowthFactor * growthFactorModifier);
		// South
		addGrowthFactorIfTileExists(column, row - 1, adjacentTreeGrowthFactor * growthFactorModifier);

		// Southwest
		addGrowthFactorIfTileExists(column - 1, row - 1, cornerTreeGrowthFactor * growthFactorModifier);
		// Northwest
		addGrowthFactorIfTileExists(column - 1, row + 1, cornerTreeGrowthFactor * growthFactorModifier);
		// Northeast
		addGrowthFactorIfTileExists(column + 1, row + 1, cornerTreeGrowthFactor * growthFactorModifier);
		// Southeast
		addGrowthFactorIfTileExists(column + 1, row - 1, cornerTreeGrowthFactor * growthFactorModifier);
	}

	private void addGrowthFactorIfTileExists(int column, int row, int growthFactor) {
		if (tileExists(column, row)) {
			terrainGrowthScores[column][row] += growthFactor;
		}
	}

	private boolean tileExists(int column, int row) {
		return column >= 0 && column < terrainGrowthScores.length
				&& row >= 0 && row < terrainGrowthScores[0].length;
	}

	/**
	 * Searches the terrain growth scores array for the highest scores, and stores them
	 * in the next growth targets queue.
	 */
	private void findHighestScores(World world) {
		var scores = new ArrayList<TerrainGrowthScore>();
		for (int column = 0; column < terrainGrowthScores.length; column++) {
			for (int row = 0; row < terrainGrowthScores[0].length; row++) {
				byte score = terrainGrowthScores[column][row];
				if (isGrass(score) && score > grassIndicator) {
					scores.add(new TerrainGrowthScore(column, row, terrainGrowthScores[column][row]));
				}
			}
		}

		// Shuffle to reduce location bias resulting from stable sorting.
		Collections.shuffle(scores);
		scores.sort(null);

		int maxNextTargets = (25 < scores.size()) ? 25 : scores.size();
		for (int targetIndex = scores.size() - 1, targets = 0; targets < maxNextTargets; targetIndex--, targets++) {
			nextGrowthTargets.add(scores.get(targetIndex));
		}

		world.timer().scheduleSeconds(secondsPerTreeGrowth, this::growNextTree);
	}

	private static boolean isGrass(byte growthFactorScore) {
		return (growthFactorScore & grassIndicator) == grassIndicator;
	}

	private void growNextTree(World world) {
		var nextLocation = nextGrowthTargets.pollLast();
		// If the nextGrowthTargets queue is empty, refill it.
		if (nextLocation == null) {
			findHighestScores(world);
		} else {
			if (world.getTerrainImprovement(nextLocation.column, nextLocation.row) == null) {
				var args = new Entity.ConstructionArgs(
						nextLocation.column * Units.TileToWorldScale,
						nextLocation.row * Units.TileToWorldScale,
						0);
				world.addEntity(Tree.class, args);

				Systems.network().send(new CreateEntity(Tree.class, args));
			}

			world.timer().scheduleSeconds(secondsPerTreeGrowth, this::growNextTree);
		}
	}

	private static class TerrainGrowthScore implements Comparable<TerrainGrowthScore> {
		final short column;
		final short row;
		final byte growthScore;

		TerrainGrowthScore(int column, int row, byte growthScore) {
			this.column = (short) column;
			this.row = (short) row;
			this.growthScore = growthScore;
		}

		@Override
		public int compareTo(TerrainGrowthScore o) {
			return Byte.compare(growthScore, o.growthScore);
		}
	}
}
