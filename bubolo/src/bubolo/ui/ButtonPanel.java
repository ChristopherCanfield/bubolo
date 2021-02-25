package bubolo.ui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A panel with Main Menu Buttons
 *
 * @author BU673 - Clone Industries
 */
public class ButtonPanel extends JPanel
{
	private static final long serialVersionUID = -1624357058854582729L;

	/**
	 * The Runnable that should be activated when the Start menu button is pressed.
	 */
	private Runnable singlePlayerTarget;
	private Runnable hostMultiPlayerTarget;
	private Runnable joinMultiPlayerTarget;

	/**
	 * Constructor for the Main Menu Button Panel
	 *
	 * @param singlePlayer
	 *            a runnable that launches the single player game.
	 * @param hostMultiPlayer
	 *            a runnable that launches the host multiplayer game.
	 * @param joinMultiPlayer
	 *            a runnable that launches the client multiplayer game.
	 */
	public ButtonPanel(Runnable singlePlayer, Runnable hostMultiPlayer, Runnable joinMultiPlayer)
	{
		singlePlayerTarget = singlePlayer;
		hostMultiPlayerTarget = hostMultiPlayer;
		joinMultiPlayerTarget = joinMultiPlayer;

		setLayout(new GridLayout(4, 1));

		// Create set of JButtons to be displayed on the Main Menu
		JButton singleBtn = new JButton("NEW SINGLE PLAYER GAME");
		JButton hostMPBtn = new JButton("HOST MULTI-PLAYER GAME");
		JButton joinMPBtn = new JButton("JOIN MULTI-PLAYER GAME");
		JButton exitBtn = new JButton("EXIT");

		// Add the buttons to this panel
		add(singleBtn);
		add(hostMPBtn);
		add(joinMPBtn);
		add(exitBtn);

		// Handle newMPBtn push
		joinMPBtn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				joinMPBtnPerformed();
			}
		});

		// Handle newMPBtn push
		hostMPBtn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				hostMPBtnPerformed();
			}
		});

		// Handle singleBtn push
		singleBtn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				singleBtnPerformed();
			}
		});

		// Handle exitBtn push
		exitBtn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				System.exit(0);
			}
		});
	}

	// public void actionPerformed

	/**
	 * Starts the game by creating a new Thread and running the Runnable passed into the
	 * Constructor. For most implementations, this will launch the primary game thread.
	 */
	private void singleBtnPerformed()
	{
		singlePlayerTarget.run();
	}

	private void joinMPBtnPerformed()
	{
		joinMultiPlayerTarget.run();
	}

	private void hostMPBtnPerformed()
	{
		hostMultiPlayerTarget.run();

	}
}