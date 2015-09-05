import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


/** A shot fired by the player */
public class Blast extends Entity {
	/** The vertical speed at which the players shot moves */
	private double moveSpeed = -500;
	/** True if this shot has strikes a target */
	private boolean struck = false;
	/** The game in which this entity exists */
	private Game game;
	
	/** Create a new shot from the player
	 * @param game The game where the the shot has been created
	 * @param sprite The sprite representing this shot
	 * @param x The initial x location of the shot
	 * @param y The initial y location of the shot */
	public Blast(Game game, String sprite, int x, int y) {
		super(sprite, x, y);
		this.game = game;
		ySpeed = moveSpeed;
		
		// Creation of blasting sound effect
		try { // open an audio input stream
			  URL url = this.getClass().getClassLoader().getResource("missileFired.wav");
			  AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			  Clip clip = AudioSystem.getClip(); // get a sound clip resource
			  clip.open(audioIn);
			  clip.start();
		  } // end try
		  catch (UnsupportedAudioFileException e){ e.printStackTrace(); }
		  catch (IOException e){ e.printStackTrace();  }
		  catch (LineUnavailableException e){ e.printStackTrace(); }
		
	} // end constructor

	/** Request that this shot moved based on time elapsed
	 * @param delta The time that has elapsed since last move */
	public void move(long delta) {
		super.move(delta); // missile travels
		
		// remove missile graphic if it goes off the edge of the screen
		if (y < -150)
			game.removeEntity(this);
	} // end move
	
	/** Notification that this shot has collided with another entity
	 * @param other The other entity with which we've collided */
	public void collidedWith(Entity other) {

		if (struck) // if it already collided
			return;
		
		// if the missile touches a UFO
		if (other instanceof UFO) {
			// Creation of sound effect upon missile impact
			try { // open an audio input stream
				  URL url = this.getClass().getClassLoader().getResource("UFOexploded.wav");
				  AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
				  Clip clip = AudioSystem.getClip(); // get a sound clip resource
				  clip.open(audioIn);
				  clip.start();
			  } // end try
			  catch (UnsupportedAudioFileException e){ e.printStackTrace(); }
			  catch (IOException e){ e.printStackTrace();  }
			  catch (LineUnavailableException e){ e.printStackTrace(); }
			
			struck = true; // true so that missile cannot kill the rest
			
			// remove the affected entities
			game.removeEntity(this);	// remove the missile
			game.removeEntity(other);	// remove the UFO
			game.notifyUFOKilled(); // notify game an UFO has been killed
			
		} // end if
	}
} // end class