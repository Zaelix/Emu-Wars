package core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

import javax.imageio.ImageIO;

import objects.Emu;
import objects.Explosion;
import objects.GameObject;
import objects.Grenade;
import objects.Player;
import objects.Projectile;
import objects.Shield;
import objects.Tower;
import objects.UpgradeButton;

public class GameManager {
	static boolean isPaused = false;
	public static int currentState = 0;
	public static final int MENU_STATE = 0;
	public static final int GAME_STATE = 1;
	public static final int END_STATE = 2;
	public static double frameRate = 1;

	static int playerHealth = 10;
	public static Rectangle base = new Rectangle(0, 0, 210, EmuCore.HEIGHT);
	static double points = 10;
	static int score = 0;
	static Point mouseLoc = new Point();
	static Point clicked = new Point();

	static ArrayList<Projectile> bullets = new ArrayList<Projectile>();
	static ArrayList<Emu> emus = new ArrayList<Emu>();
	static ArrayList<UpgradeButton> buttons = new ArrayList<UpgradeButton>();
	static ArrayList<Tower> towers = new ArrayList<Tower>();
	static ArrayList<Shield> shields = new ArrayList<Shield>();
	static ArrayList<Grenade> grenades = new ArrayList<Grenade>();
	static ArrayList<Explosion> explosionPool = new ArrayList<Explosion>();

	static Player player = new Player(250, 450, 50, 50, Color.BLUE);
	static Emu menuEmu = new Emu(EmuCore.WIDTH/2, 200, 200, 300, 0, Color.BLUE,
			GamePanel.emuSit);
	static Rectangle menuSelection = new Rectangle(100,
			(int) (EmuCore.HEIGHT * 0.3));
	static int difficulty = 0;
	long lastFrameTime;
	long spawnTimer;
	static long spawnCooldown = 5000;
	static double spawnChangeRate = 0.9985; // 0.9996 is the original value
	public static long frameCount = 0;

	static long pausedStart;
	static long pausedEnd;
	static long timeAtStart;
	static long timeAtEnd;

	Font endFont = new Font("AR DESTINE", Font.BOLD, 50);
	Font pausedFont = new Font("AR DESTINE", Font.BOLD, 430);

	DecimalFormat myFormatter = new DecimalFormat("###.#");

	int pausedRed;
	int pausedBlue;
	int pausedGreen;

	static int specialsSpawnChance = 0;
	static int specialsSpawnChanceGrowthrate = 40;
	static int specialsSpawnChanceMax = 50;

	GameManager() {
		timeAtStart = System.currentTimeMillis();
		menuSelection.x = 75;
		menuSelection.y = (int) (EmuCore.HEIGHT * 0.23);

		for (int i = 0; i < 20; i++) {
			explosionPool.add(new Explosion(-1000, -1000, 150, 150));
		}
	}

	void draw(Graphics g) {
		if (currentState == MENU_STATE) {
			drawMenuState(g);
		} else if (currentState == GAME_STATE) {
			drawGameState(g);
		} else if (currentState == END_STATE) {
			drawEndState(g);
		}
	}

	void drawMenuState(Graphics g) {
		g.setFont(endFont);
		g.setColor(Color.BLACK);
		g.drawString("CHOOSE DIFFICULTY: ", 75, (int) (EmuCore.HEIGHT * 0.2));
		g.drawString("EASY", 150, (int) (EmuCore.HEIGHT * 0.3));
		g.drawString("MEDIUM ", 150, (int) (EmuCore.HEIGHT * 0.4));
		g.drawString("HARD ", 150, (int) (EmuCore.HEIGHT * 0.5));

		g.setColor(Color.RED);
		g.drawString("1", 100, (int) (EmuCore.HEIGHT * 0.3));
		g.drawString("2 ", 100, (int) (EmuCore.HEIGHT * 0.4));
		g.drawString("3 ", 100, (int) (EmuCore.HEIGHT * 0.5));
		menuEmu.draw(g);
		menuSelection.y = (int) (EmuCore.HEIGHT * (0.23 + (0.1 * difficulty)));
		g.drawRoundRect(menuSelection.x, menuSelection.y, 400, 100, 50, 50);

		g.drawString("PRESS ENTER TO START", 75, (int) (EmuCore.HEIGHT * 0.6));
	}

