package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.GameManager;
import core.GamePanel;

public class Emu extends GameObject {
	public static final int NORMAL = 0;
	public static final int SHIELD = 1;
	public static final int MOTHER = 2;
	public static final int BOUNCER = 3;
	public static double healthDifficultyDivisor = 20;
	ArrayList<BufferedImage> emuAnim;
	HealthBar hpBar;
	Shield shield;

	Rectangle head;
	Rectangle body;
	Rectangle legs;

	int type = 0;
	long specialTimer;
	long specialCooldown = 5000;

	// Hitbox adjustment numbers
	int bhXMod = (width / 25);
	int legsXMod = (width / 3);
	int bodyYMod = (int) (height * 0.4);
	
	public Emu(int x, int y, int width, int height, double speed, Color color,
			ArrayList<BufferedImage> emuAnim) {
		super(x, y, width, height, speed, color);
		this.emuAnim = emuAnim;
		double size = (2.0 - speed / 2.1);
		this.width *= size;
		this.height *= size;
		this.hpBar = new HealthBar(this, 50, 10);
		head = new Rectangle();
		body = new Rectangle();
		legs = new Rectangle();
		setAnimCooldown((long) (getAnimCooldown() * (3.2 - speed)));
		int healthMod = (int) ((GameManager.getSecondsSinceStart() / healthDifficultyDivisor) * size * size);
		this.maxHealth = Math.max(2.5 - speed, 1) + 1 + healthMod;
		this.health = maxHealth;
		bhXMod = (this.width / 25);
		legsXMod = (this.width / 3);
		bodyYMod = (int) (this.height * 0.4);
		updateCollisionBoxes();
	}

	public void setType(int type) {
		this.type = type;
		if (type == SHIELD) {
			shield = new Shield((int) (getX() + width / 3),
					(int) (getY() - height / 3), (int) (width * 1.5),
					(int) (height * 1.5), 0, Color.BLUE, this);
			GameManager.addShield(shield);
			setAnim(GamePanel.emuFloat);
		}
		if (type == MOTHER && speed > 0.4) {
			type = NORMAL;
			setAnim(GamePanel.emuFloat);
		} else if (type == MOTHER) {
			setAnim(GamePanel.emuSit);
			this.width *= 1.5;
			this.height *= 1.5;
			this.maxHealth *= 1.5;
			this.health = this.maxHealth;
		}
		else if (type == BOUNCER) {
			int healthMod = (int) (GameManager.getSecondsSinceStart() / healthDifficultyDivisor)*8;
			this.width = 5;
			this.height = 5;
			this.maxHealth = 200+healthMod;
			this.health = this.maxHealth;
		}
	}

	public void update() {
		super.update();
		if (GameManager.frameCount % 2 == 0) {
			hpBar.update();
			if (type == 1) {
				shield.update();
			}
			performSpecial();
		}
		animate();
		translateCollisionBoxes();
		move();
	}

	void performSpecial() {
		if (System.currentTimeMillis() - specialTimer >= specialCooldown) {
			specialTimer = System.currentTimeMillis();
			if (type == MOTHER) {
				Emu e = new Emu((int) getX(), (int) getY(),
						(int) (width / 1.5), (int) (height / 1.5), speed * 1.5,
						Color.BLACK, GamePanel.emuRun);
				GameManager.addEmu(e);
			}
		}

	}
	
	void translateCollisionBoxes(){
		int frameMod = Math.abs(2 - getFrame());
		head.setLocation((int) getX()+bhXMod-frameMod*3, (int) getY());
		body.setLocation((int) getX()+bhXMod, (int) getY()+bodyYMod);
		legs.setLocation((int) getX()+legsXMod, (int) getY()+bodyYMod*2);
	}

	void updateCollisionBoxes() {
		int d = Math.abs(2 - getFrame());
		head.setBounds((int) getX() + (width / 25) - d * 3, (int) getY(),
				(int) (width / 2.5), (int) (height / 2.6));
		body.setBounds((int) getX() + (width / 25),
				(int) (getY() + (height * 0.4)), (int) (width * 0.95),
				(int) (height / 2.5));
		legs.setBounds((int) (getX() + width / 3),
				(int) (getY() + (height * 0.8)), (int) (width / 2.5),
				height / 5);
	}

	void drawCollisionBoxes(Graphics g) {
		g.setColor(Color.BLUE);
		g.drawRect((int) head.x, (int) head.y, head.width, head.height);
		g.drawRect((int) body.x, (int) body.y, body.width, body.height);
		g.drawRect((int) legs.x, (int) legs.y, legs.width, legs.height);
	}

	boolean collidesWith(Rectangle o) {
		if (o.intersects(head) || o.intersects(body) || o.intersects(legs)) {
			return true;
		}
		return false;
	}

	public boolean collidesWith(Projectile o) {
		if (o.collisionBox.intersects(head)) {
			takeDamage(o.getDamage() * Player.headshotMultiplier);
			return true;
		} else if (o.collisionBox.intersects(body)
				|| o.collisionBox.intersects(legs)) {
			takeDamage(o.getDamage());
			return true;
		}
		return false;
	}

	public void move() {
		setX(getX() - speed);
		if (collidesWith(GameManager.base)) {
			GameManager.takeDamage((int) health);
			setAlive(false);
		}
	}

	public void takeDamage(double damage) {
		if (type == SHIELD && shield.isAlive()) {
			shield.takeDamage(damage);
		} else {
			health -= damage;
		}
		if (health <= 0) {
			setAlive(false);
			GameManager.explodeAt((int) getX() - width / 2, (int) getY()
					- height / 2, width, GamePanel.explosion);
			GameManager.incrementScore((int) (maxHealth));
		}
	}

	public void draw(Graphics g) {
		hpBar.draw(g);
		g.drawImage(emuAnim.get(getFrame()), (int) getX() - 10,
				(int) getY() - 15, width + 20, height + 25, null);
		if (GameObject.debugRenderMode == 1) {
			drawCollisionBoxes(g);
		}
		if (type == 1) {
			shield.draw(g);
		}
		super.draw(g);
	}

	public void setAnim(ArrayList<BufferedImage> anim) {
		this.emuAnim = anim;
	}
}
