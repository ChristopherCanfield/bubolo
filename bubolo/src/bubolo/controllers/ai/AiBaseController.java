package bubolo.controllers.ai;

import bubolo.controllers.ActorEntityController;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.ChangeOwner;
import bubolo.world.Base;
import bubolo.world.Tank;
import bubolo.world.World;

/**
 * A controller for bases. This controller checks for contact with its owner and heals and
 * reloads the owner accordingly.
 *
 * @author BU CS673 - Clone Productions
 */
public class AiBaseController extends ActorEntityController<Base>
{
	/**
	 * Time allowed between supply orders
	 */
	private static final long resupplyDelayTime = 500;

	/**
	 * Time since last supply order
	 */
	private long nextSupplyTime = 0;

	/**
	 * Time allowed for base to gain supplies
	 */
	private static final long replinishTime = 750;

	/**
	 * How many Hit Points the base can heal per update
	 */
	private static final int HIT_POINTS_PER_HEAL = 10;

	/**
	 * Time since the last replenishment
	 */
	private long lastReplinishment = 0;

	/**
	 * constructs an AI Base controller
	 *
	 * @param base
	 *            the base this controller will correspond to.
	 */
	public AiBaseController(Base base) {
		super(base);
	}

	@Override
	public void update(World world)
	{
		Base base = parent();

		base.setCharging(false);
		for (Tank tank : world.getTanks()) {
			if (tank.overlapsEntity(base)) {
				// Base has an owner.
				if (base.hasOwner()) {
					if(tank.equals(base.owner()) && !isTankRecharged(tank)) {
						base.setCharging(true);

						if(System.currentTimeMillis() > nextSupplyTime) {
							nextSupplyTime = System.currentTimeMillis() + resupplyDelayTime;
							if (tank.hitPoints() < tank.maxHitPoints()) {
								tank.heal(base.giveHitPoints());
							}
							if(tank.ammoCount() < tank.getTankMaxAmmo()) {
								tank.collectAmmo(base.giveAmmo());
							}
							if(tank.mineCount() < tank.getTankMaxMineCount()) {
								tank.collectMines(base.giveMine());
							}
						}
					}

				// Base does not have an existing owner.
				} else {
					base.setOwner(tank);
					base.heal(100);

					if (tank.isOwnedByLocalPlayer() && !base.isOwnedByLocalPlayer()) {
						base.setOwnedByLocalPlayer(true);
						base.setOwner(tank);

						Network net = NetworkSystem.getInstance();
						net.send(new ChangeOwner(base));
					}
				}
			}
		}

		if(System.currentTimeMillis() - lastReplinishment < replinishTime)
		{
			base.heal(HIT_POINTS_PER_HEAL);
			base.gatherAmmo();
			base.gatherMines();
		}
	}

	private static boolean isTankRecharged(Tank tank) {
		return tank.hitPoints() >= tank.maxHitPoints()
				&& tank.ammoCount() >= tank.getTankMaxAmmo()
				&& tank.mineCount() >= tank.getTankMaxMineCount();
	}
}