	void drawGameState(Graphics g) {
		g.setFont(endFont);
		if (isPaused) {
			g.setFont(pausedFont);
			pausedRed = Math.max(
					Math.min(pausedRed + GamePanel.gen.nextInt(50) - 25, 255),
					0);
			pausedGreen = Math
					.max(Math.min(pausedGreen + GamePanel.gen.nextInt(50) - 25,
							255), 0);
			pausedBlue = Math.max(
					Math.min(pausedBlue + GamePanel.gen.nextInt(50) - 25, 255),
					0);
			g.setColor(new Color(pausedRed, pausedGreen, pausedBlue));
			g.drawString("PAUSED", 0, (int) (EmuCore.HEIGHT * 0.3));
			g.drawString("PAUSED", 0, (int) (EmuCore.HEIGHT * 0.65));
			g.drawString("PAUSED", 0, (int) (EmuCore.HEIGHT * 1));
		} else {
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, 220, EmuCore.HEIGHT);
			player.draw(g);
			drawLine(g);
			for (Projectile p : bullets) {
				p.draw(g);
			}
			for (Emu e : emus) {
				e.draw(g);
			}
			for (Tower t : towers) {
				t.draw(g);
			}

			for (int i = 0; i < buttons.size(); i++) {
				buttons.get(i).draw(g);
			}

			for (int i = 0; i < explosionPool.size(); i++) {
				explosionPool.get(i).draw(g);
			}
			
			for (int i = 0; i < grenades.size(); i++) {
				grenades.get(i).draw(g);
			}
			// drawLineToCursor(g);
			drawPointsBox(g);
			drawHealthBox(g);
			g.drawString(getSecondsSinceStart() + "", EmuCore.WIDTH / 2, 40);
			g.drawString(spawnCooldown + "", (int) (EmuCore.WIDTH * 0.8), 40);
		}
	}

	void drawEndState(Graphics g) {
		g.setColor(Color.BLACK);
		g.setFont(endFont);
		g.drawString("GAME OVER", (int) (EmuCore.WIDTH / 2.5),
				(int) (EmuCore.HEIGHT / 2.3));

		g.drawString("Score: " + score, (int) (EmuCore.WIDTH / 2.5),
				(int) (EmuCore.HEIGHT / 2.1));
		g.drawString("Time: " + timeAtEnd, (int) (EmuCore.WIDTH / 2.5),
				(int) (EmuCore.HEIGHT / 1.92));
		menuEmu.draw(g);
	}

	void drawHealthBox(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(130, 10, 80, 49);
		g.setColor(new Color(40, 240, 40));
		g.fillRect(131, 11, 78, 47);

		g.setColor(Color.BLACK);
		g.drawString("Hp: ", 133, 40);
		g.drawString((int) playerHealth + "",
				180 - (playerHealth + "").length() * 4, 40);
	}

	void drawPointsBox(Graphics g) {
		String pts = myFormatter.format(points);
		g.setColor(Color.BLACK);
		g.fillRect(10, 10, 120, 49);
		g.setColor(new Color(40, 240, 40));
		g.fillRect(11, 11, 118, 47);

		g.setColor(Color.BLACK);
		g.drawString("Points: ", 12, 40);
		g.drawString(pts, 90 - pts.length() * 4, 40);
	}

	void update() {
		mouseLoc = getMouseLocation();
		if (isPaused == false) {
			if (currentState == MENU_STATE) {
				updateMenuState();
			} else if (currentState == GAME_STATE) {
				updateGameState();
			} else if (currentState == END_STATE) {
				updateEndState();
			}
		}
	}

	void updateMenuState() {
		menuEmu.update();
	}

	void updateGameState() {
		frameRate = (1000.0 / (System.currentTimeMillis() - lastFrameTime));
		String fps = myFormatter.format(frameRate);
		// System.out.println(fps);
		lastFrameTime = System.currentTimeMillis();
		player.update();
		// fire();
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).update();
		}
		for (int i = 0; i < emus.size(); i++) {
			emus.get(i).update();
		}
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).update();
		}
		for (int i = 0; i < towers.size(); i++) {
			towers.get(i).update();
		}
		for (int i = 0; i < explosionPool.size(); i++) {
			explosionPool.get(i).update();
		}
		for (int i = 0; i < grenades.size(); i++) {
			grenades.get(i).update();
		}
		spawnEmus();
		checkCollisions();
		purgeObjects();

		// Increasing spawn rates
		if (frameCount % 10 == 0 && spawnCooldown > 10) {
			spawnCooldown *= spawnChangeRate;
		}
		else if(spawnCooldown<10 && frameCount % 10 == 0){
			Emu.healthDifficultyDivisor-=0.01;
		}
		if (frameCount % 2000 == 0 && spawnChangeRate < 0.9999) {
			spawnChangeRate += 0.0001;
		}
		frameCount++;
		updateStats();

		specialsSpawnChance = Math.min(getSecondsSinceStart()
				/ specialsSpawnChanceGrowthrate, specialsSpawnChanceMax);
	}

	void updateEndState() {
		menuEmu.update();
	}

	void updateStats() {
		player.setFireCooldown((long) (600 / buttons.get(0).getValue()));
		player.setBulletSpeed(2 + buttons.get(1).getValue());
		player.setDamage(buttons.get(2).getValue());
		player.setSpeed(buttons.get(3).getValue());

		Tower.setFireCooldown((long) (5000 / buttons.get(6).getValue()));
		Tower.setBulletSpeed(1 + buttons.get(7).getValue());
		Tower.setDamage(buttons.get(8).getValue());
	}

	public static int getSecondsSinceStart() {
		return (int) ((System.currentTimeMillis() - timeAtStart) / 1000);
	}

	void drawLineToCursor(Graphics g) {
		g.setColor(Color.RED);
		g.drawLine((int) player.getX(), (int) player.getY(), clicked.x - 10,
				clicked.y - 32);
	}

	void drawLine(Graphics g) {
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		Point frame = EmuCore.frame.getLocation();
		clicked = mouseLoc;
		g.setColor(Color.RED);
		g.drawLine((int) player.getCenterX(), (int) player.getCenterY(),
				clicked.x - 10, clicked.y - 32);

	}

	public Point getMouseLocation() {
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		Point frame = EmuCore.frame.getLocation();
		return new Point((int) (mouse.getX() - frame.getX()),
				(int) (mouse.getY() - frame.getY()));
	}

	public boolean mouseIntersects(Rectangle box) {
		if (mouseLoc.getX() > box.getX()
				&& mouseLoc.getX() < box.getX() + box.getWidth()
				&& mouseLoc.getY() > box.getY()
				&& mouseLoc.getY() < box.getY() + box.getHeight()) {
			return true;
		}
		return false;
	}

	public static float dist(double x, double y, double x2, double y2) {
		return (float) Math.sqrt(Math.pow((x - x2), 2) + Math.pow((y - y2), 2));
	}

	void spawnEmus() {
		if (System.currentTimeMillis() - spawnTimer >= spawnCooldown
				&& GamePanel.runAnims.size() > 0) {
			spawnEmu(GamePanel.runAnims.get(0));
			GamePanel.runAnims.remove(0);
		} else if (System.currentTimeMillis() - spawnTimer >= spawnCooldown
				&& GamePanel.runAnims.size() == 0) {
			spawnEmu(GamePanel.emuRun);
		}
	}

	void spawnEmu(ArrayList<BufferedImage> anim) {
		spawnTimer = System.currentTimeMillis();
		Emu e = new Emu(EmuCore.WIDTH + 50, new Random().nextInt(750) + 100,
				108, 128, (GamePanel.gen.nextDouble() * 2.0) + 0.1,
				Color.BLACK, anim);

		if (GamePanel.gen.nextInt(100) < specialsSpawnChance) {
			int type = GamePanel.gen.nextInt(3);
			e.setType(type);
		}
		addEmu(e);

	}

	static void createButtons() {
		buttons.clear();
		buttons.add(new UpgradeButton(10, 150, 25, "Fire Rate", 1, 2, 1));
		buttons.add(new UpgradeButton(10, 150, 25, "Bullet Speed", 1, 2, 1.4));
		buttons.add(new UpgradeButton(10, 150, 25, "Bullet Damage", 1, 2, 1));
		buttons.add(new UpgradeButton(10, 150, 25, "Move Speed", 1, 2, 2.3));
		buttons.add(new UpgradeButton());
		buttons.add(new UpgradeButton(10, 150, 25, "Buy Tower", 1, 2, 0.35,
				"Tower"));
		buttons.add(new UpgradeButton(10, 150, 25, "T.Fire Rate", 1, 2, 0.2));
		buttons.add(new UpgradeButton(10, 150, 25, "T.Bullet Speed", 1, 2, 0.2));
		buttons.add(new UpgradeButton(10, 150, 25, "T.Bullet Dmg", 1, 2, 0.2));
	}

	void checkCollisions() {
		for (Projectile p : bullets) {
			for (Emu e : emus) {
				if (e.collidesWith(p)) {
					p.setAlive(false);
				}
			}
			for (Shield s : shields) {
				if (s.collidesWith(p)) {
					s.takeDamage(p.getDamage());
					p.setAlive(false);
				}
			}
		}
	}

	void purgeObjects() {
		for (int i = bullets.size() - 1; i >= 0; i--) {
			if (bullets.get(i).isAlive() == false) {
				bullets.remove(i);
			}
		}
		for (int i = emus.size() - 1; i >= 0; i--) {
			if (emus.get(i).isAlive() == false) {
				emus.remove(i);
			}
		}
		for (int i = shields.size() - 1; i >= 0; i--) {
			if (shields.get(i).isAlive() == false) {
				shields.remove(i);
			}
		}
		for (int i = grenades.size() - 1; i >= 0; i--) {
			if (grenades.get(i).isAlive() == false) {
				grenades.remove(i);
			}
		}
	}

	public static void togglePaused() {
		isPaused = !isPaused;
		if (isPaused) {
			pausedStart = System.currentTimeMillis();
		} else {
			pausedEnd = System.currentTimeMillis();
			timeAtStart += pausedEnd - pausedStart;
		}
	}

	public static void addBullet(Projectile p) {
		bullets.add(p);
	}

	public static void addEmu(Emu e) {
		emus.add(e);
	}

	public static void addShield(Shield s) {
		shields.add(s);
	}
	
	public static void addGrenade(Grenade g){
		grenades.add(g);
	}

	public static void incrementScore(int value) {
		score += value;
		points += value;
	}

	public static double getPoints() {
		return points;
	}

	public static void gainPoints(int value) {
		points += value;
	}

	public static void spendPoints(double value) {
		points -= value;
	}

	public static void takeDamage(int dmg) {
		playerHealth -= dmg;
		if (playerHealth <= 0) {
			gameOver();
		}
	}

	void fire() {
		fireAt(clicked);
	}

	void fireAt(Point p) {
		Point play = new Point((int) player.getCenterX(),
				(int) player.getCenterY());
		bullets.add(new Projectile((int) player.getCenterX(), (int) player
				.getCenterY(), player.getBulletSpeed(), Color.ORANGE, play, p,
				player.getDamage()));
	}

	static void setClickPoint(Point p) {
		clicked = p;
	}

	public static void gameOver() {
		currentState = END_STATE;
		menuEmu.setAnim(GamePanel.emuSit);
		menuEmu.setX(EmuCore.WIDTH / 6);

		timeAtEnd = getSecondsSinceStart();
		// timer.stop();
	}

	public static void start() {
		currentState = GAME_STATE;
		setDifficultyStats();
	}

	public static void setDifficulty(int diff) {
		difficulty = diff;
	}

	public static void setDifficultyStats() {
		if (difficulty == 0) {
			playerHealth = 100;
			points = 50;
			spawnCooldown = 8000;
			spawnChangeRate = 0.9999;
			specialsSpawnChance = 0;
			specialsSpawnChanceGrowthrate = 40;
			specialsSpawnChanceMax = 50;
		}
		if (difficulty == 1) {
			playerHealth = 50;
			points = 20;
			spawnCooldown = 6000;
			spawnChangeRate = 0.9992;
			specialsSpawnChance = 0;
			specialsSpawnChanceGrowthrate = 40;
			specialsSpawnChanceMax = 50;
		}
		if (difficulty == 2) {
			playerHealth = 10;
			points = 10;
			spawnCooldown = 4000;
			spawnChangeRate = 0.9985;
			specialsSpawnChance = 0;
			specialsSpawnChanceGrowthrate = 40;
			specialsSpawnChanceMax = 50;
		}
		if (difficulty == 9) {
			playerHealth = 100000;
			points = 100000;
			spawnCooldown = 3000;
			spawnChangeRate = 0.9985;
			specialsSpawnChance = 50;
			specialsSpawnChanceGrowthrate = 1;
			specialsSpawnChanceMax = 90;
		}
		timeAtStart = System.currentTimeMillis();
		score = 0;
		emus.clear();
		bullets.clear();
		buttons.clear();
		towers.clear();
		UpgradeButton.nextValidKey = 1;
		UpgradeButton.nextValidY = 65;
		createButtons();
		currentState = GAME_STATE;
	}

	public static void restart() {
		setDifficultyStats();
	}

	public void buy(int key) {
		for (UpgradeButton b : buttons) {
			if (b.getKey() == key + 1) {
				b.buy();
			}
		}
	}

	public static void spawnObject(String object) {
		GameObject obj;
		if (object.equals("Tower")) {
			obj = new Tower((int) player.getCenterX(),
					(int) player.getCenterY(), 100, 100, 0, Color.GREEN);
			towers.add((Tower) obj);
		}

	}

	public static void explodeAt(int x, int y, int size, ArrayList<BufferedImage> anim) {
		Explosion ex = null;
		for (Explosion e : explosionPool) {
			if (!e.isActive) {
				ex = e;
			}
		}
		if (ex == null) {
			ex = new Explosion(-100, -100, 150, 150);
		}
		ex.setAnim(anim);
		ex.setSize(size * 2);
		ex.setPosition(x, y);
		ex.startAnimation();
	}
	
	public static void damageArea(int x, int y, double size, double damage){
		for(Emu e : emus){
			double dist = dist(e.getCenterX(), e.getCenterY(), x, y);
			if(dist < size){
				e.takeDamage(damage/dist);
			}
		}
	}

	public static Point getClickedPoint() {
		return clicked;
	}
}
