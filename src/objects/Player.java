package objects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Timer;

import core.EmuCore;
import core.GameManager;
import core.GamePanel;

public class Player extends GameObject {
	private boolean isFiring = false;
	private boolean isFiringScattered = false;
	long fireTimer = 0;
	private long fireCooldown = 600;

	public static boolean up;
	public static boolean down;
	static double headshotMultiplier = 2;
	private double speed = 1;
	private double damage = 1;
	private double bulletSpeed = 3;

	public Player(int x, int y, int width, int height, Color color) {
		super(x, y, width, height, height, color);
	}

	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect((int) getX(), (int) getY(), width, height);
		
		// draw the center point
		g.setColor(Color.CYAN);
		super.draw(g);
	}

	public void update() {
		super.update();
		if (isFiring() && System.currentTimeMillis() - fireTimer >= getFireCooldown()) {
			fire();

			fireTimer = System.currentTimeMillis();
		}

		if (isFiringScattered()
				&& System.currentTimeMillis() - fireTimer >= getFireCooldown()) {
			fireRandom();

			fireTimer = System.currentTimeMillis();
		}

		if (up) {
			setY(getY() - getSpeed());
		}
		if (down) {
			setY(getY() + getSpeed());
		}
	}

	void fireAt(int x, int y) {
		Point play = new Point((int) getCenterX(), (int) getCenterY());
		Point target = new Point(x, y);
		Projectile p = new Projectile((int) getCenterX(), (int) getCenterY(), 10, 10,
				getBulletSpeed(), Color.orange, play, target, getDamage());
		GameManager.addBullet(p);
	}

	void fireRandom() {
		fireAt(EmuCore.WIDTH, GamePanel.gen.nextInt(EmuCore.HEIGHT));
	}

	void fire() {
		fireAt(GameManager.getClickedPoint().x, GameManager.getClickedPoint().y);
	}

	public double getBulletSpeed() {
		return bulletSpeed;
	}

	public void setBulletSpeed(double bulletSpeed) {
		this.bulletSpeed = bulletSpeed;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public long getFireCooldown() {
		return fireCooldown;
	}

	public void setFireCooldown(long fireCooldown) {
		this.fireCooldown = fireCooldown;
	}

	public boolean isFiringScattered() {
		return isFiringScattered;
	}

	public void setFiringScattered(boolean isFiringScattered) {
		this.isFiringScattered = isFiringScattered;
	}

	public boolean isFiring() {
		return isFiring;
	}

	public void setFiring(boolean isFiring) {
		this.isFiring = isFiring;
	}
}
