package objects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import core.GamePanel;

public class Projectile extends GameObject {
	private double damage = 1;
	

	public Projectile(int x, int y, double speed, Color color, Point from, Point to, double damage){
		super(x,y,(int) (20+speed), (int) (15-(speed/2)),speed,color);

		setup(x,y, speed, color, from, to, damage);
		
	}

	void setup(int x, int y, double speed, Color color,
			Point from, Point to, double damage) {
		double slopeX = from.x - (to.x - 10);
		double slopeY = from.y - (to.y - 32);

		double magn = Math.sqrt(Math.pow((slopeX), 2) + Math.pow((slopeY), 2));
		vx = -slopeX / magn * speed;
		vy = -slopeY / magn * speed;
		
		this.setDamage(damage);
	}

	public void draw(Graphics g) {
		super.draw(g);
		if(vy == 0){
			g.drawImage(GamePanel.fireball.get(getFrame()), (int) getX(), (int) getY(), width, height,	null);
		}
		else{
			Graphics2D g2d = (Graphics2D) g.create();
			double rads = Math.toRadians((vy/speed)*90);
			g2d.rotate(rads, (int) getX()+width/2, (int) getY()+height/2);
			g2d.drawImage(GamePanel.fireball.get(getFrame()), (int) getX(), (int) getY(), width, height,	null);
			g2d.rotate(-rads, (int) getX()+width/2, (int) getY()+height/2);
			g2d.dispose();
		}
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
