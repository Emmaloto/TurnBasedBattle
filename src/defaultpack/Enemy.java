/** This is the class for the foe. It controls animation
 * of character when fighting, and also controls the enemey's healthbar.
 * 
 *
 * @author  Emmanuel Oluloto
 * @version 1.40, 24/02/18
 */

package defaultpack;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Enemy implements ActionListener {

	private Image enemyImg;
	private Image[] smallFish = new Image[1]; // smallfish array
	private Image bigFish, bigFishSmile, bigFishMouth, littleFishImg, lilFishSkins[], bubble;
	private HPResizeBar enemyHealth;

	// Transformations
	private AffineTransform smallFishPos[] = new AffineTransform[1];
	private AffineTransform bigFishPos;

	private int rot, r, x, y, defaultX, defaultY, bigX, bigY;
	private double scaleX, scaleY;

	private JComponent canvas;

	private boolean firstAttack, secondAttack, attackingTime, showHP, missHit, isSkinRandom;
	private int damageTaken, fontSize;

	private Sound chomp, bubbles;

	private Timer timer = new Timer(100, this);

	public Enemy(JComponent c) {
		loadImages();

		enemyHealth = new HPResizeBar(850, 100);
		canvas = c;

		bigX = -200;
		bigY = 2700; // final 2000
		defaultX = x;
		defaultY = y;

		isSkinRandom = true;
	}

	// ********* IMAGES DRAWN ON CANVAS ********* //
	// Draw Character
	public void drawEnemy(AffineTransform transform, Graphics2D g) {

		// Scale with window size
		scaleX = transform.getScaleX();
		scaleY = transform.getScaleY();

		enemyHealth.drawHealth(scaleX, scaleY, g);
		transform.rotate(rot);
		g.drawImage(enemyImg, transform, null);

		// Time to start attacking!
		if (attackingTime) {

			if (firstAttack) {
				for (int i = 0; i < smallFish.length; i++) {
					drawSmallFish(i);
					g.drawImage(smallFish[i], smallFishPos[i], null);
				}

				x = defaultX;
				y = defaultY;

			} else if (secondAttack) {
				drawBigFish();
				g.drawImage(bigFish, bigFishPos, null);
			}

		}

		// HP Bar
		if (showHP) {

			// Set & scale font size according to damage and window size
			if (damageTaken < 500)
				fontSize = (Math.abs(damageTaken) * 80 / 250) + 30;
			else
				fontSize = 200;
			fontSize = (int) (fontSize * ((scaleX + scaleY) / 2));
			g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));

			// Draw hit val
			int textPosX = (int) (1100 * scaleX);
			int textPosY = (int) (250 * scaleY);
			if (damageTaken < 0)
				g.drawString("+" + Integer.toString(-damageTaken), textPosX, textPosY);
			else if (missHit)
				g.drawString("Miss", textPosX, textPosY);
			else
				g.drawString(Integer.toString(damageTaken), textPosX, textPosY);

			// Critical hit text for opponent
			if (damageTaken > 200) {
				g.setFont(new Font("Serif", Font.PLAIN, 45));
				g.drawString("CRITICAL HIT!!", 400, 50);
			}

		}

	}

	private void drawSmallFish(int index) {
		smallFishPos[index] = new AffineTransform();

		// Rotation
		AffineTransform rotation = new AffineTransform();
		rotation.setTransform(new AffineTransform()); // identity
		rotation.rotate(Math.toRadians(r), x * scaleX, y * scaleY);

		// Translation
		AffineTransform trans = new AffineTransform();
		// Draws fish in a pattern
		x = x + 70;
		if (x > defaultX + 200) {
			x = defaultX + 35;
			y = y + 55;
		}

		trans.translate(x * scaleX, y * scaleY);

		// Scale
		AffineTransform scaleVal = new AffineTransform();
		scaleVal.scale(-.3, .3);

		// When window is resized
		scaleVal.scale(scaleX, scaleY);

		smallFishPos[index].concatenate(rotation);
		smallFishPos[index].concatenate(trans);
		smallFishPos[index].concatenate(scaleVal);
	}

	private void drawBigFish() {
		bigFishPos = new AffineTransform();

		// Rotation
		AffineTransform rotation = new AffineTransform();
		rotation.setTransform(new AffineTransform()); // identity
		rotation.rotate(Math.toRadians(-90), bigX * scaleX, bigY * scaleY);

		// Translation
		AffineTransform trans = new AffineTransform();
		trans.translate(bigX * scaleX, bigY * scaleY);

		// Scale
		AffineTransform scaleVal = new AffineTransform();
		scaleVal.scale(1, 1);

		// When window is resized
		scaleVal.scale(scaleX, scaleY);

		bigFishPos.concatenate(rotation);
		bigFishPos.concatenate(trans);
		bigFishPos.concatenate(scaleVal);
	}

	
	// Transforms the images until the timer runs out
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == timer) {

			// First attack
			if (firstAttack) {

				defaultX = defaultX - 100;
				// Fish must HIT hero, not pass them
				if (defaultX < 200) {

					defaultX = 200;
					r = r + GameUtilities.getRandomInteger(0, 80);
					for (int i = 0; i < smallFish.length; i++) {
						smallFish[i] = bubble;
					}
				}

			} else if (secondAttack) {

				bigY = bigY - (int) (300 * scaleY);

				if (bigY <= 1700 * scaleY) {
					bigY = (int) (1700 * scaleY);
					bigFish = bigFishSmile;
				}

			}

		}

		canvas.repaint();
	}

	// ********* CLASS FIELD VARS CONFIG ********* //
	
	// Set up number of small fish based on hit points
	private void setUpFish(int damageDealt) {
		int noOfFish = 1;

		if (damageDealt > 200) {
			noOfFish = 5;
		} else if (damageDealt > 150) {
			noOfFish = 4;
		} else if (damageDealt > 100) {
			noOfFish = 3;
		} else if (damageDealt > 50) {
			noOfFish = 2;
		}

		smallFish = new Image[noOfFish];
		for (int i = 0; i < smallFish.length; i++) {
			if (isSkinRandom)
				smallFish[i] = lilFishSkins[GameUtilities.getRandomInteger(0, lilFishSkins.length - 1)];
			else
				smallFish[i] = littleFishImg;

		}
		smallFishPos = new AffineTransform[noOfFish];

	}

	public void beginAttack(int damageToHero) {
		attackingTime = true;

		if (firstAttack) {
			rot = 50;
			setUpFish(damageToHero);
			bubbles.play();
		} else {
			chomp.play();
		}

		timer.start();
	}

	public void defaultPose() {
		if (timer.isRunning()) timer.stop();

		showHP = false;
		attackingTime = false;

		missHit = false;

		x = 1000;
		y = 500;
		rot = 0;
		defaultX = x;
		defaultY = y;

	}
	
	// ********* ATTACK TYPES ********* //
	
	public void attackA(int hitVal) {
		damageTaken = hitVal;
		firstAttack = true;
		secondAttack = false;

		x = 1000;
		y = 500;

		r = 0;

		// bubbles.play();
	}

	public void attackB(int hitVal) {
		damageTaken = hitVal;
		secondAttack = true;
		firstAttack = false;

		bigX = -200;
		bigY = 2700;

		bigFish = bigFishMouth;

		// chomp.play();
	}

	public void randomAttack(int hitVal) {
		if (GameUtilities.getRandomInteger(0, 10) % 2 == 0)
			attackA(hitVal);
		else
			attackB(hitVal);

	}

	public void criticalAttack() {

	}

	public void skip() {

	}

	// ********* GENERAL SETTERS AND GETTERS for Fish ********* //
	public double getHealth() {
		return enemyHealth.getHealth();
	}

	public void showHealth() {
		showHP = true;

		if (!missHit)
			enemyHealth.setHealth(damageTaken);

	}

	public void randomizeFish(boolean isRandom) {
		isSkinRandom = isRandom;

	}

	// NOT used, somewhat obsolete
	public void setHealth(int hit) {
		enemyHealth.setHealth(hit);
	}

	// Loads all resources
	private void loadImages() {
		try {

			enemyImg = ImageIO.read(this.getClass().getResource("res/enemy/fish.png"));

			littleFishImg = ImageIO.read(this.getClass().getResource("res/enemy/fishsmall.png")); // Default
			Image fishSkinA = ImageIO.read(this.getClass().getResource("res/enemy/fishsmallblack.png"));
			Image fishSkinB = ImageIO.read(this.getClass().getResource("res/enemy/fishsmallblue.png"));
			Image fishSkinC = ImageIO.read(this.getClass().getResource("res/enemy/fishsmallblue2.png"));

			bubble = ImageIO.read(this.getClass().getResource("res/enemy/bubble.png"));

			lilFishSkins = new Image[4];
			lilFishSkins[0] = littleFishImg;
			lilFishSkins[1] = fishSkinA;
			lilFishSkins[2] = fishSkinB;
			lilFishSkins[3] = fishSkinC;

			bigFishMouth = ImageIO.read(this.getClass().getResource("res/enemy/bigfishAttack.png"));
			bigFishSmile = ImageIO.read(this.getClass().getResource("res/enemy/bigfishsmile.png"));

			chomp = new Sound("audio/chomp.wav");
			bubbles = new Sound("audio/bubbles.wav");

		} catch (IOException e) {

			e.printStackTrace();
			System.out.printf("\nFILE NOT FOUND!\n");
		}

	}

}