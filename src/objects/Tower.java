package objects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import core.GameManager;
import core.GamePanel;

public class Tower extends GameObject {

	long fireTimer = 0;
	private static long fireCooldown = 1000;
	private static double damage = 1;
	private static double bulletSpeed = 1;
	

	public Tower(int x, int y, int width, int height, double speed, Color color) {
		super(x, y, width, height, speed, color);
		// this.x = x + (width/2);
		// this.y = y + (height/2);
		// centerX = (int) (x + width / 2);
		// centerY = (int) (y + height / 2);
	}

	public void draw(Graphics g) {
		// drawCollisionBoxes(g);
		g.drawImage(GamePanel.emuStand.get(0), (int) getX(), (int) getY(), width, height,
				null);
		super.draw(g);
	}

	public void update() {
		super.update();
		if (System.currentTimeMillis() - fireTimer >= getFireCooldown()*(30/GameManager.frameRate)) {
			fire();

			fireTimer = System.currentTimeMillis();
		}
	}

	void fire() {
		Point tower = new Point((int) getCenterX(), (int) getCenterX());
		Point target = new Point((int) (getCenterX() + 500), (int) (getCenterX() + 32));
		Projectile p = new Projectile((int) getCenterX(), (int) getCenterY(), 10, 10,
				getBulletSpeed(), Color.BLUE, tower, target, getDamage(), 0);
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
