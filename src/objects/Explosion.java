package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.GamePanel;

public class Explosion extends GameObject {

	private ArrayList<BufferedImage> anim;

	public Explosion(int x, int y, int width, int height) {
		super(x, y, width, height, 0, Color.WHITE);
		anim = GamePanel.explosion;
	}

	public void update() {
		if (isActive) {
			animateOnce(GamePanel.explosion.size());
		}
		else{
			setPosition(-1000,-1000);
		}
	}

	public void draw(Graphics g) {
		if(getFrame()<GamePanel.explosion.size()){
			g.drawImage(anim.get(getFrame()), (int) getX() - 10, (int) getY() - 15, width, height, null);
		}
		super.draw(g);
	}

	public void setPosition(int x, int y){
		setX(x);
		setY(y);
	}
	
	public void setSize(int width, int height){
		this.width = width;
		this.height = height;
	}
	
	public void setSize(int size){
		this.width = size;
		this.height = size;
	}

	public void setAnim(ArrayList<BufferedImage> anim){
		this.anim = anim;
	}

}
