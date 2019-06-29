package objects;
import java.awt.Color;
import java.awt.Graphics;

import core.GameManager;
import core.GamePanel;

public class Shield extends GameObject {
	GameObject parent;
	HealthBar hpBar;
	double percent;
	double regenRate = 1.002;
	int type = 0;

	Shield(int x, int y, int width, int height, double speed, Color color,
			GameObject parent) {
		super(x, y, width, height, speed, color);
		this.parent = parent;
		hpBar = new HealthBar(this, 70, 15);
		hpBar.setOffset(width/3);
		this.health = parent.maxHealth * 2;
		this.maxHealth = parent.maxHealth * 4;
		this.percent = 1;
	}

	void draw(Graphics g) {
		if (isAlive()) {
			g.drawImage(GamePanel.shield, (int) getX(), (int) getY(), getWidth(), getHeight(), null);
			hpBar.draw(g);
		}
		super.draw(g);
	}

	void update() {
		super.update();
		if (isAlive()) {
			hpBar.update();
			hpBar.setOffset((getWidth()/2)-25);
		}
		this.setX(parent.getX() + (parent.getWidth() / 2) - (getWidth()/2));
		this.setY(parent.getY() + (parent.getHeight() / 2) - (getHeight()/2));
		percent = health / maxHealth;
		if(percent > 0.01){
			setWidth((int) ((parent.getWidth()*3)*percent));
			setHeight((int) ((parent.getWidth()*3)*percent));
		}
		else{
			setWidth(5);
			setHeight(5);
		}
		if (isAlive() && health < maxHealth && type == 0) {
			health *= regenRate;
		}
	}

	public void takeDamage(double damage) {
		health -= damage;
		if (health <= 0) {
			setAlive(false);
		}
	}

	public boolean collidesWith(Projectile p) {
		double distance = GameManager.dist(getCenterX(), getCenterY(), p.getCenterX(),
				p.getCenterY());
		if (isAlive() && distance < getWidth() / 2) {
			return true;
		}
		return false;
	}
}
