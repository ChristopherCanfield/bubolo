package bubolo.mock;

import java.util.List;
import java.util.UUID;

import bubolo.controllers.Controller;
import bubolo.controllers.ControllerFactory;
import bubolo.util.GameLogicException;
import bubolo.util.Timer;
import bubolo.world.ActorEntity;
import bubolo.world.Collidable;
import bubolo.world.Entity;
import bubolo.world.Entity.ConstructionArgs;
import bubolo.world.EntityLifetimeObserver;
import bubolo.world.Mine;
import bubolo.world.Pillbox;
import bubolo.world.Spawn;
import bubolo.world.Tank;
import bubolo.world.Terrain;
import bubolo.world.TerrainImprovement;
import bubolo.world.World;

public class MockWorld implements World {

	@Override
	public Entity getEntity(UUID id) throws GameLogicException {
		return null;
	}

	@Override
	public Entity getEntityOrNull(UUID id) {
		return null;
	}

	@Override
	public List<Entity> getEntities() {
		return null;
	}

	@Override
	public List<Tank> getTanks() {
		return null;
	}

	@Override
	public Spawn getRandomSpawn() {
		return null;
	}

	@Override
	public List<ActorEntity> getActors() {
		return null;
	}

	@Override
	public Timer<World> timer() {
		return null;
	}

	@Override
	public <T extends Entity> T addEntity(Class<T> c, ConstructionArgs args) throws GameLogicException {
		return null;
	}

	@Override
	public <T extends Entity> T addEntity(Class<T> c, ConstructionArgs args, ControllerFactory controllerFactory)
			throws GameLogicException {
		return null;
	}

	@Override
	public <T extends Terrain> void populateEmptyTilesWith(Class<T> terrainType) {
	}

	@Override
	public void addEntityLifetimeObserver(EntityLifetimeObserver observer) {
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getTileColumns() {
		return 0;
	}

	@Override
	public int getTileRows() {
		return 0;
	}

	@Override
	public boolean isValidTile(int column, int row) {
		return false;
	}

	@Override
	public boolean isTileAdjacentToWater(int column, int row) {
		return false;
	}

	@Override
	public void update() {
	}

	@Override
	public Terrain getTerrain(int column, int row) {
		return null;
	}

	@Override
	public TerrainImprovement getTerrainImprovement(int column, int row) {
		return null;
	}

	@Override
	public Mine getMine(int column, int row) {
		return null;
	}

	@Override
	public int getTileDistanceToDeepWater(int tileColumn, int tileRow, int maxDistanceTiles) {
		return 0;
	}

	@Override
	public void addController(Class<? extends Controller> controllerType) {
	}

	@Override
	public void removeController(Class<? extends Controller> controllerType) {
	}

	@Override
	public int getControllerCount() {
		return 0;
	}

	@Override
	public void movePillboxOffTileMap(Pillbox pillbox) {
	}

	@Override
	public void movePillboxOntoTileMap(Pillbox pillbox, int column, int row) {
	}

	@Override
	public List<Collidable> getCollidablesWithinTileDistance(Entity entity, int tileMaxDistance,
			boolean onlyIncludeSolidObjects) {
		return null;
	}

	@Override
	public List<Collidable> getCollidablesWithinTileDistance(Entity entity, int tileMaxDistance,
			boolean onlyIncludeSolidObjects, Class<?> typeFilter) {
		return null;
	}

	@Override
	public Terrain getNearestBuildableTerrain(float x, float y) {
		return null;
	}

	@Override
	public List<Spawn> getRandomSpawns(int count) {
		return null;
	}

	@Override
	public String getZoneFromTile(int column, int row) {
		return null;
	}

	@Override
	public Tank getLocalTank() {
		return null;
	}

	@Override
	public Tank getOwningTank(UUID ownedObjectId) {
		return null;
	}

	@Override
	public String getOwningPlayerName(UUID ownedObjectId) {
		return null;
	}

}
