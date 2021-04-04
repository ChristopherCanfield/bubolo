package bubolo.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonKey;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import bubolo.Config;
import bubolo.util.Coords;
import bubolo.util.Nullable;
import bubolo.world.Entity;
import bubolo.world.EntityCreationObserver;
import bubolo.world.GameWorld;
import bubolo.world.World;
import bubolo.world.entity.concrete.Base;
import bubolo.world.entity.concrete.Crater;
import bubolo.world.entity.concrete.DeepWater;
import bubolo.world.entity.concrete.Grass;
import bubolo.world.entity.concrete.Mine;
import bubolo.world.entity.concrete.Pillbox;
import bubolo.world.entity.concrete.Road;
import bubolo.world.entity.concrete.Rubble;
import bubolo.world.entity.concrete.Spawn;
import bubolo.world.entity.concrete.Swamp;
import bubolo.world.entity.concrete.Tree;
import bubolo.world.entity.concrete.Wall;
import bubolo.world.entity.concrete.Water;

/**
 * Imports the Tiled-generated map.
 *
 * @author Christopher D. Canfield
 * @since 0.4.0
 */
public class MapImporter {
	/**
	 * A tileset in the Tiled-generated map. The name and tiles are set before importing the map. The
	 * firstGid is set by the importer.
	 */
	private static class Tileset {
		final String name;
		/**
		 * Map of tile IDs to Entity creation functions. The tile ID is the tile's gid minus the tileset's firstGid.
		 */
		final Map<Integer, BiFunction<World, Entity.ConstructionArgs, Entity>> tiles = new HashMap<>();

		int firstGid;

		Tileset(String name) {
			this.name = name;
		}

		int lastGid() {
			return firstGid + tiles.size() - 1;
		}

		/**
		 * Whether the specified tile global ID belongs to this tileset.
		 * @param tileGid the tile global ID to check.
		 */
		boolean isGidInThisTileset(int tileGid) {
			// If the firstGid wasn't set, always return false. This happens when the tileset wasn't found
			// in the json map file. That's fine, because there are multiple tileset layouts available in the map files.
			if (firstGid == 0) {
				return false;
			}
			return tileGid >= firstGid && tileGid <= lastGid();
		}
	}

	/**
	 * Map import results, including the fully instantiated World and diagnostic information.
	 *
	 * @author Christopher D. Canfield
	 */
	public static class Diagnostics {
		Set<String> typesImported = new HashSet<>();

		int tileHeight;
		int tileWidth;

		int layerCount;
		int tilesetCount;

		public Set<String> typesImported() {
			return typesImported;
		}

		public int tileHeight() {
			return tileHeight;
		}

		public int tileWidth() {
			return tileWidth;
		}

		public int layerCount() {
			return layerCount;
		}

		public int tilesetCount() {
			return tilesetCount;
		}
	}

	private static final String DefaultExceptionMessage = "Error parsing the json map file";

	private final Map<String, Tileset> tilesets = new HashMap<>();

