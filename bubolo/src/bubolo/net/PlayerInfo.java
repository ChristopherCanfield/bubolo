package bubolo.net;

import java.net.InetAddress;

import bubolo.graphics.PlayerColor;
import bubolo.util.Nullable;

/**
 * Player information that is passed to the application's setState method.
 *
 * @param name the player's name.
 * @param color the player's color.
 * @param ipAddress [optional] the player's ip address. May be null.
 *
 * @author Christopher D. Canfield
 */
public record PlayerInfo(String name, PlayerColor color, @Nullable InetAddress ipAddress) {
	public PlayerInfo {
		assert name != null;
		assert color != null;
	}
}
