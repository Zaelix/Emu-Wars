package objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.text.DecimalFormat;

import core.GameManager;

public class UpgradeButton {
	private boolean isHidden = false;
	public static int nextValidKey = 1;
	public static int nextValidY = 65;
	public boolean isDivider = false;
	private int key = -1;
	int x;
	int y;
	int width;
	int height;
	Color color = Color.GREEN;
	Font keyFont = new Font("AR ESSENCE", 10, 40);
	Font displayFont = new Font("AR ESSENCE", 10, 20);
	String text;
	private double value;
	int decimals;
	double cost = 1;
	private double costMult = 1;
	double valueMult = 0.1;
	int maxValue = Integer.MAX_VALUE;
	private int minValue = 0;
	boolean buysAnObject = false;
	boolean isPercentageBasedValue = false;
	boolean isPercentageBasedCost = false;
	String object;

	DecimalFormat myFormatter = new DecimalFormat("###.#");

	public UpgradeButton(String text, double value, double costMult) {
		setup(text, value, costMult);
	}

	public UpgradeButton(String text, double value, double costMult, double valueMult) {
		setup(text, value, costMult);
		this.valueMult = valueMult;
	}

	public UpgradeButton(String text, double value, double costMult, String object) {
		setup(text, value, costMult);
		this.buysAnObject = true;
		this.object = object;
		valueMult = 1;
	}

	public UpgradeButton(String text) {
		this.x = 10;
		this.width = 200;
		this.height = 10;
		calculateYPosition();
		if (text.equals("")) {
			this.height = 10;
			nextValidY += height + 5;
		} else {
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
		this.width = 150;
		this.height = 25;
		calculateYPosition();
		this.text = text;
		this.setValue(value);
		this.decimals = 2;
		this.setCostMult(costMult);
	}

	public void calculateYPosition() {
		this.y = nextValidY;
		if (!isDivider) {
			nextValidY += height * 2 + 5;
		} else {
			nextValidY += height + 5;
		}
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
				g.drawString(text, x + (width / 4), y + 28);
			}
		} else {
			String val = myFormatter.format(getValue());
			String costF = myFormatter.format(cost);
			int tHeight = (int) (y + (height / 1.3));
			g.setColor(Color.RED);
			g.fillRect(x, y + height, width, height); // Name box
			g.fillRect(x, y, width, height); // Cost Box
			g.fillRect(x + width, y, width / 3, height * 2); // Value Box

			g.setColor(color);
			g.fillRect(x + 1, y + 1, width - 2, height - 2); // Name Box
			g.fillRect(x + 1, y + height + 1, (width) - 2, height - 2); // Cost//
																		// Box
			g.fillRect(x + width + 1, y + 1, (width / 3) - 2, (height * 2) - 2); // Value
																					// Box
			int keyBoxWidth = (width / 5);
			g.setFont(displayFont);
			g.setColor(Color.BLACK);
			g.drawString(text, x + (width / 4), tHeight);
			if (!isHidden()) {
				g.drawString("Cost: " + costF, x + (width / 4), tHeight + height);
			} else {
				g.drawString("MAX LEVEL", x + (width / 4), tHeight + height);
			}
			g.drawString(val + "", x + width + 2, tHeight + (height / 2));

			// Key Box Display
			g.setFont(keyFont);
			g.setColor(Color.RED);
			g.fillRect(x, y, keyBoxWidth, height * 2);
			g.setColor(color);
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
		if (GameManager.getPoints() >= cost && !isDivider && value < maxValue && value > minValue) {
			GameManager.spendPoints(cost);
			if (buysAnObject) {
				GameManager.spawnObject(object);
			}
			if (isPercentageBasedValue) {
				setValue(getValue() + (getValue() * (valueMult / 100)));
			} else {
				setValue(getValue() + valueMult);
			}
			if (isPercentageBasedCost) {
				cost *= 1 + (getCostMult() / 100);
			} else {
				cost += 1 / getCostMult();
			}
		}
		if (value >= maxValue) {
			setHidden(true);
			color = Color.BLUE;
		}
		if (value <= minValue) {
			setHidden(true);
			color = Color.BLUE;
		}
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getText() {
		return this.text;
	}

	public UpgradeButton setMaxValue(int maxValue) {
		this.maxValue = maxValue;
		return this;
	}

	public UpgradeButton setPercentageBasedValue(boolean b) {
		isPercentageBasedValue = b;
		return this;
	}

	public UpgradeButton setPercentageBasedCost(boolean b) {
		isPercentageBasedCost = b;
		return this;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getCost() {
		return cost;
	}

	public UpgradeButton setCost(double cost) {
		this.cost = cost;
		return this;
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

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	int getMinValue() {
		return minValue;
	}

	public UpgradeButton setMinValue(int minValue) {
		this.minValue = minValue;
		return this;
	}

	double getCostMult() {
		return costMult;
	}

	public UpgradeButton setCostMult(double costMult) {
		this.costMult = costMult;
		return this;
	}

	public UpgradeButton setValueMult(int valueMult) {
		this.valueMult = valueMult;
		return this;
	}
}
