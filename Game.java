// User Interface Programming Graphics - Project | Norman Low Wei Kit

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Game extends Canvas {
	/** For the usage of page flipping acceleration */
	private BufferStrategy strategy;
	/** The list of all the entities that exist in our game */
	private ArrayList entities = new ArrayList();
	/** The list of entities that need to be removed from the game this loop */
	private ArrayList removeList = new ArrayList();
	private int alienCount; // Aliens left alive on screen
	private String message = ""; // game message
	/** The entity representing the player */
	private Entity ship;
	/** The speed at which the player's ship should move (pixels/sec) */
	private double moveSpeed = 300;
	/** The interval between our players shot (ms) */
	private long firingInterval = 300; // 
	/** The time which last fired a shot */
	private long timeLastFired = 0;
	/** True if the game is currently running */
	private boolean gameRunning = true;
	private boolean leftPressed = false;  // True if left arrow key is pressed
	private boolean rightPressed = false; // True if right arrow key is pressed
	private boolean firePressed = false;  // True if firing
	/** True if until a key has been pressed */
	private boolean gamePaused = true;
	/** True if game logic needs to be adjusted this loop */
	private boolean checkLogic = false;
	BufferedImage bgImage = null;	// stores background image temporarily
	public int screenWidth = 360;	// screen width
	public int screenHeight = 640;	// screen height
	JFrame frame = new JFrame("Moonshot");
	
	/** Constructor. Build the game. */
	public Game() {

//		JFrame frame = new JFrame("Moonshot"); // the frame (container)
		// store the frame content into panel
		JPanel mainPanel = (JPanel) frame.getContentPane(); // cast ContentPane
//		mainPanel.setMinimumSize(new Dimension(screenWidth,screenHeight));;
		mainPanel.setLayout(null);
		// Set canvas size and put it into the frame's content
		setBounds(0, 0, screenWidth, screenHeight);
		mainPanel.add(this);
//		frame.getContentPane().add(menu);
//		mainPanel.add(menu);
		
		// Creation of the PROGRAM ICON
		BufferedImage sourceImage = null;
//		try { sourceImage = ImageIO.read(new File("images/greenUFO.png")); }
		try { sourceImage = ImageIO.read(new File("greenUFO.png")); }
		catch (IOException e){
			System.out.println("Icon image retrieve error.");
			JOptionPane.showMessageDialog(null, "Icon image retrieve error.");
		} // end catch
		Image programIcon = sourceImage;		
		
		// Creation of GAME BACKGROUND IMAGE (cloud and skies environment)
		bgImage = null;
//		try { bgImage = ImageIO.read(new File("images/battleground01.gif")); }
		try { bgImage = ImageIO.read(new File("battleground01.gif")); }
		catch (IOException e){
			System.out.println("BG image retrieve error.");
			JOptionPane.showMessageDialog(null, "BG image retrieve error.");
		} // end catch
		
		// Creation of GAME SOUNDTRACK
		try { // open an audio input stream
			URL url = this.getClass().getClassLoader().getResource("universal-edit.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			Clip clip = AudioSystem.getClip(); // get a sound clip resource
			clip.open(audioIn);
			clip.loop(1);
			clip.start();
		} // end try
		catch (UnsupportedAudioFileException e){ e.printStackTrace(); }
		catch (IOException e){ e.printStackTrace();  }
		catch (LineUnavailableException e){ e.printStackTrace(); }
		
		// Add a listener to listen to user intending to close the window
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			} // end windowClosing
		});
		
		addKeyListener(new KeyInputHandler()); // add keyboard input listener
		requestFocus(); // request key events

		frame.setResizable(true);
		frame.setSize(screenWidth,screenHeight);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(programIcon);
		frame.setVisible(true);
		
		setIgnoreRepaint(true); // manually repaint in accelerated mode
		// for use with Canvas and double/triple buffering
		createBufferStrategy(3); // create AWT's triple buffering to manage accelerated graphics
		strategy = getBufferStrategy();
		
		initEntity(); // load up ship and UFO at game startup
	} // end constructor
	
	/** Initialize the starting state of the entities (ship and aliens). 
	 * Add each entities to a list of entities in the game. */
	private void initEntity() {
		// Creation of a block of UFOs - 6 rows by 5 columns
		alienCount = 0;
		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 5; column++) {
//				Entity alien = new UFO(this, "images/greenUFO.png", (column * 60) + 38, (row * 40) + 50);
				Entity alien = new UFO(this, "greenUFO.png", (column * 60) + 38, (row * 40) + 50);
				entities.add(alien);
				alienCount++;
			} // end for
		} // end for
		
		// Creation of PLAYER JET FIGHTER and set the position
		int initLocX = (getWidth() / 2) - (84 / 2); // 84 represents image width
		int initLocY = getHeight() - 136; // 136 represents the image height