	public MapImporter() {
		// Add the known map tiles here.

		Tileset stationaryElements = new Tileset("bubolo_tilset_stationaryElements");
		stationaryElements.tiles.put(0, (world, args) -> world.addEntity(Pillbox.class, args));
		stationaryElements.tiles.put(1, (world, args) -> world.addEntity(Tree.class, args));
		stationaryElements.tiles.put(2, (world, args) -> world.addEntity(Mine.class, args));
		stationaryElements.tiles.put(3, (world, args) -> world.addEntity(Wall.class, args));
		stationaryElements.tiles.put(4, (world, args) -> world.addEntity(Base.class, args));
		stationaryElements.tiles.put(5, (world, args) -> world.addEntity(Crater.class, args));
		stationaryElements.tiles.put(6, (world, args) -> world.addEntity(Rubble.class, args));
		stationaryElements.tiles.put(7, (world, args) -> world.addEntity(Spawn.class, args));
		tilesets.put(stationaryElements.name, stationaryElements);

		Tileset terrain = new Tileset("bubolo_tilset_terrain");
		terrain.tiles.put(0, (world, args) -> world.addEntity(Grass.class, args));
		terrain.tiles.put(1, (world, args) -> world.addEntity(Swamp.class, args));
		terrain.tiles.put(2, (world, args) -> world.addEntity(Water.class, args));
		terrain.tiles.put(3, (world, args) -> world.addEntity(DeepWater.class, args));
		terrain.tiles.put(4, (world, args) -> world.addEntity(Road.class, args));
		tilesets.put(terrain.name, terrain);

		// Tilesets for the original layout, which put rubble and craters in the terrain category.
		// Needed so the Everard Island map will continue to work.
		Tileset stationaryElements_oldLayout = new Tileset("bubolo_tilset_stationaryElements_oldLayout");
		stationaryElements_oldLayout.tiles.put(0, (world, args) -> world.addEntity(Pillbox.class, args));
		stationaryElements_oldLayout.tiles.put(1, (world, args) -> world.addEntity(Tree.class, args));
		stationaryElements_oldLayout.tiles.put(2, (world, args) -> world.addEntity(Mine.class, args));
		stationaryElements_oldLayout.tiles.put(3, (world, args) -> world.addEntity(Wall.class, args));
		stationaryElements_oldLayout.tiles.put(4, (world, args) -> world.addEntity(Base.class, args));
		stationaryElements_oldLayout.tiles.put(5, (world, args) -> world.addEntity(Spawn.class, args));
		tilesets.put(stationaryElements_oldLayout.name, stationaryElements_oldLayout);

		Tileset terrain_oldLayout = new Tileset("bubolo_tilset_terrain_oldLayout");
		terrain_oldLayout.tiles.put(0, (world, args) -> world.addEntity(Grass.class, args));
		terrain_oldLayout.tiles.put(1, (world, args) -> world.addEntity(Swamp.class, args));
		terrain_oldLayout.tiles.put(2, (world, args) -> world.addEntity(Water.class, args));
		terrain_oldLayout.tiles.put(3, (world, args) -> world.addEntity(DeepWater.class, args));
		terrain_oldLayout.tiles.put(4, (world, args) -> world.addEntity(Road.class, args));
		terrain_oldLayout.tiles.put(5, (world, args) -> world.addEntity(Crater.class, args));
		terrain_oldLayout.tiles.put(6, (world, args) -> world.addEntity(Rubble.class, args));
		tilesets.put(terrain_oldLayout.name, terrain_oldLayout);
	}

	/**
	 * The Tiled map keys that are relevant to us.
	 */
	enum Key implements JsonKey {
		MapHeight("height"),
		MapWidth("width"),
		Tilesets("tilesets"),
		Layers("layers"),

		Data("data"),

		Name("name"),
		FirstGid("firstgid");

		private String key;

		private Key(String key) {
			this.key = key;
		}

		@Override
		public String getKey(){
			return key;
		}

		@Override
		public Object getValue(){
			return null;
		}
	}

	public static record Result(World world, Diagnostics diagnostics) {
	}

