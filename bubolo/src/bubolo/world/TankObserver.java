package bubolo.world;

/**
 * Observes a tank. Call {@code tank.setTankObserver} to assocate a TankObserver with a tank.
 *
 * @author Christopher D. Canfield
 */
public interface TankObserver {
	void onTankAmmoCountChanged(int ammo);

	void onTankMineCountChanged(int mines);

	void onTankSpeedChanged(float speedWorldUnits, float speedKph);
}
