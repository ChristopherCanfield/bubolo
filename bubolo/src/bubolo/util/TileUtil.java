/**
 *
 */

package bubolo.util;

import java.util.Arrays;

import bubolo.world.Entity;
import bubolo.world.Terrain;
import bubolo.world.TerrainImprovement;
import bubolo.world.World;

public abstract class TileUtil
{
//	/**
//	 * get a list of entities that are currently colliding with a given entity
//	 *
//	 * @param entity
//	 * 			the entity to check for collisions
//	 * @param world
//	 * 			reference to the game world
//	 * @return
//	 * 			the list of entities that are colliding with the given entity
//	 */
//	public static List<OldEntity> getLocalCollisions(OldEntity entity, World world)
//	{
//		ArrayList<OldEntity> localCollisions = new ArrayList<OldEntity>();
//		getLocalEntities(entity.getX(),entity.getY(),world);
//
//		for(OldEntity collider:TileUtil.getLocalEntities(entity.getX(),entity.getY(), world))
//		{
//			if (collider.isSolid() && collider != entity)
//			{
//				if (Intersector.overlapConvexPolygons(collider.getBounds(), entity.getBounds()))
//				{
//					localCollisions.add(collider);
//				}
//			}
//		}
//
//		return localCollisions;
//	}
//
//	/**
//	 * Get all entities are likely to overlap with Entities within the given grid
//	 * location.
//	 *
//	 * @param gridX
//	 *            is the X index of the target grid location.
//	 * @param gridY
//	 *            is the Y index of the target grid location.
//	 * @param w
//	 *            is the World in which the Entities reside.
//	 * @return a List of all Entities which could be near the target location.
//	 */
//	public static List<OldEntity> getLocalEntities(int gridX, int gridY, World w)
//	{
//		ArrayList<OldEntity> localEnts = new ArrayList<OldEntity>();
//		Tile[][] worldTiles = w.getTiles();
//		if (worldTiles == null)
//		{
//			localEnts.addAll(w.getEntities());
//		}
//		else
//		{
//			int startX = gridX - LOCAL_TILE_DISTANCE;
//			int startY = gridY - LOCAL_TILE_DISTANCE;
//			for (int i = 0; i < 5; i++)
//			{
//				for (int j = 0; j < 5; j++)
//				{
//					if (isValidTile(startX + i, startY + j, w))
//					{
//						Tile targetTile = worldTiles[startX + i][startY + j];
//						localEnts.add(targetTile.getTerrain());
//						if (targetTile.hasElement())
//						{
//							localEnts.add(targetTile.getElement());
//						}
//					}
//				}
//			}
//			if (w.getActors() != null)
//			{
//				localEnts.addAll(w.getActors());
//			}
//			if (w.getEffects() != null)
//			{
//				localEnts.addAll(w.getEffects());
//			}
//		}
//		return localEnts;
//	}

	private static boolean matchesType(int column, int row, World world, Class<?>[] targetTypes)
	{
		if (world.isValidTile(column, row)) {
			Terrain terrain = world.getTerrain(column, row);
			boolean containsTarget = Arrays.stream(targetTypes).anyMatch(type -> type.equals(terrain.getClass()));
			if (!containsTarget) {
				TerrainImprovement ti = world.getTerrainImprovement(column, row);
				if (ti != null) {
					containsTarget = Arrays.stream(targetTypes).anyMatch(type -> type.equals(ti.getClass()));
				}
			}

			return containsTarget;
		}
		return false;
	}

	private static final boolean[] tilingStateArray = new boolean[4];

	/**
	 * Returns the adaptive tiling state of an object located at the specified tile, given
	 * the list of Classes that the algorithm should consider 'matches'.
	 *
	 * @param t
	 *            is the Tile where the object to be checked is contained.
	 * @param w
	 *            is the World object where the object, and any objects to be checked
	 *            against, reside.
	 * @param targetClasses
	 *            is an Array of any class which should be considered a 'match' -- that
	 *            is, tiles that contain any of the Class types listed in this array will
	 *            be considered a match for the purposes of determining the adaptive
	 *            tiling state of the specified Tile.
	 * @return an integer representing the correct adaptive tiling state for the specified
	 *         tile, according to the adaptive tiling mechanism outlined on the project wiki.
	 */
	public static int getTilingState(Entity t, World w, Class<?>[] targetClasses)
	{
		int stateSum = 0;

		getEdgeMatches(tilingStateArray, t, w, targetClasses);
		boolean[] edges = tilingStateArray;

		// Match above
		if (edges[0]) {
			stateSum += 1;
		}

		// Match below
		if (edges[1]) {
			stateSum += 2;
		}

		// Match left
		if (edges[2]) {
			stateSum += 4;
		}

		// Match right
		if (edges[3]) {
			stateSum += 8;
		}

		return stateSum;
	}

	/**
	 * Populates an array of booleans, representing whether the Tiles immediately
	 * above, below, to the left, and to the right of the target Tile contain objects of a
	 * Class matching those specified in the targetClasses array.
	 *
	 * @param destinationArray a preallocated boolean array of size 4. The results will be placed into this array.
	 * @param entity the entity to check.
	 * @param w reference to the game world.
	 * @param targetClasses an Array of class that will be checked against. That is, tiles that contain any of the
	 * 			Class types listed in this array will be considered a match for the purposes of determining the adaptive
	 *          tiling state of the specified entity.
	 */
	public static void getEdgeMatches(boolean[] destinationArray, Entity entity, World w, Class<?>[] targetClasses)
	{
		assert destinationArray.length == 4;

		int col = entity.tileColumn();
		int row = entity.tileRow();

		destinationArray[0] = matchesType(col, row + 1, w, targetClasses);
		destinationArray[1] = matchesType(col, row - 1, w, targetClasses);
		destinationArray[2] = matchesType(col - 1, row, w, targetClasses);
		destinationArray[3] = matchesType(col + 1, row, w, targetClasses);
	}

	/**
	 * Populates an array of booleans, representing whether the Tiles to the top
	 * left, top right, bottom left, and bottom right of the specified tile contain
	 * objects of a Class matching those specified in the targetClasses array.
	 *
	 * @param destinationArray a preallocated boolean array of size 4. The results will be placed into this array.
	 * @param entity the entity to check.
	 * @param w reference to the game world.
	 * @param targetClasses an Array of class that will be checked against. That is, tiles that contain any of the
	 * 			Class types listed in this array will be considered a match for the purposes of determining the adaptive
	 *          tiling state of the specified entity.
	 */
	public static void getCornerMatches(boolean[] destinationArray, Entity entity, World w, Class<?>[] targetClasses)
	{
		assert destinationArray.length == 4;

		int col = entity.tileColumn();
		int row = entity.tileRow();

		destinationArray[0] = matchesType(col - 1, row + 1, w, targetClasses);
		destinationArray[1] = matchesType(col + 1, row + 1, w, targetClasses);
		destinationArray[2] = matchesType(col - 1, row - 1, w, targetClasses);
		destinationArray[3] = matchesType(col + 1, row - 1, w, targetClasses);
	}
}
