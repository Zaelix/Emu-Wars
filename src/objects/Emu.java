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
	Shield shield;

	Rectangle head;
	Rectangle body;
	Rectangle legs;

	int type = 0;
	long specialTimer;
	long specialCooldown = 5000;

	// Hitbox adjustment numbers
	int bhXMod = (getWidth() / 25);
	int legsXMod = (getWidth() / 3);
	int bodyYMod = (int) (getHeight() * 0.4);
	private double moveSpeedPercent = 1;
	
	public Emu(int x, int y, int width, int height, double speed, Color color,
			ArrayList<BufferedImage> emuAnim) {
		super(x, y, width, height, speed, color);
		this.emuAnim = emuAnim;
		double size = (2.0 - speed / 2.1);
		this.setWidth((int) (this.getWidth() * size));
		this.setHeight((int) (this.getHeight() * size));
		head = new Rectangle();
		body = new Rectangle();
		legs = new Rectangle();
		setAnimCooldown((long) (getAnimCooldown() * (3.2 - speed)));
		int healthMod = (int) ((GameManager.getSecondsSinceStart() / healthDifficultyDivisor) * size * size);
		this.maxHealth = Math.max(2.5 - speed, 1) + 1 + healthMod;
		this.health = maxHealth;
		bhXMod = (this.getWidth() / 25);
		legsXMod = (this.getWidth() / 3);
		bodyYMod = (int) (this.getHeight() * 0.4);
		updateCollisionBoxes();
	}

	public void setType(int type) {
		this.type = type;
		if (type == SHIELD) {
			shield = new Shield((int) (getX() + getWidth() / 3),
					(int) (getY() - getHeight() / 3), (int) (getWidth() * 1.5),
					(int) (getHeight() * 1.5), 0, Color.BLUE, this);
			GameManager.addShield(shield);
			setAnim(GamePanel.emuFloat);
		}
		if (type == MOTHER && speed > 0.4) {
			type = NORMAL;
			setAnim(GamePanel.emuFloat);
		} else if (type == MOTHER) {
			setAnim(GamePanel.emuSit);
			this.setWidth((int) (this.getWidth() * 1.5));
			this.setHeight((int) (this.getHeight() * 1.5));
			this.hpBar.setOffset(getWidth()/5);
			this.maxHealth *= 1.5;
			this.health = this.maxHealth;
		}
		else if (type == BOUNCER) {
			int healthMod = (int) (GameManager.getSecondsSinceStart() / healthDifficultyDivisor)*8;
			this.setWidth(500);
			this.setHeight(500);
			this.hpBar.setOffset(100);
			this.speed = 0.6;
			this.maxHealth = 200+healthMod;
			this.health = this.maxHealth;
			bhXMod = (this.getWidth() / 25);
			legsXMod = (this.getWidth() / 3);
			bodyYMod = (int) (this.getHeight() * 0.4);
			updateCollisionBoxes();
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
		if(moveSpeedPercent < 1){
			moveSpeedPercent += 0.001;
		}
	}

	void performSpecial() {
		if (System.currentTimeMillis() - specialTimer >= specialCooldown) {
			specialTimer = System.currentTimeMillis();
			if (type == MOTHER) {
				int xMod = GamePanel.gen.nextInt(100);
				int yMod = GamePanel.gen.nextInt(100);
				Emu e = new Emu((int) (getX()+getWidth()/2)+xMod, (int) (getY()+getHeight()/2)+yMod,
						(int) (getWidth() / 1.5), (int) (getHeight() / 1.5), speed * 1.5,
						Color.BLACK, GamePanel.emuRun);
				e.setWidth((int) (getWidth() / 1.5));
				e.setHeight((int) (getHeight() / 1.5));
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
		head.setBounds((int) getX() + (getWidth() / 25) - d * 3, (int) getY(),
				(int) (getWidth() / 2.5), (int) (getHeight() / 2.6));
		body.setBounds((int) getX() + (getWidth() / 25),
				(int) (getY() + (getHeight() * 0.4)), (int) (getWidth() * 0.95),
				(int) (getHeight() / 2.5));
		legs.setBounds((int) (getX() + getWidth() / 3),
				(int) (getY() + (getHeight() * 0.8)), (int) (getWidth() / 2.5),
				getHeight() / 5);
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
		if (o.getCollisionBox().intersects(head)) {
			takeDamage(o.getDamage() * Player.headshotMultiplier);
			return true;
		} else if (o.getCollisionBox().intersects(body)
				|| o.getCollisionBox().intersects(legs)) {
			takeDamage(o.getDamage());
			return true;
		}
		return false;
	}

	public void move() {
		setX(getX() - (speed*getMoveSpeedPercent()));
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
			GameManager.addJerky(new Jerky((int)getX(), (int)getY(), 40, 40));
			GameManager.explodeAt((int) getX() - getWidth() / 2, (int) getY()
					- getHeight() / 2, getWidth(), GamePanel.explosion);
			GameManager.incrementScore((int) (maxHealth));
		}
	}

	public void draw(Graphics g) {
		hpBar.draw(g);
		g.drawImage(emuAnim.get(getFrame()), (int) getX() - 10,
				(int) getY() - 15, getWidth() + 20, getHeight() + 25, null);
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

	public double getMoveSpeedPercent() {
		return moveSpeedPercent;
	}

	public void setMoveSpeedPercent(double moveSpeedPercent) {
		this.moveSpeedPercent = moveSpeedPercent;
	}
}
