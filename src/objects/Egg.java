package objects;

import java.awt.Color;
import java.awt.Graphics;

import core.GamePanel;

public class Egg extends GameObject {

	Egg(int x, int y, int width, int height) {
		super(x, y, width, height, 0, Color.WHITE);
		// TODO Auto-generated constructor stub
	}

	public void draw(Graphics g) {
		g.drawImage(GamePanel.egg, (int) getX(), (int) getY(), getWidth(), getHeight(), null);
		hpBar.draw(g);
		super.draw(g);
	}

	public void update() {
		super.update();
		hpBar.update();
	}

}
