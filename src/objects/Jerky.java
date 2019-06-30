package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import core.GameManager;
import core.GamePanel;

public class Jerky extends GameObject {
	int age = 0;
	int deathX = 80;
	int deathY = 160;
	double slowXMultAged = 0.95;
	double slowXMultFresh = 0.995;
	double slowYMultAged = 0.97;
	double slowYMultFresh = 0.97;

	Jerky(int x, int y, int width, int height) {
		super(x, y, width, height, 3, Color.RED);
		setup(new Point(x, y), new Point(65, 35));
	}

	void setup(Point from, Point to) {
		double slopeX = from.x - (to.x - GameManager.mouseXOffset);
		double slopeY = from.y - (to.y - GameManager.mouseYOffset);

		double magn = Math.sqrt(Math.pow((slopeX), 2) + Math.pow((slopeY), 2));
		speed = 3*magn / 165;
		vx = -slopeX / magn * speed;
		vy = -slopeY / magn * speed*2;
	}

	public void draw(Graphics g) {
		g.drawImage(GamePanel.jerky, (int) getX(), (int) getY(), getWidth(), getHeight(),
				null);
	}

	public void slow() {
		if (age > 150) {
			vx *= slowXMultAged;
			vy *= slowYMultAged;
		} else {
			vx *= slowXMultFresh;
			vy *= slowYMultFresh;
		}
	}

	public void update() {
		super.update();
		setX(getX() + vx);
		setY(getY() + vy);
		slow();
		age++;
		if (isAlive() && getX() < deathX && getY() < deathY) {
			die();
		}
	}
	
	public void die(){
		setAlive(false);
	}

}
