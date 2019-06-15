package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import core.GameManager;
import core.GamePanel;

public class Tower extends GameObject {
	public static boolean canMove = false;
	static int seekRange;
	int frame = 0;
	long fireTimer = 0;
	private static long fireCooldown = 1000;
	private static double damage = 1;
	private static double bulletSpeed = 1;
	static int speed = 2;
	Emu target;

	public Tower(int x, int y, int width, int height, Color color) {
		super(x, y, width, height, speed, color);
		// this.x = x + (width/2);
		// this.y = y + (height/2);
		// centerX = (int) (x + width / 2);
		// centerY = (int) (y + height / 2);
	}

	public void draw(Graphics g) {
		// drawCollisionBoxes(g);
		int f = 0;
		if (getFrame() < GamePanel.turretFire.size()) {
			f = getFrame();
		}
		g.drawImage(GamePanel.turretFire.get(f), (int) getX(), (int) getY(),
				width, height, null);

		super.draw(g);
	}

	public void update() {
		super.update();
		
		if(target != null && target.isAlive()){
			if(target.getY()>getY()){
				setY(getY()+speed);
			}
			if(target.getY()<getY()){
				setY(getY()-speed);
			}
		}
		else if(GameManager.getClosestEmus().size() > 0){
			ArrayList<Emu> c = GameManager.getClosestEmus();
			int n = GamePanel.gen.nextInt(c.size());
			target = c.get(n);
		}
		
		if (isActive) {
			animateOnce(GamePanel.turretFire.size());
		}
		if (getFireCooldown() < getAnimCooldown() * 6) {
			setAnimCooldown((long) (getFireCooldown() * 0.1));
		}
		if (System.currentTimeMillis() - fireTimer >= getFireCooldown()
				* (30 / GameManager.frameRate)) {
			fire();
			startAnimation();
			fireTimer = System.currentTimeMillis();
		}
	}

	void fire() {
		Point tower = new Point((int) getCenterX(), (int) getCenterY());
		Point target = new Point((int) (getCenterX() + 500),
				(int) (getCenterY() + GameManager.mouseYOffset));
		Projectile p = new Projectile((int) getCenterX() + 15,
				(int) getCenterY() + 3, getBulletSpeed(), Color.BLUE, tower,
				target, getDamage());
		GameManager.addBullet(p);
	}

	public static double getDamage() {
		return damage;
	}

	public static void setDamage(double damage) {
		Tower.damage = damage;
	}

	public static double getBulletSpeed() {
		return bulletSpeed;
	}

	public static void setBulletSpeed(double bulletSpeed) {
		Tower.bulletSpeed = bulletSpeed;
	}

	public static long getFireCooldown() {
		return fireCooldown;
	}

	public static void setFireCooldown(long fireCooldown) {
		Tower.fireCooldown = fireCooldown;
	}

}
