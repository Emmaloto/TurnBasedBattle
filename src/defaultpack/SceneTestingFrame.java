package defaultpack;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class SceneTestingFrame extends JComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2885425818544064374L;
	private Image image, charImage, fishImage;
	
	private JFrame frame;
	private int DEFAULT_WIDTH, DEFAULT_HEIGHT;
	private int canvasW, canvasH; // To position things relative to screen

	
	public static void main(String[] args) {
		new SceneTestingFrame();
	}

	SceneTestingFrame(){
		try {
			// TODO Place image location here -> res/scenery/fon5.png
			image = ImageIO.read(this.getClass().getResource("res/scenery/tulip-3024741.png"));
			charImage = ImageIO.read(this.getClass().getResource("res/Charcalm.png"));
			fishImage = ImageIO.read(this.getClass().getResource("res/enemy/shark.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DEFAULT_WIDTH = 1600;
		DEFAULT_HEIGHT = 1000;
		
		frame = new JFrame();
		frame.setTitle("View Scenery");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //Close window on exit
		frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);	    
		frame.getContentPane().add(this);	
		frame.setFocusable(true);
	    frame.requestFocusInWindow();	  
	    frame.setVisible(true);
	    
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		canvasW = frame.getWidth();
		canvasH = frame.getHeight();
		
		AffineTransform scaleToCanvas = new AffineTransform();
		scaleToCanvas.scale(rescaleW(), rescaleH());
		
		if(image != null) {
			// For BG
			AffineTransform transformTest = new AffineTransform(scaleToCanvas);
			transformTest.scale(.9, .9);
			transformTest.translate(-50, -400);
			
//			transformTest.translate(50, 400);
//			transformTest.scale(.95, .95);
	
			// For FG
			AffineTransform transformTestFG = new AffineTransform(scaleToCanvas);
			transformTestFG.scale(.5, .5);
			transformTestFG.translate(-180, 550);
			
			g2d.drawImage(image, transformTestFG, null);
			
		}
		
		// Hero
		AffineTransform trans = new AffineTransform();
		trans.translate(0, setPosY(10));
		trans.concatenate(scaleToCanvas);
		g2d.drawImage(charImage, trans, null);

		// Enemy
		AffineTransform transE = new AffineTransform();
		transE.translate(canvasW / 1.6, setPosY(4));
		transE.concatenate(scaleToCanvas);
		transE.scale(.5, .5);
		g2d.drawImage(fishImage, transE, null);
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
		return (double) frame.getWidth() / (double) DEFAULT_WIDTH;
	}

	private double rescaleH() {
		return (double) frame.getHeight()  / (double) DEFAULT_HEIGHT;
	}

}
