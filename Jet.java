
/** The entity that represents the players ship */
public class Jet extends Entity {
	/** The game in which the ship exists */
	private Game game;
	
	/** Create the player's ship.
	 * @param game The game in which the ship is being created
	 * @param reference The reference to the sprite to show for the ship
	 * @param x Player initial x location
	 * @param y Player initial y location */
	public Jet(Game game, String reference, int x, int y) {
		super(reference, x, y);
		this.game = game;
	} // end constructor
	
	/** Request that the ship move itself based on an elapsed amount of time
	 * @param delta The time that has elapsed since last move (ms) */
	public void move(long delta) {
		// Stop moving left if object reach left edge of the screen
		if (x < 10 && xSpeed < 0)
			return;
		// Stop moving right if object reach right edge of the screen
		if (x > 266 && xSpeed > 0) {
			return;
		}
	
		super.move(delta);
	}
	
	/** Notify player has collided with an entity
	 * @param other The entity with which the ship has collided */
	public void collidedWith(Entity other) {
		// if the collided element is an UFO then game over 
		if (other instanceof UFO) {
			game.notifyDeath();
		} // end if
		
	}
} // end class