//		ship = new Jet(this, "images/jetFighter.png",initLocX, initLocY);
		ship = new Jet(this, "jetFighter.png",initLocX, initLocY);
		entities.add(ship);
		
	} // end initEntities
	
	/** Clears old data, initialize and start a new game. */
	private void startGame() {
		leftPressed = false;	// reset
		rightPressed = false;	// reset
		firePressed = false;	// reset
		entities.clear();	// clear the game
		initEntity();		// initialize a new game
	} // end startGame
	
	/** Game is placed in a loop to give movement to the player and UFOs. 
	 * Background image is continuously drawn, collision calculation, 
	 * listen to user keyboard input. */
	public void gameLoop() {
		long lastLoopTime = System.currentTimeMillis();
		
		// Keep looping until game ends
		while (gameRunning) {
			// calculate how far entities should move since last update
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();
			
			// Creation of GAME BACKGROUND IMAGE
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			
			if (bgImage == null){ // if image retrieve error
				g.setColor(new Color(49, 75, 90));	// colour of the game BG
				g.fillRect(0, 0, screenWidth, screenHeight);
			} // end if
			else // otherwise load game BG
				g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);			
			
			// cycle round asking each entity to move itself
			if (!gamePaused) {
				for (int i = 0; i < entities.size(); i++) {
					Entity entity = (Entity) entities.get(i);
					
					entity.move(delta);
				} // end for
			} // end if
			
			// cycle through array list to draw all the entities in the game
			for (int i = 0; i < entities.size(); i++) {
				Entity entity = (Entity) entities.get(i);
				entity.draw(g);
			}
			
			// collision
			for (int i = 0; i < entities.size(); i++) {
				for (int j = i + 1; j < entities.size(); j++) {
					Entity me = (Entity) entities.get(i);
					Entity him = (Entity) entities.get(j);
					
					if (me.collidesWith(him)) {
						me.collidedWith(him);
						him.collidedWith(me);
					} // end if
				} // end inner for
			} // end outer for
			
			entities.removeAll(removeList); // marked entity is to be cleared up
			removeList.clear();

			// check every game entity to make sure they function as supposed
			// the UFOs will reverse their movement direction with this check
			if (checkLogic) {
				for (int i = 0; i < entities.size(); i++) {
					Entity entity = (Entity) entities.get(i);
					entity.doLogic();
				} // end for
				
				checkLogic = false;
			} // end if
			
			if (gamePaused) { // if the game is paused
				g.setColor(Color.white);
				g.setFont(new Font("Arial", Font.BOLD, 14));
				g.drawString(message,(getWidth() - g.getFontMetrics().stringWidth(message))/2, 250);
				g.drawString("Press Any Key",(getWidth() - g.getFontMetrics().stringWidth("Press Any Key"))/2, getHeight() / 2);
			} // end if
			
			g.dispose(); // flip the buffer once drawing is complete
			strategy.show();
			
			if (firePressed && !gamePaused) // if fire button is pressed
				fireAttempt(); // try to fire
			
			ship.setHorizontalMovement(0); // Reset the player movement rate
			
			if (leftPressed && !rightPressed)
				ship.setHorizontalMovement(-moveSpeed);
			
			else if (rightPressed && !leftPressed)
				ship.setHorizontalMovement(moveSpeed);
			
			// System pause for a while
			try { Thread.sleep(30); } 
			catch (Exception e) {}
		} // end while gameRunning
	} // end game loop
	
	/** Attempt to create a blast or shot from the player. 
	If the interval is still running, shot cannot be fired. */
	public void fireAttempt() {
		long currentTime = System.currentTimeMillis();
		// check if the time elapsed is more than the firing interval or not
		if (currentTime - timeLastFired < firingInterval) {
			return; // abort the function
		} // end if

		// example: 123000999 - 123000971 < 300
		if (currentTime - timeLastFired > firingInterval){
		timeLastFired = System.currentTimeMillis(); // record new last fired time
		Blast shot = new Blast( // create the shot graphic
//				this, "images/missile.png", ship.getX() + 28, ship.getY() - 20);
				this, "missile.png", ship.getX() + 28, ship.getY() - 20);

		entities.add(shot);

		} // end if
	} // end fire
	
	/** An update from the game entity with regards to the game logic */
	public void updateLogic() {
		checkLogic = true;
	} // end updateLogic
	
	/** Remove an entity from the game.
	 * @param entity The entity that should be removed */
	public void removeEntity(Entity entity) {
		removeList.add(entity);
	} // end removeEntity
	
	/** To notify game lost. */
	public void notifyLost() {
		gamePaused = true;
		message = "You Lost!";
		
		// Creation of SFX sound lose game
		try { // open an audio input stream
			URL url = this.getClass().getClassLoader().getResource("sfx-retro-lose.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			Clip clip = AudioSystem.getClip(); // get a sound clip resource						  
			clip.open(audioIn);
			clip.start();
		} // end try
		catch (UnsupportedAudioFileException e){ e.printStackTrace(); }
		catch (IOException e){ e.printStackTrace();  }
		catch (LineUnavailableException e){ e.printStackTrace(); }
	} // end notifyLost
	
	/** To notify game has been won by player. */
	public void notifyWin() {
		gamePaused = true;
		message = "You Win!";
	} // end notifyWin
	
	/** To notify game lost. */
	public void notifyDeath() {
		gamePaused = true;
		message = "UFO got you. Retry?";
	} // end notifyDeath
	
	/** Notify that an alien has been killed */
	public void notifyUFOKilled() { // speed of UFO is controlled here
		alienCount--; // count the decrease number of UFO population
		
		if (alienCount == 0) { // there are no more UFO left
			notifyWin();
			
			// Creation of WINNING SOUND FX
			try { // open an audio input stream
				  URL url = this.getClass().getClassLoader().getResource("success.wav");
				  AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
				  Clip clip = AudioSystem.getClip(); // get a sound clip resource
				  clip.open(audioIn);
				  clip.start();
			  } // end try
			  catch (UnsupportedAudioFileException e){ e.printStackTrace(); }
			  catch (IOException e){ e.printStackTrace();  }
			  catch (LineUnavailableException e){ e.printStackTrace(); }

		} // end if
		
		// speed up all existing aliens if population is low
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = (Entity) entities.get(i);
			
			if (entity instanceof UFO) {
				// 10% speed boost
				entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.10);
			} // end if
		} // end for
	}
	
	/** Custom AWT class to handle keyboard input from the user. */
	private class KeyInputHandler extends KeyAdapter {
		/** The number of key presses accumulated while waiting for start game */
		private int pressCount = 1;
		
		/** Keypad pushed down and unreleased(hold)
		 * @param e The key that was pressed */
		public void keyPressed(KeyEvent e) {
			if (gamePaused)
				return; // this function made unavailable until game starts
			
			if (e.getKeyCode() == KeyEvent.VK_A)
				leftPressed = true;
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				leftPressed = true;
			
			if (e.getKeyCode() == KeyEvent.VK_D)
				rightPressed = true;
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				rightPressed = true;
			
			if (e.getKeyCode() == KeyEvent.VK_SPACE)
				firePressed = true;
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
				firePressed = true;
			
		} // end keyPressed
		
		/** Notification from AWT that a key has been released.
		 * @param e The key button which was released */
		public void keyReleased(KeyEvent e) {
			if (gamePaused)
				return; // this function made unavailable until game starts
			// inform key button is no longer pressed and released
			if (e.getKeyCode() == KeyEvent.VK_A)
				leftPressed = false;
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				leftPressed = false; 
		
			if (e.getKeyCode() == KeyEvent.VK_D)
				rightPressed = false;
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				rightPressed = false;
			
			if (e.getKeyCode() == KeyEvent.VK_SPACE)
				firePressed = false;
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
				firePressed = false;

		} // end keyReleased

		/** Notification that a key has been typed. Press and release.
		 * @param e The key details that was typed. */
		public void keyTyped(KeyEvent e) {
			// press count to check if there are still any keys being typed
			if (gamePaused) {
				if (pressCount == 1) { // once we receive a key, we start game
					gamePaused = false;
					startGame();
					pressCount = 0;
				} else
					pressCount++;
			} // end if
			
			if (e.getKeyChar() == KeyEvent.VK_ESCAPE) // quit game - 'ESC' button
				System.exit(0);
		} // end keyTyped
	} // end keyInputHandler
	
	/** The entry point into the game. */
	public static void main(String[] args) {
//		Menu menu = new Menu();
		Game game = new Game();
		game.gameLoop(); // Loop until game finished running.
	} // end main
} // end class
