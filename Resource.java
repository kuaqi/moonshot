

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.io.IOException;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.awt.Image;

import javax.imageio.ImageIO;

/** Provides, cache and loads game resources - sprites. */
public class Resource {
	/** The singleton instance of this class */
	private static Resource single = new Resource();
	
	/** The cached sprite map, from reference to sprite instance */
	private HashMap sprites = new HashMap();
	
	/** Get the single instance of this class 
	 * @return The single instance of this class */
	public static Resource get() {
		return single;
	} // end constructor
	
	/** Retrieve a sprite from the store
	 * @param imageReference The reference to the image to use for the sprite
	 * @return A sprite instance containing image of the reference */
	public Sprite getSprite(String imageReference) {
		// return the existing cached sprite if available
		if (sprites.get(imageReference) != null)
			return (Sprite) sprites.get(imageReference);
		
		// if not, grab the sprite from the resource loader
		BufferedImage sourceImage = null;
		
		try { // Get the sprite from the appropriate place
			URL url = this.getClass().getClassLoader().getResource(imageReference);
			if (url == null)
				failLoad("Can't find ref: "+imageReference); // end if
			sourceImage = ImageIO.read(url); // read image with ImageIO
		} catch (IOException e) { failLoad("Load failure: "+ imageReference); }
		
		// create an accelerated image of the right size to store our sprite in
		GraphicsConfiguration gConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image image = gConfig.createCompatibleImage(sourceImage.getWidth(),sourceImage.getHeight(),Transparency.BITMASK);
		
		// draw the source image into the accelerated image
		image.getGraphics().drawImage(sourceImage, 0, 0, null);

		Sprite sprite = new Sprite(image); // create sprite
		sprites.put(imageReference, sprite); // add sprite to cache
		
		return sprite;
	}
	
	 /** @param message The message to display on resource loading failure. */
	private void failLoad(String message) {
		System.err.println(message);
		System.exit(0);	// program termination
	} // end fail
} // end class