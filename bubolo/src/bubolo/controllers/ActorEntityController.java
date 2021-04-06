package bubolo.controllers;

import bubolo.world.ActorEntity;

public abstract class ActorEntityController<T extends ActorEntity> implements Controller {
	private final T parent;

	public ActorEntityController(T parent) {
		this.parent = parent;
	}

	protected T parent() {
		return parent;
	}
}
