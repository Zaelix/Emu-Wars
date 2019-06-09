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

	long grenadeTimer = 0;
	private long grenadeCooldown = 4000;
	int grenades = 3;
	int maxGrenades = 3;

	public Player(int x, int y, int width, int height, Color color) {
		super(x, y, width, height, height, color);
	}

	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect((int) getX(), (int) getY(), width, height);

		// draw the center point
		g.setColor(Color.CYAN);
		drawGrenadeCount(g);
		super.draw(g);
	}

	public void drawGrenadeCount(Graphics g) {
		g.setFont(GameManager.defaultFont);
		g.setColor(Color.BLACK);
		g.drawString(grenades+"", (int) getX(), (int) getY() - 4);
		g.drawString("x", (int) getX()+15, (int) getY() - 4);
		g.drawImage(GamePanel.grenade, (int) getX() + 25, (int) getY() - 25,
				25, 25, null);
	}

	public void update() {
		super.update();
		if (isFiring()
				&& System.currentTimeMillis() - fireTimer >= getFireCooldown()) {
			fire();

			fireTimer = System.currentTimeMillis();
		}

		if (isFiringScattered()
				&& System.currentTimeMillis() - fireTimer >= getFireCooldown()) {
			fireRandom();

			fireTimer = System.currentTimeMillis();
		}
		
		if (System.currentTimeMillis() - grenadeTimer >= grenadeCooldown) {
			grenadeTimer = System.currentTimeMillis();
			if(grenades < maxGrenades){
				grenades++;
			}
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
		Projectile p = new Projectile((int) getCenterX(), (int) getCenterY(),
				getBulletSpeed(), Color.orange, play, target, getDamage());
		GameManager.addBullet(p);
	}

	void fireRandom() {
		fireAt(EmuCore.WIDTH, GamePanel.gen.nextInt(EmuCore.HEIGHT));
	}

	void fire() {
		fireAt(GameManager.getClickedPoint().x, GameManager.getClickedPoint().y);
	}

	public void throwGrenade() {
		if (grenades > 0) {
			Point play = new Point((int) getCenterX(), (int) getCenterY());
			Point target = new Point(GameManager.getClickedPoint().x,
					GameManager.getClickedPoint().y);
			Grenade g = new Grenade((int) getCenterX(), (int) getCenterY(), 5,
					play, target);
			GameManager.addGrenade(g);
			grenades--;
		}
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
