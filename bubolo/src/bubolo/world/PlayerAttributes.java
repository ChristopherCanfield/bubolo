package bubolo.world;

import java.util.UUID;

import bubolo.graphics.TeamColor;

/**
 * A read-only view into a player's attributes.
 *
 * @author Christopher D. Canfield
 */
public interface PlayerAttributes {
	UUID id();
	String name();
	TeamColor color();
	boolean isLocal();
}
