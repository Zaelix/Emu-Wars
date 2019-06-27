package objects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import core.EmuCore;
import core.GamePanel;


public class GameObject {
	public static int debugRenderMode = 0;
	double maxHealth = 2;
	double health = 2;
	private double x;
	private double y;
	private double centerX;
	private double centerY;
	int width;
	int height;
	double angle;
	double vx;
	double vy;
	double speed;
	Color color;
	private boolean isAlive = true;
	private Rectangle collisionBox;
	private long animTimer;
	private long animCooldown = 100;
	private int frame;
	
	private long actionTimer;
	private long actionCooldown = 10;
	public boolean isActive = true;
	
	GameObject(int x, int y, int width, int height, double speed, Color color){
		this.setX(x - (width/2));
		this.setY(y - (height/2));
		this.width = width;
		this.height = height;
		this.speed = speed;
		this.color = color;
		
		setCollisionBox(new Rectangle(x,y,width,height));
	}
	
	void update(){
		setCenterX((int) (getX() + width / 2));
		setCenterY((int) (getY() + height / 2));
		if(getX()>EmuCore.WIDTH + 250 || getX() < 200){
			setAlive(false);	
		}
		if(getY()>EmuCore.HEIGHT + 250 || getY() < -250){
			setAlive(false);
		}
		getCollisionBox().setBounds((int)getX(), (int)getY(), width, height);
	}
	
	void draw(Graphics g){
		if(debugRenderMode == 1){
			g.setColor(Color.RED);
			g.drawRect(getCollisionBox().x, getCollisionBox().y, getCollisionBox().width, getCollisionBox().height);
			g.setColor(Color.CYAN);
			g.drawOval((int)getCenterX()-2, (int)getCenterY()-2, 4, 4);
		}
	}
	
	void animate(){
		if (System.currentTimeMillis() - animTimer >= getAnimCooldown()) {
			animTimer = System.currentTimeMillis();
			setFrame(getFrame() + 1);
			if(getFrame() > 3){
				setFrame(0);
			}
		}
	}
	
	void animateOnce(int animationLength){
		if (System.currentTimeMillis() - animTimer >= getAnimCooldown()) {
			animTimer = System.currentTimeMillis();
			setFrame(getFrame() + 1);
			if (getFrame() >= animationLength) {
				isActive = false;
			}
		}
	}

	public double getCenterX() {
		return centerX;
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public int getAverageSize(){
		return (width+height)/2;
	}

	public long getAnimCooldown() {
		return animCooldown;
	}

	public void setAnimCooldown(long animCooldown) {
		this.animCooldown = animCooldown;
	}

	protected void setAnimTimer(long animTimer) {
		this.animTimer = animTimer;
		
	}
	
	public void startAnimation() {
		setFrame(0);
		setAnimTimer(System.currentTimeMillis());
		isActive = true;
	}

	public int getFrame() {
		return frame;
	}

	public void setFrame(int frame) {
		this.frame = frame;
	}

	public Rectangle getCollisionBox() {
		return collisionBox;
	}

	public void setCollisionBox(Rectangle collisionBox) {
		this.collisionBox = collisionBox;
	}
}
