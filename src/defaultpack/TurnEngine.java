/** This is the class that does basically everything! 
 * It brings all the components together, and actually creates the graphics
 * element to draw on.
 * 
 *
 * @author  Emmanuel Oluloto
 * @version 1.40, 24/02/18
 */

package defaultpack;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.Font;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.*;
import java.util.Timer;
import java.util.TimerTask;


public class TurnEngine extends JComponent implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Our main stars
	private Hero hero;
	private Enemy fish;

	// For screen width and height changes
	private final int DEFAULT_WIDTH, DEFAULT_HEIGHT;

	private Timer attackTime = new Timer("Attack Begins");
	private TimerTask changeImage, enemyLoseHP, heroLoseHP, heroHeal, revertHero, revert, changeHealth;

	// Button icons
	private Image danceIcon, fightIcon, healIcon, laughIcon, sAttackIcon, photoIcon, sfishIcon, clothIcon, onefishIcon, manyfishIcon,
	              infoScreen, winGame, loseGame, gameResult;

	private JFrame fr;
	private JButton attack, regenerate, taunt, sAttack, dance, resetButton;

	private JMenuBar menu;
	private JMenu fileMenu, bgsMenu, fgsMenu, enemyMenu;

	// Amount of damage done (or undone) to characters
	private int damageToHero, damageToEnemy, healVal;

	// Controls number of turns for characters to cooldown
	private int healCount, specialCount;

	private int canvasW, canvasH; // To position things relative to screen

	private boolean healPressed = false, specialPressed = false, gameEnded = false, showInst = false;

	private String turnText = "Your Turn";
	private String winText = "";
	private String infoText[] = new String[1];

	private Font title, body;
	private float orgTitleSize, orgBodySize;

	private Sound victClip, failClip, healClip, laughClip, danceClip[], specClip, hitClip[];
	private SceneController sceneControl;


	

	public TurnEngine() {

		// Set up variables
		loadImages();
		setTasks();

		DEFAULT_WIDTH = 1600;
		DEFAULT_HEIGHT = 1000;

		hero = new Hero(this);
		fish = new Enemy(this);

		fr = new JFrame();

		fr.setTitle("TIME TO FIGHT!");
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close window on exit
		fr.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setPreferredSize(new Dimension(1600, 1000)); /****/
		fr.getContentPane().add(this);

		canvasW = fr.getWidth();
		canvasH = fr.getHeight();

		// Add buttons
		JPanel buttonbar = new JPanel();
		buttonbar.setBackground(Color.DARK_GRAY);

		regenerate = new JButton("Heal");
		taunt = new JButton("Trash Talk");
		attack = new JButton("Attack!");
		sAttack = new JButton("Special Attack");
		dance = new JButton("DANCE?!?");

		resetButton = new JButton("REPLAY GAME");

		regenerate.setIcon(new ImageIcon(healIcon));
		taunt.setIcon(new ImageIcon(laughIcon));
		attack.setIcon(new ImageIcon(fightIcon));
		sAttack.setIcon(new ImageIcon(sAttackIcon));
		dance.setIcon(new ImageIcon(danceIcon));

		// Set up menu bar and submenus
		menu = new JMenuBar();
		menu.setBackground(new Color(204, 204, 204));
		
	    fileMenu = new JMenu("File");	    
	    bgsMenu  = new JMenu("Backgrounds");
	    fgsMenu  = new JMenu("Foregrounds");
	    enemyMenu  = new JMenu("Foe Customization");

	    JMenuItem openSite = new JMenuItem("Visit My Website");	    
	    openSite.addActionListener(this);
	    fileMenu.add(openSite);
	    
	    JMenuItem close = new JMenuItem("Close Game");	    
	    close.addActionListener(this);
	    fileMenu.add(close);
	    
	    // -- Setting up skin controls
	    JMenu skins = new JMenu("Skins");
	    String [] listOfFishskins = fish.getSkinNames();
	    for(String name: listOfFishskins) {
		    JMenuItem skinEntry = new JMenuItem(name, new ImageIcon(clothIcon));
		    skinEntry.addActionListener(this);
		    skins.add(skinEntry);
	    }
	    
	    JMenu minifish = new JMenu("Toggle MiniFish");
	    JMenuItem varType = new JMenuItem("Various Types", new ImageIcon(manyfishIcon));
	    JMenuItem oneType = new JMenuItem("One Type", new ImageIcon(onefishIcon));
	    varType.addActionListener(this);
	    oneType.addActionListener(this);
	    minifish.add(varType);
	    minifish.add(oneType);
	    
	    enemyMenu.add(skins);
	    enemyMenu.add(minifish);
	    
	    // -- Setting up scene UI controls
	    sceneControl = new SceneController();

	    JMenuItem removeValue = new JMenuItem("NONE");
	    JMenuItem removeValue2 = new JMenuItem("NONE");
	    removeValue.addActionListener(this);
	    removeValue2.addActionListener(this);
	    bgsMenu.add(removeValue);
	    fgsMenu.add(removeValue2);
	    
	    String [] listOfBGs = sceneControl.getAvailableNames(true);
	    for(String name: listOfBGs) {
		    JMenuItem bg = new JMenuItem(name, new ImageIcon(photoIcon));
		    bg.addActionListener(this);
		    bgsMenu.add(bg);
	    }
	    
	
	    String [] listOfFGs = sceneControl.getAvailableNames(false);
	    for(String name: listOfFGs) {
		    JMenuItem fg = new JMenuItem(name, new ImageIcon(sfishIcon));
		    fg.addActionListener(this);
		    fgsMenu.add(fg);
	    }
	    
	    menu.add(fileMenu);
	    menu.add(bgsMenu);
	    menu.add(fgsMenu);
	    menu.add(enemyMenu);


		// Change font of button hover text, and set text
		UIManager.put("ToolTip.font", new FontUIResource("SansSerif", Font.BOLD, 25));

		regenerate.setToolTipText("Heal for 100 - 150 hp. Can use every 4 turns.");
		taunt.setToolTipText("Mock the poor fishie. 50 - 200 hp.");
		attack.setToolTipText("Generic Attack for 50 - 200 hp.");
		sAttack.setToolTipText("Powerful Attack for 100 - 400 hp. Can use every 3 turns.");
		dance.setToolTipText("This (probably) won't have any effect, but you'll show off your cool dance moves!!!");

		regenerate.addActionListener(this);
		taunt.addActionListener(this);
		attack.addActionListener(this);
		sAttack.addActionListener(this);
		dance.addActionListener(this);
		resetButton.addActionListener(this);

		buttonbar.add(regenerate);
		buttonbar.add(taunt);
		buttonbar.add(attack);
		buttonbar.add(sAttack);
		buttonbar.add(dance);

		fr.add(buttonbar, BorderLayout.SOUTH);
		fr.add(resetButton, BorderLayout.NORTH);
		fr.setJMenuBar(menu);

		fr.setVisible(true);

		// Configure for different screen sizes
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1920 || screenSize.getHeight() < 1080) {
			fr.setSize(1295, 800);
		}

	}

	
	// ********* DRAWING + VAR CONFIG FOR DRAWING/RESIZING ********* //

	// For all the graphics on screen
	public void paintComponent(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		canvasW = fr.getWidth();
		canvasH = fr.getHeight();
		AffineTransform scaleToCanvas = new AffineTransform();
		scaleToCanvas.scale(rescaleW(), rescaleH());
		
		// BG here
		sceneControl.drawBackground(new AffineTransform(scaleToCanvas), g2d);

		// Short top-left info text
		g2d.setFont(new Font("TimesRoman", Font.BOLD, 30));
		g2d.drawString("Press h for more game info.", 0, setPosY(20));

		// Hero
		AffineTransform trans = new AffineTransform();
		trans.translate(0, setPosY(10));
		trans.concatenate(scaleToCanvas);
		hero.drawCharacter(trans, g2d);

		// Enemy
		AffineTransform transE = new AffineTransform();
		transE.translate(setPosX(2), setPosY(8));
		transE.concatenate(scaleToCanvas);
		fish.drawEnemy(transE, g2d);

		
		// Foreground here
		sceneControl.drawForeground(new AffineTransform(scaleToCanvas), g2d);

		// Whose turn
		g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
		g2d.drawString(turnText, setPosX(2), (int) (canvasH / 1.25));

		// Game result
		if (gameEnded) {
			g2d.setFont(new Font("TimesRoman", Font.PLAIN, 200));
			g2d.drawString(winText, (int) (canvasW / 5.3), (canvasH / 2));

			double wx = fr.getWidth() / 2 - 1.5 * gameResult.getWidth(null) / 2;
			double wy = fr.getHeight() / 2 - 1.5 * gameResult.getHeight(null) / 2;
			g2d.drawImage(gameResult, GameUtilities.getTransform(wx, wy, 0, 1.5, 1.5), null);
		}

		
		// Instruction screen
		if (showInst) {
			resizeFonts();
			g2d.drawImage(infoScreen, 0, 0, (int) fr.getWidth() - setPosX(100), (int) fr.getHeight() - setPosY(100),
					null);
			g.setColor(Color.WHITE);

			// Top text
			if (title != null)
				g2d.setFont(title);
			g2d.drawString(infoText[0], setPosX(30), setPosY(10));

			// Body
			if (body != null)
				g2d.setFont(body);
			for (int i = 1, y = setPosY(6), x = setPosX(30); i < infoText.length; i++) {
				if (infoText[i] != null)
					g2d.drawString(infoText[i], x, y);
				y += 40;

				// Text wrap
				if (y > canvasH - 150 && x < 100) {
					x += (690 * rescaleW());
					y = setPosY(6);

				}
			}
		}

	}


	// This is for resizing the text when the screen is resized
	private void resizeFonts() {
		try {
			Font swap = title;
			title = swap.deriveFont((float) ((rescaleW() + rescaleH()) / 2 * orgTitleSize));
			swap = body; // Stopping annoying errors
			body = swap.deriveFont((float) ((rescaleW() + rescaleH()) / 2 * orgBodySize));
		} catch (NullPointerException e) {
			title = new Font("SansSerif", Font.BOLD, 30);
			body = new Font("SansSerif", Font.BOLD, 20);
		}

	}

	// Sets x and y from original 1600, 1000
	private int setPosX(int val) {
		return (int) canvasW / val;
	}

	private int setPosY(int val) {
		return (int) canvasH / val;
	}

	// Sets width and height from original 1600, 1000
	private double rescaleW() {
		return (double) canvasW / (double) DEFAULT_WIDTH;
	}

	private double rescaleH() {
		return (double) canvasH / (double) DEFAULT_HEIGHT;
	}

	
	
	// ********* WHERE THE MAGIC HAPPENS ********* //
	// Controls the actions of buttons
	public void actionPerformed(ActionEvent a) {

		if (a.getSource() == regenerate) { // Player heals
			healVal = -GameUtilities.getRandomInteger(100, 150);
			hero.heal(healVal);

			damageToEnemy = GameUtilities.getRandomInteger(50, 180); // Will be set in changeHealth
			fish.randomAttack(damageToEnemy);
			healPressed = true;

			healClip.play();

			attackTime.schedule(changeImage, 0);

			attackTime.schedule(heroHeal, 1000);
			attackTime.schedule(revertHero, 4000);
			attackTime.schedule(changeHealth, 4100); // For second attack

			attackTime.schedule(heroLoseHP, 6000);
			attackTime.schedule(revert, 8000);

		} else if (a.getSource() == taunt) { // Player mocks

			damageToEnemy = GameUtilities.getRandomInteger(50, 200);

			fish.randomAttack(damageToEnemy);

			damageToHero = GameUtilities.getRandomInteger(50, 250);
			hero.taunt(damageToHero);

			laughClip.play();

			attackTime.schedule(changeImage, 0);
			attackTime.schedule(enemyLoseHP, 3500);
			attackTime.schedule(revertHero, 5000);

			attackTime.schedule(heroLoseHP, 6500);
			attackTime.schedule(revert, 8500);
		} else if (a.getSource() == attack) { // Player does normal attack
			damageToEnemy = GameUtilities.getRandomInteger(50, 200);
			// System.out.println("Damage to enemy:" + damageToHero);
			fish.randomAttack(damageToEnemy);

			damageToHero = GameUtilities.getRandomInteger(50, 250);
			hero.attack(damageToHero);

			hitClip[GameUtilities.getRandomInteger(0, 1)].play();

			attackTime.schedule(changeImage, 0);
			attackTime.schedule(enemyLoseHP, 1500);
			attackTime.schedule(revertHero, 4000);

			attackTime.schedule(heroLoseHP, 6000);
			attackTime.schedule(revert, 8000);
		} else if (a.getSource() == sAttack) { // Player does special attack
			damageToEnemy = GameUtilities.getRandomInteger(100, 400);
			fish.randomAttack(damageToEnemy);

			damageToHero = GameUtilities.getRandomInteger(50, 250);
			hero.specialAttack(damageToHero, 2400);
			specialPressed = true;

			specClip.play();

			attackTime.schedule(changeImage, 0);
			attackTime.schedule(enemyLoseHP, 2400);
			attackTime.schedule(revertHero, 4500);

			attackTime.schedule(heroLoseHP, 6000);
			attackTime.schedule(revert, 8000);
		} else if (a.getSource() == dance) { // DANCE!?!
			if (GameUtilities.getRandomInteger(1, 10) != 1) // 5% chance of fatal damage
				damageToEnemy = 0;
			else
				damageToEnemy = 5000;
			fish.randomAttack(damageToEnemy);

			damageToHero = GameUtilities.getRandomInteger(50, 250);
			hero.dance(damageToHero);

			danceClip[GameUtilities.getRandomInteger(0, 1)].play();

			attackTime.schedule(changeImage, 0);
			attackTime.schedule(enemyLoseHP, 3500);
			attackTime.schedule(revertHero, 4000);

			attackTime.schedule(heroLoseHP, 7000);
			attackTime.schedule(revert, 9000);
		} else if (a.getSource() == resetButton) { // reset game
			reset();
		}

		// Change from Menu
		if(a.getSource().getClass() == JMenuItem.class){
			
			JMenuItem chosen = (JMenuItem) a.getSource(); 
			JPopupMenu chosenPopup = (JPopupMenu) chosen.getParent();
			JMenu chosenMenu = (JMenu) chosenPopup.getInvoker();
			
			if(chosenMenu.getText() == "File") {
				if(a.getActionCommand() == "Visit My Website") GameUtilities.openWebsite("https://icyblaze16.wixsite.com/emmaloto");				
				else if(a.getActionCommand() == "Close Game")  close();
			}else if(chosenMenu.getText() == "Backgrounds") {
				sceneControl.setBackground(a.getActionCommand());
			}else if( chosenMenu.getText() == "Foregrounds") {
				sceneControl.setForeground(a.getActionCommand());
			}else if( chosenMenu.getText() == "Skins") {
				fish.setSelectedSkin(a.getActionCommand());
			}else if( chosenMenu.getText() == "Toggle MiniFish") {
				if(a.getActionCommand() == "Various Types") fish.randomizeFish(true);				
				else fish.randomizeFish(false);		
			}else {
				System.out.println("Top Menu: " + chosenMenu.getText() + " ");	
				System.out.println("   Option selected: " + a.getActionCommand());
			}

			
			repaint();
		}
		

	}
	
	

	// ********* TASKS RUN VIA TIMER (and helper methods) ********* //
	// Sets the timertasks, so I can control the timeline of actions
	private void setTasks() {

		changeImage = new TimerTask() {
			public void run() {

				toggleButtons(false);
				repaint();
			}
		};

		// Enemy gets hit
		enemyLoseHP = new TimerTask() {
			public void run() {
				hero.defaultPose();
				fish.showHealth();

				repaint();

			}
		};

		// Hero gets hit
		heroLoseHP = new TimerTask() {
			public void run() {

				fish.defaultPose();

				hero.showHealth();
				repaint();
			}
		};

		// Exact same as heroLoseHP
		heroHeal = new TimerTask() {
			public void run() {

				regenerate.setEnabled(false); // ************//
				hero.defaultPose();
				hero.showHealth();

				repaint();

			}
		};

		// Used for multiple attacks on same hero e.g heal
		changeHealth = new TimerTask() {
			public void run() {

				hero.setHealthValue(damageToHero);
			}
		};

		// Hero is done attacking
		revertHero = new TimerTask() {
			public void run() {
				fish.defaultPose();
				hero.defaultPose();
				turnText = "Fish's Turn";

				repaint();
				endGame();

				if (!gameEnded)
					fish.beginAttack(damageToHero);
			}
		};

		revert = new TimerTask() {
			public void run() {

				turnText = "Your Turn";
				hero.defaultPose();
				fish.defaultPose();
				toggleButtons(true);

				checkCooldown();

				regenerate.setEnabled(!healPressed);
				sAttack.setEnabled(!specialPressed);
				repaint();

				endGame();

				attackTime.cancel();
				attackTime.purge();
				// TODO Issues?
				setTasks();
				attackTime = new Timer();

			}
		};
	}


	
	// Buttons all enabled/disabled
	private void toggleButtons(boolean buttonEnabled) {

		regenerate.setEnabled(buttonEnabled);
		taunt.setEnabled(buttonEnabled);
		attack.setEnabled(buttonEnabled);
		sAttack.setEnabled(buttonEnabled);
		dance.setEnabled(buttonEnabled);

		resetButton.setEnabled(buttonEnabled);
	}

	// Disables buttons until no of turns have passed
	private void checkCooldown() {
		// Disable the heal button for 5 turns
		if (healPressed) {
			healCount++;
			if (healCount % 4 == 0)
				healPressed = false;
		}

		if (specialPressed) {
			specialCount++;
			if (specialCount % 3 == 0)
				specialPressed = false;
		}
	}


	// Check if game has ended
	private void endGame() {
		gameEnded = hero.getHealth() <= 0 || fish.getHealth() <= 0;
		if (gameEnded) {
			toggleButtons(false);
			attackTime.cancel();
			if (hero.getHealth() <= 0) {
				gameResult = loseGame;
				failClip.play();
			} else {
				gameResult = winGame;
				victClip.play();
			}

			repaint();
			resetButton.setEnabled(true);
		}

	}

	

	// For easy restart of the game
	public void setup() {

		hero = new Hero(this);
		fish = new Enemy(this);

		toggleButtons(true);
		healPressed = false;
		specialPressed = false;
		gameEnded = false;
		attackTime = new Timer("Attack Begins");

		turnText = "Your Turn";
		winText = "";

		title = new Font(null);
		body = new Font(null);
	}

	public JFrame getFrame() {
		return fr;
	}

	public boolean gameHasEnded() {
		return gameEnded;
	}

	public void setHelpScreen(Image infoBG) {
		infoScreen = infoBG;
	}

	public void setInfoText(String... info) {
		infoText = info;
	}

	public void showHelp() {
		showInst = !showInst;
		toggleButtons(!showInst);
	}

	public void setInfoFont(Font f, Font b) {
		title = f;
		body = b;

		orgTitleSize = title.getSize();
		orgBodySize = body.getSize();
	}
	
	
	public void close() {
		fr.dispose();
		System.exit(0);
	}

	// Reset for new play
	private void reset() {
		setup();
		setTasks();
		repaint();
	}
	
	
	
	// Loads all resources
	private void loadImages() {
		try {

			fightIcon = ImageIO.read(this.getClass().getResource("icons/starbutton.png"));
			healIcon = ImageIO.read(this.getClass().getResource("icons/healicon.png"));
			laughIcon = ImageIO.read(this.getClass().getResource("icons/talk.png"));
			sAttackIcon = ImageIO.read(this.getClass().getResource("icons/specialicon.png"));
			danceIcon = ImageIO.read(this.getClass().getResource("icons/danceicon.png"));
			
			photoIcon = ImageIO.read(this.getClass().getResource("icons/photo-photography.png"));
			sfishIcon = ImageIO.read(this.getClass().getResource("icons/starfish_icon.png"));
			clothIcon = ImageIO.read(this.getClass().getResource("icons/coaticon.png"));
			onefishIcon = ImageIO.read(this.getClass().getResource("icons/onefishicon.png"));
			manyfishIcon = ImageIO.read(this.getClass().getResource("icons/manyfishicon.png"));

			winGame = ImageIO.read(this.getClass().getResource("res/win_text.png"));
			loseGame = ImageIO.read(this.getClass().getResource("res/lose_text.png"));

			gameResult = winGame;

			danceClip = new Sound[2];
			hitClip = new Sound[2];
			

		} catch (IOException | NullPointerException e) {

			e.printStackTrace();
			System.out.printf("\nFILE NOT FOUND!\n");
		}

		victClip = new Sound("audio/badass-victory.wav");
		failClip = new Sound("audio/menacing.wav");

		healClip = new Sound("audio/heal.wav");
		laughClip = new Sound("audio/laughing.wav");
		danceClip[0] = new Sound("audio/dance-1.wav");
		danceClip[1] = new Sound("audio/dance-2.wav");
		specClip = new Sound("audio/fanfare.wav");
		hitClip[0] = new Sound("audio/attack-1.wav");
		hitClip[1] = new Sound("audio/attack-2.wav");
	}


}