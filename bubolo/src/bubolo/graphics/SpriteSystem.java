package bubolo.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bubolo.world.Base;
import bubolo.world.Bullet;
import bubolo.world.Crater;
import bubolo.world.DeepWater;
import bubolo.world.Entity;
import bubolo.world.Grass;
import bubolo.world.Building;
import bubolo.world.Mine;
import bubolo.world.MineExplosion;
import bubolo.world.Pillbox;
import bubolo.world.Road;
import bubolo.world.Rubble;
import bubolo.world.Spawn;
import bubolo.world.Swamp;
import bubolo.world.Tank;
import bubolo.world.Tree;
import bubolo.world.Wall;
import bubolo.world.Water;

/**
 * Contains methods for adding new sprites.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
class SpriteSystem {
	private Map<Class<? extends Entity>, SpriteFactory> spriteFactories;

	private List<Sprite> sprites = new ArrayList<Sprite>();

	SpriteSystem() {
		spriteFactories = setSpriteFactories();
	}

	/**
	 * Returns a reference to the list of sprites. Package-private because this method should not be accessed outside of
	 * the Graphics system.
	 *
	 * @return the list of all sprites.
	 */
	List<Sprite> getSprites() {
		return sprites;
	}

	/**
	 * Creates a new sprite based on the type of entity provided.
	 *
	 * @param graphics reference to the graphics system.
	 * @param entity reference to an entity.
	 * @return reference to the new sprite.
	 */
	public Sprite createSprite(Graphics graphics, Entity entity) {
		if (!spriteFactories.containsKey(entity.getClass())) {
			throw new IllegalStateException("createSprite is unable to create a sprite from entity type "
					+ entity.getClass().getName() + " because no factory exists.");
		}

		Sprite sprite = spriteFactories.get(entity.getClass()).create(graphics, entity);
		sprites.add(sprite);
		return sprite;
	}

	/**
	 * Adds a sprite that is not attached to an entity.
	 *
	 * @param sprite the sprite to add.
	 */
	void addSprite(Sprite sprite) {
		sprites.add(sprite);
	}

	/**
	 * Removes the specified sprite.
	 *
	 * @param sprite the sprite to remove.
	 */
	public void removeSprite(Sprite sprite) {
		sprites.remove(sprite);
	}

	/**
	 * Wrapper for sprite creation functions.
	 *
	 * @author BU CS673 - Clone Productions
	 */
	private interface SpriteFactory {
		/**
		 * Executes the sprite creation function.
		 *
		 * @param graphics reference to the graphics system.
		 * @param e reference to the entity that the sprite represents.
		 * @return reference to the new sprite.
		 */
		Sprite create(Graphics graphics, Entity e);
	}

	/**
	 * Creates the sprite factory objects, which map concrete classes to sprite creation.
	 *
	 * @return map of the concrete classes to sprite creator classes.
	 */
	private static Map<Class<? extends Entity>, SpriteFactory> setSpriteFactories() {
		Map<Class<? extends Entity>, SpriteFactory> factories = new HashMap<>();

		factories.put(Base.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new BaseSprite((Base) e);
			}
		});

		factories.put(Bullet.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new BulletSprite((Bullet) e);
			}
		});

		factories.put(Crater.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new CraterSprite(graphics, (Crater) e);
			}
		});

		factories.put(DeepWater.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new DeepWaterSprite((DeepWater) e);
			}
		});

		factories.put(Grass.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new GrassSprite((Grass) e);
			}
		});

		factories.put(Building.class, (graphics, e) -> {
			return new BuildingSprite((Building) e);
		});

		factories.put(Mine.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new MineSprite((Mine) e);
			}
		});

		factories.put(MineExplosion.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new MineExplosionSprite(graphics, (MineExplosion) e);
			}
		});

		factories.put(Pillbox.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new PillboxSprite((Pillbox) e);
			}
		});

		factories.put(Road.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new RoadSprite((Road) e);
			}
		});

		factories.put(Rubble.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new RubbleSprite((Rubble) e);
			}
		});

		factories.put(Swamp.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new SwampSprite((Swamp) e);
			}
		});

		factories.put(Tank.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new TankSprite((Tank) e);
			}
		});

		factories.put(Tree.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new TreeSprite((Tree) e);
			}
		});

		factories.put(Wall.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new WallSprite((Wall) e);
			}
		});

		factories.put(Water.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new WaterSprite((Water) e);
			}

		});

		factories.put(Spawn.class, new SpriteFactory() {
			@Override
			public Sprite create(Graphics graphics, Entity e) {
				return new SpawnSprite((Spawn) e);
			}
		});

		return factories;
	}
}
