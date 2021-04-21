/** This is the class for the hero. It controls animation
 * of character when fighting, and also controls the health bar.
 * 
 *
 * @author  Emmanuel Oluloto
 * @version 1.40, 24/02/18
 */

package defaultpack;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Hero implements ActionListener {

	private Image[] danceImg, fightImg, healImg, calmImg, laughImg, sAttackImg, fightpose;

	private Image specialWeapon, missingImg;
	private AffineTransform specialTrans;
	private int rot;
	private double x, y;

	private Image current[];
	private HPResizeBar herohealth;
	private JComponent canvas;

	private int maxIndex = 0, currentImg = 0, randIndex = 0;

	private boolean useWeapon, showHP, mockTime;
	private int damageTaken, fontSize;
	private double scaleX, scaleY;

	private Timer timer = new Timer(100, this);
	private long period, startTime;

	private String insults[] = { "You smell like dead fish!", "I'm going to turn you into sushi!!",
			"I'm about to drop this bass!", "How can you even swim with a tail like that?!!?",
			"Not bad, cod do better...", "A giant cod again? Really?", "I'm about to give you a very biting review!",
			"Not bad for a bottom feeder.", "You are rEELY bad at this!", "Prepare to get schooled!",
			"You need to scale up your efforts!", "I'm going to of-fish-ally own you!" };
// https://www.tankarium.com/fish-puns/ "Look at me when I'm mocking ya!"

	
	public Hero(JComponent c) {
		loadImages();

		herohealth = new HPResizeBar();

		canvas = c;

		rot = 80;
		x = 1300;
		y = -1500; // Final val -200
		specialTrans = new AffineTransform();
	}

	// Draw Hero
	public void drawCharacter(AffineTransform transform, Graphics2D g) {

		scaleX = transform.getScaleX();
		scaleY = transform.getScaleY();

		// Animated weapon (out of sight)
		drawWeapon();
		g.drawImage(specialWeapon, specialTrans, null);

		// Health bar and hero
		herohealth.drawHealth(scaleX, scaleY, g);
		g.drawImage(current[currentImg], transform, null);

		// Hit point the hero received
		if (showHP) {

			if (damageTaken < 500)
				fontSize = (Math.abs(damageTaken) * 70 / 250) + 30;
			else
				fontSize = 200;

			fontSize = (int) (fontSize * ((scaleX + scaleY) / 2));
			g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));

			// Draw hit val
			int textPosX = (int) (450 * scaleX);
			int textPosY = (int) (250 * scaleY);
			if (damageTaken < 0)
				g.drawString("+" + Integer.toString(-damageTaken), textPosX, textPosY);
			else
				g.drawString(Integer.toString(damageTaken), textPosX, textPosY);

			// Critical hit text for opponent
			if (damageTaken > 200) {
				g.setFont(new Font("Serif", Font.PLAIN, 45));
				g.drawString("CRITICAL HIT!!", (int) (900 * scaleX), (int) (50 * scaleY));
			}

		}

		// Mock text
		if (mockTime) {
			g.setFont(new Font("TimesRoman", Font.PLAIN, 40));
			g.drawString(insults[randIndex], (int) (370 * scaleX), (int) (300 * scaleY));
		}

	}

	public void drawWeapon() {

		specialTrans = new AffineTransform();

		// Rotation
		AffineTransform rotation = new AffineTransform();
		rotation.setTransform(new AffineTransform()); // identity
		rotation.rotate(Math.toRadians(rot), x * scaleX, y * scaleY);

		// Translation
		AffineTransform trans = new AffineTransform();
		trans.translate(x * scaleX, y * scaleY);

		// Scale
		AffineTransform scaleVal = new AffineTransform();
		scaleVal.scale(1.3, 1.3);
		scaleVal.scale(scaleX, scaleY);

		specialTrans.concatenate(rotation);
		specialTrans.concatenate(trans);
		specialTrans.concatenate(scaleVal);
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == timer) {
			currentImg += 1;
			if (currentImg == maxIndex)
				currentImg = 0;

			// Use special weapon during this attack
			if (useWeapon) {

				// Starts attack halfway through Hero animation
				long timeElasped = System.currentTimeMillis() - startTime;
				if (timeElasped >= period / 2) {

					// Stops animating When weapon 'hits' enemy
					if (!(y >= -100 * scaleY))
						y = y + (int) ((1300 * 50 * scaleY) / (period / 6));

				}
			}

		}
		canvas.repaint();

	}

	public void heal(int hitVal) {

		current = healImg;
		maxIndex = healImg.length;

		damageTaken = hitVal;

		timer.start();

	}

	public void attack(int hitVal) {

		current = fightImg;
		maxIndex = fightImg.length;
		damageTaken = hitVal;

		timer.setDelay(100);
		timer.start();
	}

	public void specialAttack(int hitVal, int lengthOfAttack) {
		current = sAttackImg;
		maxIndex = sAttackImg.length;
		damageTaken = hitVal;

		period = lengthOfAttack;
		startTime = System.currentTimeMillis();
		useWeapon = true;

		timer.setDelay(50);
		timer.start();
	}

	public void taunt(int hitVal) {
		current = laughImg;
		maxIndex = laughImg.length;
		damageTaken = hitVal;

		mockTime = true;
		randIndex = GameUtilities.getRandomInteger(0, insults.length - 1);
		// System.out.println(randIndex);

		timer.setDelay(125);
		timer.start();
	}

	public void dance(int hitVal) {
		current = danceImg;
		maxIndex = danceImg.length;
		damageTaken = hitVal;

		timer.setDelay(125);
		timer.start();
	}

	public void defaultPose() {

		if (timer.isRunning())
			timer.stop();

		currentImg = 0;
		maxIndex = fightpose.length;
		current = fightpose;

		showHP = false;
		useWeapon = false;
		mockTime = false;
		y = -1500;
		period = 0;
		startTime = 0;

	}

	public double getHealth() {
		return herohealth.getHealth();
	}

	public void pause(int length) {

	}

	// Load all images for animation
	private void loadImages() {
		try {
			calmImg = new Image[1];
			fightpose = new Image[1];
			healImg = new Image[1];

			laughImg = new Image[2];
			sAttackImg = new Image[4];
			danceImg = new Image[4];
			fightImg = new Image[4];

			calmImg[0] = ImageIO.read(this.getClass().getResource("res/Charcalm.png"));
			fightpose[0] = ImageIO.read(this.getClass().getResource("res/charfight.png"));
			healImg[0] = ImageIO.read(this.getClass().getResource("res/heal.png"));
			
			missingImg = ImageIO.read(this.getClass().getResource("res/question-mark.png"));

			laughImg = loadAllFromDirectory("res/laugh").toArray(laughImg);

			sAttackImg = loadAllFromDirectory("res/special").toArray(sAttackImg);
			
			danceImg = loadAllFromDirectory("res/dance").toArray(danceImg);
		
			fightImg = loadAllFromDirectory("res/fight").toArray(fightImg);

			specialWeapon = ImageIO.read(this.getClass().getResource("res/weapon_special.png"));

		} catch (IOException e) {

			e.printStackTrace();
			System.out.printf("\nFILE NOT FOUND!\n");
		}
		current = fightpose;
		maxIndex = fightpose.length;
	}

	private ArrayList<Image> loadAllFromDirectory(String dirPath) {
		// Creates file using getResource URL object
		final File dir = new File(this.getClass().getResource(dirPath).getPath());

		ArrayList<Image> imageList = new ArrayList<Image>();

		for (File fileEntry : dir.listFiles()) {
			
			try {
				Image frame = ImageIO.read(fileEntry);
				imageList.add(frame);

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error loading from directory: " + dir.getPath());
			}
		}

		if(imageList.isEmpty()) imageList.add(missingImg);
		return imageList;
	}

	// NOT used, somewhat obsolete
	public void setHealth(int hit) {
		showHP = true;
		herohealth.setHealth(hit);
		damageTaken = hit;

	}

	public void setHealthValue(int hit) {
		damageTaken = hit;

	}

	public void showHealth() {
		showHP = true;
		herohealth.setHealth(damageTaken);

	}



}