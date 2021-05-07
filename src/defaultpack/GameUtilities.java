/** This is the class that contains a bunch of very
 * useful functions I did not want to have to write over
 * and over again, so I decided to put them into static methods for
 * easy calling. This class will get more methods if I add
 * more common functions in other games I make.
 *
 * @author  Emmanuel Oluloto
 * @version 1.41, 18/04/21 (dd/mm/yy)
 * Last Used On - BattleReloaded
 */

package defaultpack;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;

import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class GameUtilities {

	/* Generate random integers in a certain range, min & max included. */
	public static int getRandomInteger(int min, int max) {

		Random rand = new Random();
		int random = rand.nextInt((max - min) + 1) + min;
		return random;
	}

	/* Check if file exists at location */
	public static void fileExists(String location) {
		File f = new File(location);

		if (f.isFile()) {
			System.out.println("File exists ! File is " + location);
		} else {
			System.out.println("File not found at " + location + "!");
		}
	}

	/* Generates transformation matrix */
	public static AffineTransform getTransform(double x, double y, int rot, double scaleX, double scaleY) {
		AffineTransform specialTrans = new AffineTransform();

		// Rotation
		AffineTransform rotation = new AffineTransform();
		rotation.setTransform(new AffineTransform()); // identity
		rotation.rotate(Math.toRadians(rot), x, y);

		// Translation
		AffineTransform trans = new AffineTransform();
		trans.translate(x, y);

		// Scale
		AffineTransform scaleVal = new AffineTransform();
		scaleVal.scale(scaleX, scaleY);

		specialTrans.concatenate(rotation);
		specialTrans.concatenate(trans);
		specialTrans.concatenate(scaleVal);

		return specialTrans;
	}

	// Return an array made of images of variable size
	public static Image[] placeImages(Image... pics) {
		Image[] img = new Image[pics.length];

		for (int i = 0; i < pics.length; i++)
			img[i] = pics[i];
		return img;

	}

	/* Load all images in specified directory into ArrayList */
	// Need to differentiate between JAR and IDE - currently only works for IDE
	/* POTENTIAL Fixes*/
	// https://mkyong.com/java/java-read-a-file-from-resources-folder/
	// https://stackoverflow.com/questions/11012819/how-can-i-get-a-resource-folder-from-inside-my-jar-file
	// https://stackoverflow.com/questions/1429172/how-to-list-the-files-inside-a-jar-file
	public static ArrayList<Image> loadAllFromDirectory(String dirPath, Object cl) {
		ArrayList<Image> imageList = new ArrayList<Image>();

		final File jarFile = new File(cl.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) { // Run with JAR file
			try {

				final JarFile jar = new JarFile(jarFile);
				final Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
				while (entries.hasMoreElements()) {
					final String name = entries.nextElement().getName();

					if (name.startsWith(dirPath + "/")) { // filter according to the path
						System.out.println(name);
					}
				}

				jar.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else { // Run with IDE
		    
			// Iterate through directory
			final File dir = new File(cl.getClass().getResource(dirPath).getPath());

			for (File fileEntry : dir.listFiles()) {
				
				try {
					Image frame = ImageIO.read(fileEntry);
					imageList.add(frame);

				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Error loading from directory: " + dir.getPath());
				}
			}

	
		}	
		return imageList;
	}
    
	/* Open Website */
	public static void openWebsite(String url) {
		try {
			java.awt.Desktop.getDesktop().browse(new URI(url));
		} catch (IOException | URISyntaxException e) {
			System.out.println("Error opening url: " + url);
			e.printStackTrace();
		}
	}
	

	/* Gets font from location */
	// Object is simply the instance of the class (this) you're in
	public static Font getFont(String location, float size, Object cl) {
		Font font = new Font(null);
		InputStream i = cl.getClass().getClassLoader().getResourceAsStream(location);
		try {

			Font f = Font.createFont(Font.TRUETYPE_FONT, i);
			font = f.deriveFont(Font.BOLD, size);

		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(font);

		return font;
	}

	/*
	 * Reads lines of file at location into array. MAKE SURE you use the right
	 * location.
	 */
	// Object is simply the instance of the class (this) you're in
	public static String[] getInputFromFile(String location, Object cl) {
		String[] input = new String[1];
		InputStream i = cl.getClass().getClassLoader().getResourceAsStream(location);

		try {

			BufferedReader in = new BufferedReader(new InputStreamReader(i));
			String str;

			List<String> list = new ArrayList<String>();
			while ((str = in.readLine()) != null) {
				list.add(str);
			}

			input = list.toArray(new String[0]);
			in.close();

		} catch (IOException | NullPointerException e) {

			e.printStackTrace();
			input[0] = "Broken...";
		}

		return input;
	}

}
