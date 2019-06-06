package objects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import core.GamePanel;

public class Projectile extends GameObject {
	private double damage = 1;

	ArrayList<Point> trail = new ArrayList<Point>();
	int trailSize = 10;

	public Projectile(int x, int y, int width, int height, double speed, Color color, Point from, Point to, double damage){
		super(x,y,width,height,speed,color);

		setup(x,y,width, height, speed, color, from, to, damage);
		
	}
	
	Projectile(int x, int y, int width, int height, double speed, Color color, Point from, Point to, double damage, int trailSize){
		super(x,y,width,height,speed,color);

		setup(x,y,width, height, speed, color, from, to, damage);
		this.trailSize = trailSize;
		
	}

	void setup(int x, int y, int width, int height, double speed, Color color,
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
		int x = (int) this.getX();
		int y = (int) this.getY();
		g.setColor(color);
		g.fillOval(x, y, width, height);
		if(trailSize > 0){
			drawParticles(g, x, y);
		}
	}

	void drawParticles(Graphics g, int x, int y) {
		int dx = GamePanel.gen.nextInt(7);
		int dy = GamePanel.gen.nextInt(7);
		trail.add(new Point((int) x + dx, (int) y + dy));

		for (Point p : trail) {
			int r = GamePanel.gen.nextInt(80) + 170;
			int gb = GamePanel.gen.nextInt(40);
			g.setColor(new Color(r, gb, gb));
			g.fillOval(p.x, p.y, 2, 2);
		}

		while (trail.size() > trailSize) {
			trail.remove(0);
		}
	}

	public void update() {
		super.update();
		setX(getX() + vx);
		setY(getY() + vy);

	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}
}
