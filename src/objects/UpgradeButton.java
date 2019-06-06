package objects;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DecimalFormat;

import core.GameManager;

public class UpgradeButton {
	public static int nextValidKey = 1;
	public static int nextValidY = 65;
	boolean isDivider = false;
	private int key = -1;
	int x;
	int y;
	int width;
	int height;
	Font keyFont = new Font("AR ESSENCE", 10, 20);
	String text;
	private double value;
	int decimals;
	double cost = 1;
	double multiplier = 1;
	int maxValue = Integer.MAX_VALUE;
	boolean buysAnObject = false;
	String object;

	DecimalFormat myFormatter = new DecimalFormat("###.#");

	public UpgradeButton(int x, int width, int height, String text,
			double value, int decimals, double multiplier) {
		setup(x, width, height, text, value, decimals, multiplier);
	}

	public UpgradeButton(int x, int width, int height, String text,
			double value, int decimals, double multiplier, String object) {
		setup(x, width, height, text, value, decimals, multiplier);
		this.buysAnObject = true;
		this.object = object;
	}

	public UpgradeButton() {
		this.x = 10;
		this.y = nextValidY;
		this.width = 200;
		this.height = 10;
		nextValidY += height + 5;
		isDivider = true;
	}

	void setup(int x, int width, int height, String text,
			double value, int decimals, double multiplier) {
		this.setKey(nextValidKey);
		nextValidKey++;
		this.x = x;
		this.y = nextValidY;
		this.width = width;
		this.height = height;
		nextValidY += height * 2 + 5;
		this.text = text;
		this.setValue(value);
		this.decimals = decimals;
		this.multiplier = multiplier;
	}

	public void update() {

	}

	public void draw(Graphics g) {
		if (isDivider) {
			g.setColor(Color.BLACK);
			g.fillRect(x, y, width, height); // Name box
		} else {
			String val = myFormatter.format(getValue());
			String costF = myFormatter.format(cost);
			int tHeight = (int) (y + (height / 1.3));
			g.setColor(Color.GRAY);
			g.fillRect(x, y + height, width, height); // Name box
			g.fillRect(x, y, width, height); // Cost Box
			g.fillRect(x + width, y, width / 3, height * 2); // Value Box

			g.setColor(Color.GREEN);
			g.fillRect(x + 1, y + 1, width - 2, height - 2); // Name Box
			g.fillRect(x + 1, y + height + 1, (width) - 2, height - 2); // Cost
																		// Box
			g.fillRect(x + width + 1, y + 1, (width / 3) - 2, (height * 2) - 2); // Value
																					// Box

			g.setColor(Color.BLACK);
			g.setFont(keyFont);
			g.drawString(text, x + (width / 8), tHeight);
			g.drawString("Cost: " + costF, x + 3, tHeight + height);
			g.drawString(val + "", x + width + 2, tHeight + (height / 2));
			
			g.setColor(Color.RED);
			g.drawString(getKey() + "", x + 3, tHeight);
		}
	}

	public void buy() {
		if (GameManager.getPoints() >= cost && !isDivider && value < maxValue) {
			GameManager.spendPoints(cost);
			if (buysAnObject) {
				GameManager.spawnObject(object);
				cost /= multiplier;
				setValue(getValue() + 1);
			} else {
				cost += 1 / multiplier;
				setValue(getValue() + 0.1);
			}
		}
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
	
	public void setMaxRank(int maxValue){
		this.maxValue = maxValue;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
