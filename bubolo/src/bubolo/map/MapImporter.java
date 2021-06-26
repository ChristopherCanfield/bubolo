package bubolo.map;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonKey;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import bubolo.Config;
import bubolo.util.Units;
import bubolo.world.Base;
import bubolo.world.Building;
import bubolo.world.Crater;
import bubolo.world.DeepWater;
import bubolo.world.Entity;
import bubolo.world.GameWorld;
import bubolo.world.Grass;
import bubolo.world.Mine;
import bubolo.world.Pillbox;
import bubolo.world.Road;
import bubolo.world.Rubble;
import bubolo.world.Spawn;
import bubolo.world.Swamp;
import bubolo.world.Tree;
import bubolo.world.Wall;
import bubolo.world.Water;
import bubolo.world.World;

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
		stationaryElements.tiles.put(8, (world, args) -> world.addEntity(Building.class, args));
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
		stationaryElements_oldLayout.tiles.put(6, (world, args) -> world.addEntity(Building.class, args));
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
		CustomProperties("properties"),
		CustomPropertyName("name"),
		CustomPropertyValue("value"),

		MapHeight("height"),
		MapWidth("width"),
		Tilesets("tilesets"),
		Layers("layers"),

		Data("data"),

		TilesetName("name"),
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

	/**
	 * @return a list of paths to map files.
	 * @throws IOException if the maps folder does not exist.
	 */
	public List<Path> loadMapFilePaths() throws IOException {
		var maps = new ArrayList<Path>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Config.MapsPath, "*.json")) {
			for (Path entry: stream) {
				maps.add(entry);
			}
			return maps;
		}
	}

	public static record MapInfo(Path fullPath, String mapName, String author, String description, int tileColumns, int tileRows, String lastUpdated) {
	}

	/**
	 * Loads information about a map, including:
	 * <ul>
	 * 	<li>Name ("mapName" json field, or the file name)</li>
	 * 	<li>Author ("author" json field, or "Unknown" if no author was provided)</li>
	 * 	<li>Description ("description" json field, or an empty string if no description was provided)</li>
	 * 	<li>Tile columns ("width" json field)</li>
	 * 	<li>Tile rows ("height" json field)</li>
	 * 	<li>Last updated (from the file's last modified date)
	 * </ul>
	 *
	 * @param mapPath the full path and file name to the map.
	 * @return a populated MapInfo object.
	 * @throws IOException if the file doesn't exist or is corrupted.
	 * @throws InvalidMapException if the file doesn't have width or height json fields.
	 */
	public MapInfo loadMapInfo(Path mapPath) throws IOException {
		if (!Files.exists(mapPath)) {
			throw new FileNotFoundException("Unable to find map " + mapPath.getFileName().toString());
		}

		var lastModifiedTime = Files.getLastModifiedTime(mapPath);
		String lastUpdated = lastModifiedTime.toString();

		try (BufferedReader mapReader = Files.newBufferedReader(mapPath)) {
			JsonObject jsonTiledMap = (JsonObject) Jsoner.deserialize(mapReader);
			jsonTiledMap.requireKeys(Key.MapHeight, Key.MapWidth);

			int tileColumns = jsonTiledMap.getInteger(Key.MapWidth);
			int tileRows = jsonTiledMap.getInteger(Key.MapHeight);

			String mapName = mapPath.getFileName().toString();
			String author = "Unknown";
			String description = "";
			Collection<JsonObject> customProperties = jsonTiledMap.getCollection(Key.CustomProperties);
			for (JsonObject property : customProperties) {
				var propertyName = property.getString(Key.CustomPropertyName);
				switch (propertyName) {
					case "mapName" -> mapName = property.getString(Key.CustomPropertyValue);
					case "author" -> author = property.getString(Key.CustomPropertyValue);
					case "description" -> description = property.getString(Key.CustomPropertyValue);
				}
			}

			return new MapInfo(mapPath, mapName, author, description, tileColumns, tileRows, lastUpdated);

		} catch (JsonException e) {
			throw new InvalidMapException(e);
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
	 * @return the fully constructed world.
	 * @throws IOException if the provided path can't be opened.
	 * @throws InvalidMapException if the json Tiled map is malformed.
	 */
	public World importJsonMap(Path mapPath) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(mapPath)) {
			return importMap(reader).world();
		}
	}

	public Result importJsonMapWithDiagnostics(Reader mapReader) {
		return importMap(mapReader);
	}

	private Result importMap(Reader mapReader) {
		try {
			JsonObject jsonTiledMap = (JsonObject) Jsoner.deserialize(mapReader);
			jsonTiledMap.requireKeys(Key.MapHeight, Key.MapWidth, Key.Tilesets, Key.Layers);

			Diagnostics diagnostics = new Diagnostics();

			setTilesetFirstGids(jsonTiledMap, diagnostics);

			// Get the map height and width, in tiles.
			final int tileColumns = diagnostics.tileWidth = jsonTiledMap.getInteger(Key.MapWidth);
			final int tileRows = diagnostics.tileHeight = jsonTiledMap.getInteger(Key.MapHeight);

			GameWorld world = new GameWorld(tileColumns, tileRows);

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

			// Process a game tick, which finalizes the addition of the new entities to the world.
			world.update();

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
			jsonTileset.requireKeys(Key.TilesetName, Key.FirstGid);

			String tilesetName = jsonTileset.getString(Key.TilesetName);
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
					// The game world is flipped from json map indexes (zero is the top in the map file, but the bottom in the world map).
					int posY = (world.getTileRows() - row - 1) * Units.TileToWorldScale;
					int posX = col * Units.TileToWorldScale;
					float rotation = 0;

					// The x and y coords are flipped in the map.
					var args = new Entity.ConstructionArgs(Entity.nextId(), posX, posY, rotation);
					Entity entity = ts.tiles.get(tileGid - ts.firstGid).apply(world, args);

					diagnostics.typesImported.add(entity.getClass().getSimpleName());
				}
			}
		}
	}
}