	/**
	 * Imports the json Tiled map, and constructs a world from the data.
	 *
	 * @param mapPath path to the json Tiled map file.
	 * @return a Pair that contains the world and diagnostic information.
	 * @throws IOException if the provided path can't be opened.
	 * @throws InvalidMapException if the json Tiled map is malformed.
	 */
	public Result importJsonMapWithDiagnostics(Path mapPath) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(mapPath)) {
			return importJsonMapWithDiagnostics(reader);
		}
	}

	/**
	 * Imports the json Tiled map, and constructs a world from the data.
	 *
	 * @param mapPath path to the json Tiled map file.
	 * @param entityCreationObserver an observer that will be attached to the world.
	 * @return the fully constructed world.
	 * @throws IOException if the provided path can't be opened.
	 * @throws InvalidMapException if the json Tiled map is malformed.
	 */
	public World importJsonMap(Path mapPath, EntityCreationObserver entityCreationObserver) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(mapPath)) {
			return importMap(reader, entityCreationObserver).world();
		}
	}

	public Result importJsonMapWithDiagnostics(Reader mapReader) {
		return importMap(mapReader, null);
	}

	private Result importMap(Reader mapReader, @Nullable EntityCreationObserver entityCreationObserver) {
		try {
			JsonObject jsonTiledMap = (JsonObject) Jsoner.deserialize(mapReader);
			jsonTiledMap.requireKeys(Key.MapHeight, Key.MapWidth, Key.Tilesets, Key.Layers);

			Diagnostics diagnostics = new Diagnostics();

			setTilesetFirstGids(jsonTiledMap, diagnostics);

			// Get the map height and width, in tiles.
			final int tileColumns = diagnostics.tileWidth = jsonTiledMap.getInteger(Key.MapWidth);
			final int tileRows = diagnostics.tileHeight = jsonTiledMap.getInteger(Key.MapHeight);

			GameWorld world = new GameWorld(tileColumns, tileRows);
			world.setEntityCreationObserver(entityCreationObserver);

			JsonArray layers = (JsonArray) jsonTiledMap.get(Key.Layers.getKey());
			diagnostics.layerCount = layers.size();
			// Iterate through each map layer.
			for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++) {
				JsonObject layer = (JsonObject) layers.get(layerIndex);
				JsonArray layerTiles = (JsonArray) layer.get(Key.Data.getKey());

				// Iterate through each tile GID in the map layer.
				for (int row = 0; row < tileRows; row++) {
					for (int col = 0; col < tileColumns; col++) {
						int tileGid = layerTiles.getInteger(row * tileColumns + col);
						addEntityIfGidRecognized(tileGid, world, row, col, diagnostics);
					}
				}
			}

			// Populate any empty terrain tiles with grass. This allows slightly malformed maps, such as the Everard Island map,
			// to work properly.
			world.populateEmptyTilesWith(Grass.class);

			return new Result(world, diagnostics);
		} catch (JsonException e) {
			throw new InvalidMapException(DefaultExceptionMessage, e);
		} catch (NoSuchElementException e) {
			throw new InvalidMapException(DefaultExceptionMessage + e.toString(), e);
		}
	}

	void setTilesetFirstGids(JsonObject jsonTiledMap, Diagnostics diagnostics) {
		JsonArray jsonTilesets = (JsonArray) jsonTiledMap.get(Key.Tilesets.getKey());
		if (jsonTilesets.size() < 2) {
			throw new InvalidMapException(DefaultExceptionMessage + " There should be two tilesets, but " + jsonTilesets.size() + " was found.");
		}

		for (Object ts : jsonTilesets) {
			JsonObject jsonTileset = (JsonObject) ts;
			jsonTileset.requireKeys(Key.Name, Key.FirstGid);

			String tilesetName = jsonTileset.getString(Key.Name);
			Tileset tileset = tilesets.get(tilesetName);
			if (tileset != null) {
				tileset.firstGid = jsonTileset.getInteger(Key.FirstGid);
				diagnostics.tilesetCount++;

			// Log a warning for unknown tileset, and then skip it.
			} else {
				Logger.getLogger(Config.AppProgramaticTitle).warning("Unknown tileset found in map file: " + tilesetName);
			}
		}
	}

	void addEntityIfGidRecognized(int tileGid, World world, int row, int col, Diagnostics diagnostics) {
		// Zero represents an empty space in the layer, so skip it if encountered.
		if (tileGid > 0) {
			// Check the tile GID against the known GIDs in each tileset.
			for (Tileset ts : tilesets.values()) {
				// Add the entity if it is known to this tileset.
				if (ts.isGidInThisTileset(tileGid)) {
					// The game world is flipped from json map indexes.
					int posY = row * Coords.TileToWorldScale;
					int posX = col * Coords.TileToWorldScale;
					double rotation = Math.PI / 2.0;

					// The x and y coords are flipped in the map.
					var args = new Entity.ConstructionArgs(UUID.randomUUID(), posX, posY, (float) rotation);

					System.out.println("Adding " + args);

					Entity entity = ts.tiles.get(tileGid - ts.firstGid).apply(world, args);

					diagnostics.typesImported.add(entity.getClass().getSimpleName());
				}
			}
		}
	}
}
