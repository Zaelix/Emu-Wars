package objects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import core.GameManager;
import core.GamePanel;

public class Projectile extends GameObject {
	private double damage = 1;
	Point from;
	Point to;

	public Projectile(int x, int y, double speed, Color color, Point from, Point to, double damage){
		super(x,y,(int) (20+speed), (int) (15-(speed/3)),speed,color);

		setup(x,y, speed, color, from, to, damage);
		
	}

	void setup(int x, int y, double speed, Color color,
			Point from, Point to, double damage) {
		this.from= from;
		this.to = to;
		double slopeX = from.x - (to.x - GameManager.mouseXOffset);
		double slopeY = from.y - (to.y - GameManager.mouseYOffset);

		double magn = Math.sqrt(Math.pow((slopeX), 2) + Math.pow((slopeY), 2));
		vx = -slopeX / magn * speed;
		vy = -slopeY / magn * speed;
		
		this.setDamage(damage);
	}

	public void draw(Graphics g) {
		super.draw(g);
		if(vy == 0){
			g.drawImage(GamePanel.fireball.get(getFrame()), (int) getX(), (int) getY(), getWidth(), getHeight(),	null);
		}
		else{
			Graphics2D g2d = (Graphics2D) g.create();
			double rads = Math.toRadians((vy/speed)*90);
			g2d.rotate(rads, (int) getX()+getWidth()/2, (int) getY()+getHeight()/2);
			g2d.drawImage(GamePanel.fireball.get(getFrame()), (int) getX(), (int) getY(), getWidth(), getHeight(),	null);
			g2d.rotate(-rads, (int) getX()+getWidth()/2, (int) getY()+getHeight()/2);
			g2d.dispose();
		}
		//drawLineToTarget(g);
	}
	
	void drawLineToTarget(Graphics g) {
		g.setColor(Color.MAGENTA);
		g.drawLine((int) from.getX(), (int) from.getY(), to.x - GameManager.mouseXOffset,
				to.y - GameManager.mouseYOffset);
	}

	public void update() {
		super.update();
		setX(getX() + vx);
		setY(getY() + vy);
		animate();

	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}
}
