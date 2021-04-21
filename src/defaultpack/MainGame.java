/** This contains the main method for my turn-based battle game. It loads the 
 * text used for the help screen, as well as loading the game engine that
 * controls the UI and variables of the game. Some of the commented-out portions are 
 * changes I plan to make later. 
 * 
 *
 * @author  Emmanuel Oluloto
 * @version 1.40, 24/02/18
 */

package defaultpack;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

public class MainGame implements KeyListener{

  private Image infoScreen;		
  private TurnEngine newGame;
  private JFrame screen;
  
  //private Screen helpInfo;
  
  // Help screen text
  private String [] info;
  
  public static void main(String args[]){
	 new MainGame();

  }
  
  public MainGame(){
	  // Used to set up some UI elements not directly relevant to gameplay 
	  try{
	    infoScreen =  ImageIO.read(this.getClass().getResource("res/infoscreen.png"));
	  }catch (IOException e) {}
	  
	  newGame = new TurnEngine();
	  screen = newGame.getFrame();         // Convenient way to use JFrame	  	  
	  screen.setFocusable(true);
      screen.requestFocusInWindow();	  
	  screen.addKeyListener(this);
	  
      // Load string array to TurnEngine, and set up help screen
	  info = GameUtilities.getInputFromFile("defaultpack/battleInfo.txt", this);	  
	  newGame.setInfoText(info);  
	  newGame.setHelpScreen(infoScreen);	  
	  
	  Font title, body;
	  
	  try{
	    // Load and use fonts
	     title = GameUtilities.getFont("defaultpack/fonts/Philosopher-Italic.ttf", 80f, this);
	     body = GameUtilities.getFont("defaultpack/fonts/Philosopher-Italic.ttf", 20f, this);
	  }catch(NullPointerException e){
		  title = new Font("SansSerif", Font.BOLD, 20);
		  body = new Font("SansSerif", Font.BOLD, 20);
	  }
	  
	  newGame.setInfoFont(title, body);
	  /*
	  helpInfo.setTitleFont(title);
	  helpInfo.setTextFont(body);
	  */

  }


  // Keyboard controls
  public void keyPressed(KeyEvent e) {
	  
    if(e.getKeyCode() == KeyEvent.VK_H){
		newGame.showHelp();
		newGame.repaint();
		
    }else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
		newGame.close();
	}
    
  }


  public void keyReleased(KeyEvent e) {

	
  }


  public void keyTyped(KeyEvent e) {

	
  }
  
  
}
  