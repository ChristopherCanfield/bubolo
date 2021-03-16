package bubolo.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.Test;

import bubolo.util.Coords;
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
		assertEquals(8, world.getWidth() / Coords.TILE_TO_WORLD_SCALE);
		assertEquals(2, world.getHeight() / Coords.TILE_TO_WORLD_SCALE);
		assertEquals(3, diagnostics.layerCount());
		assertEquals(2, diagnostics.tilesetCount());
		assertEquals(13, diagnostics.typesImported().size());
		assertEquals(8, diagnostics.tileWidth());
		assertEquals(2, diagnostics.tileHeight());
	}

	@Test
	public void importEverardIsland() throws IOException {
		MapImporter importer = new MapImporter();
		Path mapPath = FileSystems.getDefault().getPath("res", "maps/Everard Island.json");
		var results = importer.importJsonMapWithDiagnostics(mapPath);

		World world = results.world();
		MapImporter.Diagnostics diagnostics = results.diagnostics();

		assertNotNull(world);
		assertEquals(114, world.getWidth() / Coords.TILE_TO_WORLD_SCALE);
		assertEquals(64, world.getHeight() / Coords.TILE_TO_WORLD_SCALE);
		assertEquals(3, diagnostics.layerCount());
		assertEquals(2, diagnostics.tilesetCount());
		assertEquals(10, diagnostics.typesImported().size());
		assertEquals(114, diagnostics.tileWidth());
		assertEquals(64, diagnostics.tileHeight());
	}
}
