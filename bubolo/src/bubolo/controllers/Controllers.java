package bubolo.controllers;

import java.util.HashMap;
import java.util.Map;

import bubolo.controllers.ai.AiPillboxController;
import bubolo.controllers.input.GamepadTankController;
import bubolo.controllers.input.KeyboardTankController;
import bubolo.util.Nullable;
import bubolo.world.ActorEntity;
import bubolo.world.Pillbox;
import bubolo.world.Tank;

/**
 * Contains static methods for creating controllers.
 *
 * @author BU CS673 - Clone Productions
 * @author Christopher D. Canfield
 */
public class Controllers {
	private Map<Class<? extends ActorEntity>, ControllerFactory> defaultFactories;

	private static Controllers instance;

	/**
	 * Returns the instance of this singleton.
	 *
	 * @return the instance of this singleton.
	 */
	public static Controllers getInstance() {
		if (instance == null) {
			instance = new Controllers();
		}
		return instance;
	}

	/**
	 * Private constructor to prevent instantiation outside of getInstance().
	 */
	private Controllers() {
		defaultFactories = setDefaultControllerFactories();
	}

	/**
	 * Instantiates controllers for the specified entity. The optional ControllerFactory can be used to specify the exact
	 * controllers that will be created for the entity. Alternatively, passing a null reference will result in the creation of the
	 * default controllers for the entity.
	 *
	 * @param entity reference to the entity.
	 * @param factory reference to a controller factory, or null if the default behavior should be used.
	 * @return true if a controller was attached to the entity, or false otherwise.
	 */
	public boolean createController(ActorEntity entity, @Nullable ControllerFactory factory) {
		ControllerFactory controllerFactory = factory;
		if (controllerFactory == null) {
			controllerFactory = defaultFactories.get(entity.getClass());
		}

		if (controllerFactory != null) {
			controllerFactory.create(entity);
			return true;
		}
		return false;
	}

	/**
	 * Creates a map that maps entity classes to default factories.
	 *
	 * @return reference to the ControllerFactory map.
	 */
	private static Map<Class<? extends ActorEntity>, ControllerFactory> setDefaultControllerFactories() {
		Map<Class<? extends ActorEntity>, ControllerFactory> factories = new HashMap<>();

		factories.put(Tank.class, new ControllerFactory() {
			private static final long serialVersionUID = 1L;

			@Override
			public void create(ActorEntity entity) {
				entity.addController(new KeyboardTankController((Tank) entity));
				entity.addController(new GamepadTankController((Tank) entity));
			}
		});

		factories.put(Pillbox.class, new ControllerFactory() {
			private static final long serialVersionUID = 1L;

			@Override
			public void create(ActorEntity entity) {
				entity.addController(new AiPillboxController((Pillbox) entity));
			}
		});

		return factories;
	}
}
