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
		setInstanceNames();
		applyDefaultTransform();
		setCustomTransforms();
	}
	
	private void setInstanceNames() {
	    // looping over keys
        for (String sceneName : backgrounds.keySet())
        {
            // search  for value
        	SceneElement scene = backgrounds.get(sceneName);
        	scene.setName(sceneName);
        }
		

        for (String sceneName : foregrounds.keySet())
        {
        	SceneElement scene = foregrounds.get(sceneName);
        	scene.setName(sceneName);
        }
		
	}

	private void applyDefaultTransform() {
		for(SceneElement scene : backgrounds.values()) {
			AffineTransform tr = new AffineTransform(scene.getTransform());
			tr.scale(.86, .86);
			tr.translate(-50, -400);
			
			scene.setTransform(tr);
		}
		
		for(SceneElement scene : foregrounds.values()) {
			AffineTransform tr = new AffineTransform(scene.getTransform());
			tr.scale(.8, .8);
			
			scene.setTransform(tr);
		}
	}
	
	void setCustomTransforms() {
		// BG
		AffineTransform cus = backgrounds.get("Cartoony Sea").getTransform();
		cus.translate(-80, 400);
		cus.scale(.91, 1);
		
		AffineTransform cus2 = backgrounds.get("The Aquarium").getTransform();
		cus2.translate(0, 200);
		cus2.scale(.8, .8);
		
		AffineTransform cus3 = backgrounds.get("Starfishy").getTransform();
		cus3.translate(-100, 80);
		cus3.scale(.91, .91);
		
		AffineTransform cus4 = backgrounds.get("Yellow School").getTransform();
		cus4.translate(50, 400);
		cus4.scale(.95, .95);
		
		// FG
		AffineTransform cusf = foregrounds.get("Seaweed").getTransform();
		cusf.translate(0, 310);
		
		AffineTransform cusf2 = foregrounds.get("Rocky Reef").getTransform();
		// cusf2.translate(0, -100);
		cusf2.scale(1, -1); cusf2.translate(200, -1320);

		AffineTransform cusf3 = foregrounds.get("TULIPS?!?").getTransform();
		cusf3.scale(.625, .625); 
		cusf3.translate(-180, 550);
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
			AffineTransform trans = new AffineTransform(scene.getTransform());
			trans.concatenate(transform);
			
			g.drawImage(scene.getImage(), trans, null);
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
			backgrounds.put( "Cartoony Sea", new SceneElement(ImageIO.read(this.getClass().getResource("res/scenery/underwaterbg.png"))) );
			backgrounds.put( "The Aquarium", new SceneElement(ImageIO.read(this.getClass().getResource("res/scenery/aquarium-147660_1280.png"))) );
			backgrounds.put( "Starfishy", new SceneElement(ImageIO.read(this.getClass().getResource("res/scenery/the-bottom-of-the-sea.png"))) );
			backgrounds.put( "Yellow School", new SceneElement(ImageIO.read(this.getClass().getResource("res/scenery/sea-2418209.png"))) );
			
			backgrounds.put( "Sponges and Shells", new SceneElement(ImageIO.read(this.getClass().getResource("res/scenery/mainmenu.png"))) );
			backgrounds.put( "Undersea Sandy Beach", new SceneElement(ImageIO.read(this.getClass().getResource("res/scenery/fon0.png"))) );
			backgrounds.put( "Underwater Clearing", new SceneElement( ImageIO.read(this.getClass().getResource("res/scenery/fon2.png"))) );
			backgrounds.put( "Better in Pink", new SceneElement(ImageIO.read(this.getClass().getResource("res/scenery/fon3.png"))) );
			backgrounds.put( "Sunk History", new SceneElement(ImageIO.read(this.getClass().getResource("res/scenery/fon4.png"))) );
			backgrounds.put( "Colorful Undersea Flora", new SceneElement(ImageIO.read(this.getClass().getResource("res/scenery/fon5.png"))) );
			backgrounds.put( "Long Lost Treasure", new SceneElement(ImageIO.read(this.getClass().getResource("res/scenery/fon6.png"))) );
			backgrounds.put( "Another Shipwreck", new SceneElement(ImageIO.read(this.getClass().getResource("res/scenery/ship.jpg"))) );
			
			// FGs
			foregrounds.put( "Rocky Reef", new SceneElement("Rocky Reef", ImageIO.read(this.getClass().getResource("res/scenery/rifs_foreground.png"))) );
			foregrounds.put( "Seaweed", new SceneElement("Seaweed", ImageIO.read(this.getClass().getResource("res/scenery/shoal_foreground.png"))) );
			foregrounds.put( "TULIPS?!?", new SceneElement(ImageIO.read(this.getClass().getResource("res/scenery/tulip-3024741.png"))) );
						
		
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