package defaultpack;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class SceneController {
	private HashMap<String, SceneElement> backgrounds = new HashMap<String, SceneElement>();
	private HashMap<String, SceneElement> foregrounds = new HashMap<String, SceneElement>();
	
	private SceneElement currentBG = null, currentFG = null;
	
	public SceneController() {
		loadImages();
	}
	
	// For external selection of background
	public boolean setBackground(String bgName) {
		currentBG = backgrounds.get(bgName);
		return currentBG == null;
	}
	

	public boolean setForeground(String fgName) {
		currentFG = foregrounds.get(fgName);
		return currentFG == null;
	}
	
	// Drawing images on canvas
	public void drawBackground(AffineTransform transform, Graphics2D g) {
		drawImageOnCanvas(currentBG, transform, g);
	}
	
	public void drawForeground(AffineTransform transform, Graphics2D g) {
		drawImageOnCanvas(currentFG, transform, g);
	}
	
	private void drawImageOnCanvas(SceneElement scene, AffineTransform transform, Graphics2D g) {
		if(scene != null) {
			AffineTransform t = new AffineTransform(scene.getTransform());
			t.concatenate(transform);
			
			g.drawImage(scene.getImage(), t, null);
		}
		
	}
	
	public String [] getAvailableNames(boolean getBGs) {
		String [] nameList = {};
		if(getBGs) {
			nameList = backgrounds.keySet().toArray(nameList);
		}else {
			nameList = foregrounds.keySet().toArray(nameList);
		}
		return nameList;
	}
	
	
	private void loadImages() {
		
		try {
			// BGs
			backgrounds.put( "Cartoony Sea", new SceneElement("Cartoony Sea", ImageIO.read(this.getClass().getResource("res/scenery/underwaterbg.png"))) );
			backgrounds.put( "Underwater Clearing", new SceneElement("Underwater Clearing", ImageIO.read(this.getClass().getResource("res/scenery/mainmenu.png"))) );
			backgrounds.put( "Undersea Shipwreck", new SceneElement("Shipwreck under the Sea", ImageIO.read(this.getClass().getResource("res/scenery/fon5.png"))) );
			
			// FGs
			foregrounds.put( "Rocky Reef", new SceneElement("Rocky Reef", ImageIO.read(this.getClass().getResource("res/scenery/rifs_foreground.png"))) );
			foregrounds.put( "Seaweed", new SceneElement("Seaweed", ImageIO.read(this.getClass().getResource("res/scenery/shoal_foreground.png"))) );
			
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}
}

// For managing states used to draw & identify a scene image
class SceneElement{

	private String name;
	private Image image;
	private AffineTransform transform;
	private Color uiTextColor;
	

	public SceneElement(String n, Image i, AffineTransform t, Color tc){
		name = n;
		image = i;
		transform = t;
		uiTextColor = tc;
	}

	public SceneElement(){
		this("NULL", null, new AffineTransform(), Color.BLACK);
	}
	
	public SceneElement(Image i){
		this("IMAGE_" + GameUtilities.getRandomInteger(0, 500), i, new AffineTransform(), Color.BLACK);
	}

	public SceneElement(String n, Image i){
		this(n, i, new AffineTransform(), Color.BLACK);
	}
	
	public SceneElement(Image i, AffineTransform t){
		this("IMAGE" + GameUtilities.getRandomInteger(0, 500), i, t, Color.BLACK);
	}
	
	public SceneElement(String n, Image i, AffineTransform t){
		this(n, i, t, Color.BLACK);
	}
	
	
	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public AffineTransform getTransform() {
		return transform;
	}

	public void setTransform(AffineTransform transform) {
		this.transform = transform;
	}

	public Color getUiTextColor() {
		return uiTextColor;
	}

	public void setUiTextColor(Color uiTextColor) {
		this.uiTextColor = uiTextColor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}