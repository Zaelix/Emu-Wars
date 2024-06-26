package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.GameManager;
import core.GamePanel;

public class Grenade extends GameObject{
	long startTime = System.currentTimeMillis();
	long fuseLength = 2000;
	public static int diameter = 100;
	public static double damage = 1;
	public static double maxDamage = 5;
	public int maxTargets = 8;
	
	double angle = 0;
	Grenade(int x, int y, double speed, Point from, Point to) {
		super(x, y, 30, 30, speed, Color.RED);
		setup(from, to);
	}
	
	void setup(Point from, Point to){
		double slopeX = from.x - (to.x - GameManager.mouseXOffset);
		double slopeY = from.y - (to.y - GameManager.mouseYOffset);

		double magn = Math.sqrt(Math.pow((slopeX), 2) + Math.pow((slopeY), 2));
		speed *= magn/165;
		vx = -slopeX / magn * speed;
		vy = -slopeY / magn * speed;
	}

	public void update(){
		super.update();
		setX(getX() + vx);
		setY(getY() + vy);
		vx *= 0.97;
		vy *= 0.97;
		if (System.currentTimeMillis() - startTime >= fuseLength) {
			detonate();
		}
	}
	
	public void draw(Graphics g){
		//g.drawImage(GamePanel.grenade, (int)getX(), (int)getY(), width, height, null);
		Graphics2D g2d = (Graphics2D) g.create();
		double rads = Math.toRadians(angle);
		g2d.rotate(rads, (int) getX()+getWidth()/2, (int) getY()+getHeight()/2);
		g2d.drawImage(GamePanel.grenade, (int)getX(), (int)getY(), getWidth(), getHeight(), null);
		g2d.rotate(-rads, (int) getX()+getWidth()/2, (int) getY()+getHeight()/2);
		g2d.dispose();
		angle+=vx+vy;
		super.draw(g);
	}
	
	void detonate(){
		setAlive(false);
		
		GameManager.explodeAt((int)getX()-diameter+20, (int)getY()-diameter+20, diameter, GamePanel.explosion);
		GameManager.damageArea((int)getX(), (int)getY(), diameter, damage, damage*maxDamage, maxTargets);
	}
}
