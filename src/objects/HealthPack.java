package objects;

import java.awt.Graphics;
import java.awt.Point;

import core.GameManager;
import core.GamePanel;

public class HealthPack extends Jerky {
	int hp;
	public HealthPack(int x, int y, int width, int height, int hp) {
		super(x, y, width, height);
		this.hp = hp;
		deathX = 130;
		setup(new Point(x, y), new Point(145, 35));
		// TODO Auto-generated constructor stub
	}
	public void draw(Graphics g) {
		g.drawImage(GamePanel.healthPack, (int) getX(), (int) getY(), getWidth(), getHeight(),
				null);
	}
	
	public void update(){
		super.update();
	}
	
	public void die(){
		GameManager.getPlayer().heal(hp);
		setAlive(false);
	}
	
}
