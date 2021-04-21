// NOT USED

/** This is the class I MIGHT implement for displaying the text, as it makes 
 * displaying screens much easier, and I can also put less code in the
 * already-large TurnEngine class.
 *
 * @author  Emmanuel Oluloto
 * @version 1.40, 24/02/18
 */

package defaultpack;
import java.awt.*;
import java.awt.geom.AffineTransform;
/*https://stackoverflow.com/questions/258486/calculate-the-display-width-of-a-string-in-java*/
public class Screen {
	
	private Image s;
	
	private String message;
	private String [] text;
	
	private int textX , textY, titleX, titleY, space;
	private Color textcolor;
	private Font font, tFont;
	//private int textsize;
	private boolean showText;
			
	public Screen(Image bg){
		s = bg;
		message = "Your text goes here...";
		
		textX = 30;
		textY = 30;
		textcolor = Color.WHITE;
	
	}
	
	public void setTitle(String st){
		message = st;
	}
	
	public void changeTitlePosition(int nx, int ny){
		titleX = nx;
		titleY = ny;
	}
	
	// https://stackoverflow.com/questions/16100175/store-text-file-content-line-by-line-into-array
	public void setPhrase(String [] info, int spacing){
		text = info;
		space = spacing;
		
		showText = true;
	}

	public void changeTextPosition(int nx, int ny){
		textX = nx;
		textY = ny;
	}
	
	public void setTitleFont(Font f){
		tFont = f;
	}
	
	public void setTextFont(Font f){
		font = f;
	}
	
	public void setTextColor(Color c){
		textcolor = c;
	}
	
	public void changeScreenBackground(Image newbg){
		s = newbg;
	}
	
	
	public void drawScreen(AffineTransform trans, Graphics2D g){
		g.drawImage(s, trans, null);		
		g.setColor(textcolor);
				
		// Top text
		g.setFont(tFont);
		g.drawString(message, titleX, titleY);
		
		// Info
		g.setFont(font);
		if(showText){
		  for(int i = 0, x = textX, y = textY; i < text.length; i++){
			if(text[i] != null) g.drawString(text[i], x, y);
			y += space;
		}
	  }
		
	}
	
	public void drawScreen(int x, int y, Graphics2D g){
		drawScreen(x, y, s.getWidth(null), s.getHeight(null), g);
	}
	
	public void drawScreen(int x, int y, int w, int h, Graphics2D g){
		AffineTransform t = GameUtilities.getTransform(x, y, 0, (double)w/s.getWidth(null), (double)h/s.getHeight(null));
		drawScreen(t, g);
		
		//System.out.println((double)w/s.getWidth(null));
	}	

}
