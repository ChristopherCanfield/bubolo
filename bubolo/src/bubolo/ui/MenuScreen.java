package bubolo.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import bubolo.Config;

/**
 * Initial menu screen, which displays at program launch.
 *
 * @author BU673 - Clone Industries
 */
public class MenuScreen extends JFrame
{
	private static final long serialVersionUID = -5355152035949516532L;

	/**
	 * Constructor for the Main Menu JFrame
	 *
	 * @param singlePlayer
	 *            a runnable that launches the single player game.
	 * @param hostMultiPlayer
	 *            a runnable that launches the host multiplayer game.
	 * @param joinMultiPlayer
	 *            a runnable that launches the client multiplayer game.
	 */
	public MenuScreen(Runnable singlePlayer, Runnable hostMultiPlayer, Runnable joinMultiPlayer)
	{
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		// Set the details of the Frame
		setTitle(Config.AppTitle);
		setSize(500, 500);
		setLayout(new GridLayout(2, 1));
		setLocationRelativeTo(null);
		setIconImage(UserInterface.gameIcon.getImage());
		setResizable(false);

		// Add the MainPanel which contains our logo/title
		add(new MainPanel(), BorderLayout.NORTH);

		// Add the ButtonPanel which contains the main buttons
		add(new ButtonPanel(this, singlePlayer, hostMultiPlayer, joinMultiPlayer), BorderLayout.CENTER);
	}
}
