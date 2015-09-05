// This is the superclass/base class

import java.awt.Graphics;
import java.awt.Rectangle;

/** An entity is any element that appears in the game.
 * Double data type allows for more accurate movement and positioning */
public abstract class Entity {
	protected double x; // the object's current location in x axis
	protected double y; // the object's current location in y axis
	/** The current speed of this entity horizontally (pixels/sec) */
	protected double xSpeed;
	/** The current speed of this entity vertically (pixels/sec) */
	protected double ySpeed;
	/** The rectangle used for this entity during collisions resolution */
	private Rectangle me = new Rectangle();
	private Rectangle him = new Rectangle();
	/** The sprite that represents this entity */
	protected Sprite sprite;
	
	/** Construct a entity based on a sprite image and a location.
	 * @param ref The reference to the image to be displayed for this entity
 	 * @param x The initial x location of this entity
	 * @param y The initial y location of this entity */
	public Entity(String ref,int x,int y) {
		this.sprite = Resource.get().getSprite(ref);
		this.x = x;
		this.y = y;
	} // end constructor
	
	/** Get the x location of this entity. */
	public int getX() {
		return (int) x;
	} // end getX

	/** Get the y location of this entity. */
	public int getY() {
		return (int) y;
	} // end getY
	
	/** Set the horizontal speed of this entity
	 * @param xSpeed The horizontal speed of this entity (pixels/sec) */
	public void setHorizontalMovement(double xSpeed) {
		this.xSpeed = xSpeed;
	}
	
	/** Get the horizontal speed of this entity
	 * @return The horizontal speed of this entity (pixels/sec) */
	public double getHorizontalMovement() {
		return xSpeed;
	}

	/** Set the vertical speed of this entity
	 * @param xSpeed The vertical speed of this entity (pixels/sec) */
	public void setVerticalMovement(double ySpeed) {
		this.ySpeed = ySpeed;
	}

	/** Get the vertical speed of this entity
	 * @return The vertical speed of this entity (pixels/sec) */
	public double getVerticalMovement() {
		return ySpeed;
	}
	
	/** Move the entity based after a certain amount of time lapse
	 * @param delta Total time elapsed in milliseconds */
	public void move(long delta) {
		// update the location of the entity based on move speeds
		x = x + (delta * xSpeed) / 1000;
		y = y + (delta * ySpeed) / 1000;
	} // end move
	
	/** Draw this entity to the graphics context provided
	 * @param g The graphics context on which to draw */
	public void draw(Graphics g) {
		sprite.draw(g,(int) x,(int) y);
	}
	
	/** Check if this entity collided with another.
	 * @param other The other entity to check collision against
	 * @return True if the entities collide with each other */
	public boolean collidesWith(Entity other) {
		me.setBounds((int) x,(int) y, sprite.getWidth(), sprite.getHeight());
		him.setBounds((int) other.x,(int) other.y,other.sprite.getWidth(),other.sprite.getHeight());

		return me.intersects(him);
	} // end collide
	
	/** To periodically ensure certain game events will happen (e.g movement) */
	public void doLogic() {} // to be overridden; for derived class use
	
	/** Notification that this entity collided with another.
	 * @param other The entity with which this entity collided. */
	public abstract void collidedWith(Entity other); // to be overridden
} // end class