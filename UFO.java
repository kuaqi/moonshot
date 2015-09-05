

/** An entity which represents one of our space invader aliens. */
public class UFO extends Entity {
	/** The horizontal speed of the UFO */
	private double moveSpeed = 80;
	/** The game in which the entity exists */
	private Game game;
	
	/** Create a new UFO
	 * @param game The game in which this entity is being created
	 * @param ref The sprite which should be displayed for this UFO
	 * @param x The UFO initial x location
	 * @param y The UFO initial y location */
	public UFO(Game game,String ref,int x,int y) {
		super(ref, x, y);
		this.game = game;
		xSpeed = -moveSpeed;
	} // end constructor

	/** Move the UFO based on time elapsed
	 * @param delta The time that has elapsed since last move */
	public void move(long delta) {
		// when reach the left edge of the screen, do a logic update 
		if (x < 10 && xSpeed < 0)
			game.updateLogic();

		// when reach the right edge of the screen, do a logic update
		if (x > 309 && xSpeed > 0) {
			game.updateLogic();
		}
		super.move(delta); // proceed with moving normally
	} // end move
	
	/** Update the game logic related to aliens */
	public void doLogic() {
		// when the UFO reaches the right side of the screen,
		// reverse the direction of the movement.
		xSpeed = -xSpeed;
		y = y + 10;	// move closer to the player
		
		if (y > 640)	// if the UFO has moved past the player in y axis
			game.notifyLost(); // game over
	} // end doLogic
	
	/** Notification that this alien has collided with another entity
	 * @param other The other entity */
	public void collidedWith(Entity other) {}
		// collisions are handled in superclass
} // end class