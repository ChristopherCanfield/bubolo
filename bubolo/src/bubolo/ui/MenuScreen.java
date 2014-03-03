package bubolo.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * A Menu Screen that should follow the splash screen. Allows users to start the game or
 * quit, as the Preferences button is currently non-functional.
 * 
 * @author BU CS673 - Clone Productions
 */
public class MenuScreen extends javax.swing.JFrame
{
	private static final long serialVersionUID = -5355152035949516532L;
	
	/**
	 * The Runnable that should be activated when the Start menu button is pressed.
	 */
	Runnable myTarget; 

	/**
	 * Creates new form MenuScreen
	 */
	public MenuScreen(Runnable targetApp) throws InterruptedException
	{
		myTarget = targetApp;
		initComponents();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2
				- this.getSize().height / 2);
		setSize(500, 500);
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING:
	 * Do NOT modify this code. The content of this method is always regenerated by the
	 * Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{

		UserPrcoessContainer = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		startButton = new javax.swing.JToggleButton();
		jLabel2 = new javax.swing.JLabel();
		preferencesButton = new javax.swing.JToggleButton();
		quitButton = new javax.swing.JToggleButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new java.awt.CardLayout());

		jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
		jLabel1.setText("B.U.B.O.L.O");

		startButton.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
		startButton.setText("New Game ");
		startButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				jToggleButton1ActionPerformed(evt);
			}
		});

		jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
		jLabel2.setText("Clone Productions");

		preferencesButton.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
		preferencesButton.setText("Preferences");

		quitButton.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
		quitButton.setText("Exit");
		quitButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				jToggleButton3ActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout UserPrcoessContainerLayout = new javax.swing.GroupLayout(
				UserPrcoessContainer);
		UserPrcoessContainer.setLayout(UserPrcoessContainerLayout);
		UserPrcoessContainerLayout.setHorizontalGroup(UserPrcoessContainerLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
						UserPrcoessContainerLayout
								.createSequentialGroup()
								.addGap(178, 178, 178)
								.addGroup(
										UserPrcoessContainerLayout
												.createParallelGroup(
														javax.swing.GroupLayout.Alignment.CENTER)
												.addComponent(quitButton).addComponent(jLabel1)
												.addComponent(startButton).addComponent(jLabel2)
												.addComponent(preferencesButton))
								.addGap(130, 130, 130)));
		UserPrcoessContainerLayout.setVerticalGroup(UserPrcoessContainerLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				UserPrcoessContainerLayout
						.createSequentialGroup()
						.addGap(33, 33, 33)
						.addComponent(jLabel1)
						.addGap(35, 35, 35)
						.addComponent(jLabel2)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 223,
								Short.MAX_VALUE).addComponent(startButton).addGap(18, 18, 18)
						.addComponent(preferencesButton).addGap(18, 18, 18)
						.addComponent(quitButton).addGap(48, 48, 48)));

		getContentPane().add(UserPrcoessContainer, "card2");

		pack();
	}// </editor-fold>

	/**
	 * Starts the game by creating a new Thread and running the Runnable passed into the
	 * Constructor. For most implementations, this will launch the primary game thread.
	 * 
	 * @param evt
	 *            is the event created by this button being pressed. Currently unused.
	 */
	private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt)
	{
		(new Thread(myTarget)).start();
	}

	/**
	 * Quits the game by ending this thread. Could probably be cleaned up to make sure that all
	 * resources are disposed of correctly.
	 * 
	 * @param evt
	 *            is the event created by this button being pressed. Currently unused.
	 */
	private void jToggleButton3ActionPerformed(java.awt.event.ActionEvent evt)
	{
		// TODO add your handling code here:
		this.dispose();
	}

	// Variables declaration - do not modify
	private javax.swing.JPanel UserPrcoessContainer;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JToggleButton startButton;
	private javax.swing.JToggleButton preferencesButton;
	private javax.swing.JToggleButton quitButton;
	// End of variables declaration
}
