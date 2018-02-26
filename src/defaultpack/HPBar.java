// Deprecated, NOT USED

/** This is the class for controlling the health bars of both the hero 
 * and villain. This class has been replaced by HPResizeBar, as I've had 
 * to overhaul a lot of the positions I initially used by adding a scale to them.
 * The game looks much better if the game elements can actually be resized
 * with the screen. That's also why you'll see a lot of the position quantities
 * throughout the game multiplied by a scale.
 *
 * @author  Emmanuel Oluloto
 * @version 1.40, 24/02/18
 */



package defaultpack;
import java.awt.*;

public class HPBar {

  private int newWidth, defaultHeight;
  private Rectangle health, border;
  private Color c, normalColor;

  // Health points bar
  public HPBar(){
	this(200, 100, 75, 100, Color.RED);
  }
  
  public HPBar(int x, int y){
	this(x, y, 75, 100, Color.RED);
  }
  
  public HPBar(int x, int y, int height, int healthVal, Color col){

	health = new Rectangle(x, y, height*8, height);
	border = new Rectangle(x, y, height*8, height);
	c = col;
	normalColor = c;
	defaultHeight = height;
  }
  
  public void drawHealth(double scaleX, double scaleY, Graphics2D g){
	  g.setColor(c);
	  g.draw(health);	  
	  g.fill(health);
	  
	  g.setColor(Color.BLACK);
	  g.setStroke(new BasicStroke(3));
	  g.draw(border);
	  
	  g.setColor(Color.WHITE);
	  g.setFont(new Font("SansSerif", Font.PLAIN, 55));
	  g.drawString(Integer.toString((int)health.getWidth()), (int)(health.getX() + defaultHeight*4), (int)(health.getY() + defaultHeight/1.5));
	  
	  g.setColor(Color.BLACK);
  }
  
  public void drawHealth(Graphics2D g){
     drawHealth(1,1,g);
  }  

  // Decreases hp, bar glowing bar for low hp
  public void setHealth(int h){
    newWidth = (int)health.getWidth() - h;

    
    if(newWidth >= 0 && newWidth <= border.getWidth()) // Is inbetween empty and ful
      health.setSize(newWidth, defaultHeight);
    else if(newWidth > border.getWidth())             // Is full
      health.setSize((int)border.getWidth(), defaultHeight);	
    else if(newWidth < 0)							  // Is empty
      health.setSize(0, defaultHeight);
   
    if(newWidth < border.getWidth()/3)                // Health is low
      c = Color.YELLOW;	
	else
	  c = normalColor;
  
  }

  public int getHealth(){
    return (int)health.getWidth();

  }
  //public HPBar(int x, int y, int height, int cWidth, int cHeight, Color col)
  /*
  // Sets x and y from original 1600, 1000
  private int setPosX(int val){
	  return (int)0/val;
  }
  
  private int setPosY(int val){
	  return (int)0/val;
  }
    */
}
