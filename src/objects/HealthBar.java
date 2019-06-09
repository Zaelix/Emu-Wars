package objects;
import java.awt.Color;
import java.awt.Graphics;

public class HealthBar {
	GameObject owner;
	double x;
	double y;
	int xOffset = 0;
	int width;
	int height;

	double percent;
	Color color = Color.RED;

	HealthBar(GameObject owner, int width, int height) {
		this.owner = owner;
		this.x = owner.getX();
		this.y = owner.getY() - height - 5;
		this.width = width;
		this.height = height;
		percent = owner.health / owner.maxHealth;
	}

	void update() {
		this.x = owner.getX() + xOffset;
		this.y = owner.getY() - height - 5;
		percent = Math.max(owner.health / owner.maxHealth, 0);
		if(owner instanceof Shield){
			this.color = Color.BLUE;
		}
		else {
			updateColor();
		}
	}
	
	void updateColor(){
		Color c = new Color((int)constrain(255-(255*percent), 255, 0),  (int)constrain(255*percent, 255, 0), 0);
		this.color = c;
	}
	
	double constrain(double x, int max, int min){
		return Math.max(Math.min(x, max), min);
	}
	
	void setColor(Color c){
		this.color = c;
	}
	
	public void setOffset(int offset){
		xOffset = offset;
	}

	void draw(Graphics g) {
		if (percent < 0.99) {
			g.setColor(Color.GRAY);
			g.fillRect((int) x, (int) y, width, height);

			g.setColor(color);
			g.fillRect((int) (x + 1), (int) (y + 1),
					(int) ((width - 2) * percent), height - 2);
			if(GameObject.debugRenderMode == 1){
				g.drawString(Math.ceil(owner.health) + "/" + Math.ceil(owner.maxHealth), (int)x, (int)y);
			}
		}
	}
}
