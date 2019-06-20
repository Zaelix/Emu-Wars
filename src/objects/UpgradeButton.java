package objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.text.DecimalFormat;

import core.GameManager;

public class UpgradeButton {
	public static int nextValidKey = 1;
	public static int nextValidY = 65;
	public boolean isDivider = false;
	private int key = -1;
	int x;
	int y;
	int width;
	int height;
	Font keyFont = new Font("AR ESSENCE", 10, 40);
	Font displayFont = new Font("AR ESSENCE", 10, 20);
	String text;
	private double value;
	int decimals;
	double cost = 1;
	double costMult = 1;
	double valueMult = 0.1;
	int maxValue = Integer.MAX_VALUE;
	boolean buysAnObject = false;
	String object;

	DecimalFormat myFormatter = new DecimalFormat("###.#");

	public UpgradeButton(String text, double value, double costMult) {
		setup(text, value, costMult);
	}

	public UpgradeButton(String text, double value, double costMult,
			double valueMult) {
		setup(text, value, costMult);
		this.valueMult = valueMult;
	}

	public UpgradeButton(String text, double value, double costMult,
			String object) {
		setup(text, value, costMult);
		this.buysAnObject = true;
		this.object = object;
	}

	public UpgradeButton(String text) {
		this.x = 10;
		this.y = nextValidY;
		this.width = 200;
		this.height = 10;
		nextValidY += height + 5;
		if (text.equals("")) {
			this.height = 10;
			nextValidY += height + 5;
		}
		else{
			this.height = 35;
			nextValidY += height + 5;
		}
		isDivider = true;
		this.text = text;
	}

	void setup(String text, double value, double costMult) {
		this.setKey(nextValidKey);
		nextValidKey++;
		this.x = 10;
		this.y = nextValidY;
		this.width = 150;
		this.height = 25;
		nextValidY += height * 2 + 5;
		this.text = text;
		this.setValue(value);
		this.decimals = 2;
		this.costMult = costMult;
	}

	public void update() {

	}

	public void draw(Graphics g) {
		if (isDivider) {
			if (text.equals("")) {
				g.setColor(Color.BLACK);
				g.fillRect(x, y, width, height); // Name box
			} else {
				g.setFont(displayFont);
				g.setColor(Color.BLACK);
				g.fillRect(x, y, width, height); // Name box
				g.setColor(Color.RED);
				g.drawString(text, x+ (width / 4), y+28);
			}
		} else {
			String val = myFormatter.format(getValue());
			String costF = myFormatter.format(cost);
			int tHeight = (int) (y + (height / 1.3));
			g.setColor(Color.RED);
			g.fillRect(x, y + height, width, height); // Name box
			g.fillRect(x, y, width, height); // Cost Box
			g.fillRect(x + width, y, width / 3, height * 2); // Value Box

			g.setColor(Color.GREEN);
			g.fillRect(x + 1, y + 1, width - 2, height - 2); // Name Box
			g.fillRect(x + 1, y + height + 1, (width) - 2, height - 2); // Cost Box
			g.fillRect(x + width + 1, y + 1, (width / 3) - 2, (height * 2) - 2); // Value Box
			int keyBoxWidth = (width / 5);
			g.setFont(displayFont);
			g.setColor(Color.BLACK);
			g.drawString(text, x + (width / 4), tHeight);
			g.drawString("Cost: " + costF, x + (width / 4), tHeight + height);
			g.drawString(val + "", x + width + 2, tHeight + (height / 2));

			// Key Box Display
			g.setFont(keyFont);
			g.setColor(Color.RED);
			g.fillRect(x, y, keyBoxWidth, height * 2);
			g.setColor(Color.GREEN);
			g.fillRect(x + 1, y + 1, keyBoxWidth - 2, height * 2 - 2);
			g.setColor(Color.RED);
			if (getKey() < 10) {
				g.drawString(getKey() + "", x + 3, tHeight + 20);
			}
		}

		if (GameObject.debugRenderMode == 1) {
			// g.setColor(Color.MAGENTA);
			// g.fillRect(x, y, 200, height);
		}
	}

	public void buy() {
		if (GameManager.getPoints() >= cost && !isDivider && value < maxValue) {
			GameManager.spendPoints(cost);
			if (buysAnObject) {
				GameManager.spawnObject(object);
				cost /= costMult;
				setValue(getValue() + 1);
			} else {
				cost += 1 / costMult;
				setValue(getValue() + valueMult);
			}
		}
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public void setMaxRank(int maxValue) {
		this.maxValue = maxValue;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
