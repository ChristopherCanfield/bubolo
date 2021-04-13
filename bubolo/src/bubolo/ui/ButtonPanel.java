package bubolo.ui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A panel with Main Menu Buttons
 *
 * @author BU673 - Clone Industries
 */
class ButtonPanel extends JPanel
{
	private static final long serialVersionUID = -1624357058854582729L;

	/**
	 * The Runnable that should be activated when the Start menu button is pressed.
	 */
	private final Runnable singlePlayerTarget;
	private final Runnable hostMultiPlayerTarget;
	private final Runnable joinMultiPlayerTarget;

	private final JFrame parentFrame;

	/**
	 * Constructor for the Main Menu Button Panel
	 *
	 * @param parentFrame the parent JFrame.
	 * @param singlePlayer
	 *            a runnable that launches the single player game.
	 * @param hostMultiPlayer
	 *            a runnable that launches the host multiplayer game.
	 * @param joinMultiPlayer
	 *            a runnable that launches the client multiplayer game.
	 */
	public ButtonPanel(JFrame parentFrame, Runnable singlePlayer, Runnable hostMultiPlayer, Runnable joinMultiPlayer)
	{
		singlePlayerTarget = singlePlayer;
		hostMultiPlayerTarget = hostMultiPlayer;
		joinMultiPlayerTarget = joinMultiPlayer;

		this.parentFrame = parentFrame;

		setLayout(new GridLayout(4, 1));

		// Create set of JButtons to be displayed on the Main Menu
		JButton singleBtn = new JButton("Single Player Game");
		JButton hostMPBtn = new JButton("Host Multiplayer Game");
		JButton joinMPBtn = new JButton("Join Multiplayer Game");
		JButton exitBtn = new JButton("Exit");

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

	/**
	 * Starts the game by creating a new Thread and running the Runnable passed into the
	 * Constructor. For most implementations, this will launch the primary game thread.
	 */
	private void singleBtnPerformed()
	{
		singlePlayerTarget.run();
		parentFrame.setVisible(false);
	}

	private void joinMPBtnPerformed()
	{
		joinMultiPlayerTarget.run();
		parentFrame.setVisible(false);
	}

	private void hostMPBtnPerformed()
	{
		hostMultiPlayerTarget.run();
		parentFrame.setVisible(false);
	}
}