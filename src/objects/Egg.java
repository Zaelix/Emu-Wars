package objects;

import java.awt.Color;
import java.awt.Graphics;

import core.GameManager;
import core.GamePanel;

public class Egg extends GameObject {
	private int emuType;
	int secondsUntilHatch = 10;
	long hatchTimer;
	long hatchDelay = 1000;

	Egg(int x, int y, int width, int height, int speed, int hp, int emuType) {
		super(x, y, width, height, 0, Color.WHITE);
		this.health = hp;
		this.maxHealth = hp;
		this.emuType = emuType;
		this.speed = speed;
		hatchTimer = System.currentTimeMillis();
	}

	public void draw(Graphics g) {
		g.drawImage(GamePanel.egg, (int) getX(), (int) getY(), getWidth(), getHeight(), null);
		g.drawString(secondsUntilHatch+"s", (int)(getX()+getWidth()/2)-15, (int)(getY()+getHeight()/2)+10);
		hpBar.draw(g);
		super.draw(g);
	}

	public void update() {
		super.update();
		hpBar.update();
		if (System.currentTimeMillis() - hatchTimer >= hatchDelay) {
			secondsUntilHatch--;
			hatchTimer = System.currentTimeMillis();
		}
		if(secondsUntilHatch <= 0){
			hatch();
		}
	}

	void hatch() {
		Emu e = new Emu((int) getX(), (int) getY(), (int) (getWidth() << 2), (int) (getHeight() << 2), speed, Color.BLACK,
				GamePanel.emuRun);
		e.setWidth((int) (getWidth() << 2));
		e.setHeight((int) (getHeight() << 2));
		e.setType(emuType);
		GameManager.addEmu(e);
		setAlive(false);
	}

	public void takeDamage(double damage) {
		health -= damage;
		if (health <= 0) {
			setAlive(false);
			GameManager.addJerky(new Jerky((int) getX(), (int) getY(), 40, 40));
			GameManager.explodeAt((int) getX() - getWidth() / 2, (int) getY() - getHeight() / 2, getWidth(), GamePanel.explosion);
			GameManager.incrementScore((int) (maxHealth * 2));
		}
	}

}
