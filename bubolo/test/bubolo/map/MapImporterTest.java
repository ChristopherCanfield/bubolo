package bubolo.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.Test;

import bubolo.util.Units;
import bubolo.world.World;

public class MapImporterTest {

	@Test
	public void importAllTerrainTypes() throws IOException {
		MapImporter importer = new MapImporter();
		Path mapPath = FileSystems.getDefault().getPath("res", "maps/Test/AllTerrainTypes.json");
		var results = importer.importJsonMapWithDiagnostics(mapPath);

		World world = results.world();
		MapImporter.Diagnostics diagnostics = results.diagnostics();

		assertNotNull(world);
		assertEquals(8, world.getWidth() / Units.TileToWorldScale);
		assertEquals(2, world.getHeight() / Units.TileToWorldScale);
		assertEquals(3, diagnostics.layerCount());
		assertEquals(2, diagnostics.tilesetCount());
		assertEquals(13, diagnostics.typesImported().size());
		assertEquals(8, diagnostics.tileWidth());
		assertEquals(2, diagnostics.tileHeight());
	}

	@Test
	public void importCanfieldIsland() throws IOException {
		MapImporter importer = new MapImporter();
		Path mapPath = FileSystems.getDefault().getPath("res", "maps/Canfield Island.json");
		var results = importer.importJsonMapWithDiagnostics(mapPath);

		World world = results.world();
		MapImporter.Diagnostics diagnostics = results.diagnostics();

		assertNotNull(world);
		assertEquals(114, world.getWidth() / Units.TileToWorldScale);
		assertEquals(64, world.getHeight() / Units.TileToWorldScale);
		assertEquals(3, diagnostics.layerCount());
		assertEquals(2, diagnostics.tilesetCount());
		assertEquals(11, diagnostics.typesImported().size());
		assertEquals(114, diagnostics.tileWidth());
		assertEquals(64, diagnostics.tileHeight());
	}

	@Test
	public void loadMapInfoForCanfieldIsland() throws IOException {
		MapImporter importer = new MapImporter();
		Path mapPath = FileSystems.getDefault().getPath("res", "maps/Canfield Island.json");
		var mapInfo = importer.loadMapInfo(mapPath);

		assertEquals(114, mapInfo.tileColumns());
		assertEquals(64, mapInfo.tileRows());
		assertEquals("Christopher Canfield & Brandon Thompson", mapInfo.author());
		assertEquals("An island that contains many distinct zones, including a forest, a port, a protected town, a large lake, and multiple rivers.", mapInfo.description());
		assertEquals(mapPath, mapInfo.fullPath());
		assertEquals("Canfield Island", mapInfo.mapName());
	}

	@Test
	public void loadMapFilePaths() throws IOException {
		MapImporter importer = new MapImporter();
		var mapPaths = importer.loadMapFilePaths();
		assertFalse(mapPaths.isEmpty());
	}
}
