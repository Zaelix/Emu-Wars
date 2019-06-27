package core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
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
import objects.Jerky;
import objects.Player;
import objects.Projectile;
import objects.Shield;
import objects.Soldier;
import objects.UpgradeButton;

public class GameManager {
	static boolean isPaused = false;
	public static int currentState = 0;
	public static final int MENU_STATE = 0;
	public static final int GAME_STATE = 1;
	public static final int END_STATE = 2;
	public static double frameRate = 1;

	public static int mouseXOffset = 10;
	public static int mouseYOffset = 32;
	static int playerHealth = 10;
	public static Rectangle base = new Rectangle(0, 0, 210, EmuCore.HEIGHT);
	static double points = 10;
	static int score = 0;
	static Point mouseLoc = new Point();
	static Point clicked = new Point();

	public static int topCategory = 0;
	static ArrayList<ArrayList<UpgradeButton>> buttonCategories = new ArrayList<ArrayList<UpgradeButton>>();
	static ArrayList<UpgradeButton> buttons = new ArrayList<UpgradeButton>();
	
	static ArrayList<Projectile> bullets = new ArrayList<Projectile>();
	static ArrayList<Emu> emus = new ArrayList<Emu>();
	static ArrayList<Emu> closestEmus = new ArrayList<Emu>();
	static ArrayList<Soldier> soldiers = new ArrayList<Soldier>();
	static ArrayList<Shield> shields = new ArrayList<Shield>();
	static ArrayList<Grenade> grenades = new ArrayList<Grenade>();
	static ArrayList<Jerky> jerkies = new ArrayList<Jerky>();
	static ArrayList<Explosion> explosionPool = new ArrayList<Explosion>();

	private static Player player = new Player(250, 450, 50, 100, Color.BLUE);
	static Emu menuEmu = new Emu(EmuCore.WIDTH / 2, 200, 200, 300, 0,
			Color.BLUE, GamePanel.emuSit);
	static Rectangle menuSelection = new Rectangle(100,
			(int) (EmuCore.HEIGHT * 0.3));

	static Rectangle[] menuDifficultyRects = new Rectangle[3];
	static int difficulty = 0;
	long lastFrameTime;
	long spawnTimer;
	static long spawnCooldown = 5000;
	public static long deltaTime = 1;
	static double spawnChangeRate = 0.9985; // 0.9996 is the original value
	public static long frameCount = 0;
	long farmerTimer;
	static long farmerCooldown = 10000;
	static long pausedStart;
	static long pausedEnd;
	static long timeAtStart;
	static long timeAtEnd;

	public static Font defaultFont = new Font("AR ESSENCE", Font.BOLD, 20);
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

		menuDifficultyRects[0] = new Rectangle(100,
				(int) (EmuCore.HEIGHT * 0.3) - 50, 250, 70);
		menuDifficultyRects[1] = new Rectangle(100,
				(int) (EmuCore.HEIGHT * 0.4) - 50, 250, 70);
		menuDifficultyRects[2] = new Rectangle(100,
				(int) (EmuCore.HEIGHT * 0.5) - 50, 250, 70);
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
		g.drawString("2", 100, (int) (EmuCore.HEIGHT * 0.4));
		g.drawString("3", 100, (int) (EmuCore.HEIGHT * 0.5));
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
			for (Projectile p : bullets) {
				p.draw(g);
			}
			for (Emu e : emus) {
				e.draw(g);
			}
			for (Soldier t : soldiers) {
				t.draw(g);
			}

			getPlayer().draw(g);

			for (int i = 0; i < explosionPool.size(); i++) {
				explosionPool.get(i).draw(g);
			}

			for (int i = 0; i < grenades.size(); i++) {
				grenades.get(i).draw(g);
			}

			g.setColor(Color.GRAY);
			g.fillRect(0, 0, 220, EmuCore.HEIGHT);
			for (int i = 0; i < buttons.size(); i++) {
				buttons.get(i).draw(g);
			}
			for (int i = 0; i < jerkies.size(); i++) {
				jerkies.get(i).draw(g);
			}
			
			drawLine(g);
			// drawLineToCursor(g);
			drawPointsBox(g);
			drawHealthBox(g);
			g.drawString(getSecondsSinceStart() + "", EmuCore.WIDTH / 2, 40);
			g.drawString(spawnCooldown + "", (int) (EmuCore.WIDTH * 0.8), 40);
		}
	}

	void drawEndState(Graphics g) {
		g.setFont(defaultFont);
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
		g.setFont(defaultFont);
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
		g.setFont(defaultFont);
		String pts = myFormatter.format(points);
		g.setColor(Color.BLACK);
		g.fillRect(10, 10, 120, 49);
		g.setColor(new Color(40, 240, 40));
		g.fillRect(11, 11, 118, 47);

		g.setColor(Color.BLACK);
		g.drawString("Jerky: ", 12, 40);
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
		EmuCore.setCursor(1);
		menuEmu.update();
		for (int i = 0; i < menuDifficultyRects.length; i++) {
			if (mouseIntersects(menuDifficultyRects[i])) {
				GamePanel.setDifficulty(i);
			}
		}

	}

	public static ArrayList<Emu> getClosestEmus() {
		return closestEmus;
	}

	ArrayList<Emu> findClosestEnemies() {
		ArrayList<Emu> closest = new ArrayList<Emu>();
		for (Emu e : emus) {
			if (isCloserThanAny(closest, (int) e.getCenterX())) {
				if (closest.size() == 5) {
					closest.remove(getIndexOfFarthest(closest));
				}
				closest.add(e);
			}
		}
		return closest;
	}

	boolean isCloserThanAny(ArrayList<Emu> closest, int dist) {
		if (closest.size() < 5) {
			return true;
		}
		for (Emu e : closest) {
			if (dist < e.getCenterX()) {
				return true;
			}
		}
		return false;
	}

	int getIndexOfFarthest(ArrayList<Emu> closest) {
		int farthest = 0;
		int index = -1;
		for (int i = 0; i < closest.size(); i++) {
			if (closest.get(i).getCenterX() > farthest) {
				farthest = (int) closest.get(i).getCenterX();
				index = i;
			}
		}
		return index;
	}

	void updateGameState() {
		deltaTime = System.currentTimeMillis() - lastFrameTime;
		frameRate = (1000.0 / (deltaTime));
		String fps = myFormatter.format(frameRate);
		// System.out.println(fps);
		lastFrameTime = System.currentTimeMillis();
		if (frameCount % 10 == 0) {
			closestEmus = findClosestEnemies();
		}
		if (mouseLoc.x > 230) {
			EmuCore.setCursor(0);
		} else {
			EmuCore.setCursor(1);
		}
		getPlayer().update();
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
		for (int i = 0; i < soldiers.size(); i++) {
			soldiers.get(i).update();
		}
		for (int i = 0; i < explosionPool.size(); i++) {
			explosionPool.get(i).update();
		}
		for (int i = 0; i < grenades.size(); i++) {
			grenades.get(i).update();
		}
		
		for (int i = 0; i < jerkies.size(); i++) {
			jerkies.get(i).update();
		}
		spawnEmus();
		spawnFarmers();
		checkCollisions();
		purgeObjects();

		// Increasing spawn rates
		if (frameCount % 10 == 0 && spawnCooldown > 200) {
			spawnCooldown *= spawnChangeRate;
		} else if (spawnCooldown <= 200 && frameCount % 10 == 0 && Emu.healthDifficultyDivisor > 0.01) {
			Emu.healthDifficultyDivisor -= 0.01;
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

	

	public static int getSecondsSinceStart() {
		return (int) ((System.currentTimeMillis() - timeAtStart) / 1000);
	}

	void drawLineToCursor(Graphics g) {
		g.setColor(Color.RED);
		g.drawLine((int) getPlayer().getX(), (int) getPlayer().getY(), clicked.x
				- mouseXOffset, clicked.y - mouseYOffset);
	}

	void drawLine(Graphics g) {
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		Point frame = EmuCore.frame.getLocation();
		clicked = mouseLoc;
		g.setColor(Color.RED);
		g.drawLine((int) getPlayer().getCenterX(), (int) getPlayer().getCenterY(),
				clicked.x - mouseXOffset, clicked.y - mouseYOffset);
		getPlayer().setFiringAngle(clicked);
	}

	public Point getMouseLocation() {
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		Point frame = EmuCore.frame.getLocation();
		return new Point((int) (mouse.getX() - frame.getX()),
				(int) (mouse.getY() - frame.getY()));
	}

	public static boolean mouseIntersects(Rectangle box) {
		Rectangle mLoc = new Rectangle(mouseLoc.x - mouseXOffset, mouseLoc.y
				- mouseYOffset, 1, 1);
		if (mLoc.intersects(box)) {
			return true;
		}
		return false;
	}

	public static void checkIfUpgradeButtonsClicked(MouseEvent e) {
		for (UpgradeButton b : buttons) {
			if (!b.isDivider
					&& mouseIntersects(new Rectangle(b.getX(), b.getY(), 200,
							b.getHeight() * 2))) {
				b.buy();
			}
		}
	}

	public static float dist(double x, double y, double x2, double y2) {
		return (float) Math.sqrt(Math.pow((x - x2), 2) + Math.pow((y - y2), 2));
	}

	void spawnEmus() {
		if (System.currentTimeMillis() - spawnTimer >= spawnCooldown
				&& GamePanel.runAnims.size() > 0) {
			spawnEmu(GamePanel.runAnims.get(0));
			GamePanel.runAnims.remove(0);
			spawnFarmer();
		} else if (System.currentTimeMillis() - spawnTimer >= spawnCooldown
				&& GamePanel.runAnims.size() == 0) {
			spawnEmu(GamePanel.emuRun);
		}
		
	}
	void spawnFarmers() {
		if (System.currentTimeMillis() - farmerTimer >= farmerCooldown) {
			spawnFarmer();
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
	
	void spawnFarmer(){
		farmerTimer = System.currentTimeMillis();
		Soldier obj = new Soldier(EmuCore.WIDTH + 20,
				new Random().nextInt(750) + 100, 100, 100, Color.GREEN);
		soldiers.add((Soldier) obj);
	}

	static void createButtons() {
		buttons.clear();
		buttons.add(new UpgradeButton("TANK"));
		buttons.add(new UpgradeButton("T.Fire Rate", 1, 1));
		buttons.add(new UpgradeButton("T.Bullet Speed", 1, 1.4));
		buttons.add(new UpgradeButton("T.Bullet Dmg", 1, 1));
		buttons.add(new UpgradeButton("T.Move Speed", 1, 2.3));
		buttons.add(new UpgradeButton("SOLDIER"));
		buttons.add(new UpgradeButton("Buy Soldier", 1, 0.35, "Tower"));
		buttons.add(new UpgradeButton("S.Fire Rate", 1, 0.2));
		buttons.add(new UpgradeButton("S.Bullet Speed", 1, 0.3));
		buttons.add(new UpgradeButton("S.Bullet Dmg", 1, 0.2));
		buttons.add(new UpgradeButton("GRENADE"));
		buttons.add(new UpgradeButton("G.Count", 1, 1, 1));
		buttons.add(new UpgradeButton("G.Refill rate", 4000, 10, -10));
		buttons.add(new UpgradeButton("G.Dmg", 1, 2, 0.1));
		buttons.add(new UpgradeButton("G.Area", 100, 2, 5));
		
	}
	
	public static void createButtonsWithCategories(){
		buttonCategories.clear();
		ArrayList<UpgradeButton> tankButtons = new ArrayList<UpgradeButton>();
		tankButtons.add(new UpgradeButton("TANK"));
		tankButtons.add(new UpgradeButton("T.Fire Rate", 1, 1));
		tankButtons.add(new UpgradeButton("T.Bullet Speed", 1, 1.4));
		tankButtons.add(new UpgradeButton("T.Bullet Dmg", 1, 1));
		tankButtons.add(new UpgradeButton("T.Move Speed", 1, 2.3));
		
		ArrayList<UpgradeButton> soldierButtons = new ArrayList<UpgradeButton>();
		soldierButtons.add(new UpgradeButton("SOLDIER"));
		soldierButtons.add(new UpgradeButton("Buy Soldier", 20, 285, "Tower").setPercentageBasedCost(true).setCost(10));
		soldierButtons.add(new UpgradeButton("S.Fire Rate", 1, 0.2));
		soldierButtons.add(new UpgradeButton("S.Bullet Speed", 1, 3).setPercentageBasedCost(true));
		soldierButtons.add(new UpgradeButton("S.Bullet Dmg", 1, 2).setPercentageBasedCost(true));
		soldierButtons.add(new UpgradeButton("S.Move Speed", 1, 0.4));

		ArrayList<UpgradeButton> grenadeButtons = new ArrayList<UpgradeButton>();
		grenadeButtons.add(new UpgradeButton("GRENADE"));
		grenadeButtons.add(new UpgradeButton("G.Count", 1, 1, 1));
		grenadeButtons.add(new UpgradeButton("G.Refill rate", 4000, 10, -1).setPercentageBasedValue(true).setPercentageBasedCost(true));
		grenadeButtons.add(new UpgradeButton("G.Dmg", 1, 2, 0.1).setPercentageBasedCost(true));
		grenadeButtons.add(new UpgradeButton("G.Area", 100, 4, 5).setPercentageBasedCost(true).setCost(10));
		
		buttonCategories.add(tankButtons);
		buttonCategories.add(soldierButtons);
		buttonCategories.add(grenadeButtons);
		
		assignButtonKeys();
		addButtonsToUI();
	}
	
	public static void addButtonsToUI(){
		buttons.clear();
		int x = topCategory; 
		System.out.println("Category:" + x);
		UpgradeButton.nextValidY = 65;
		for(int i = 0; i < buttonCategories.size(); i++){
			if(x>= buttonCategories.size()){
				x = 0;
			}
			for(int k = 0; k < buttonCategories.get(x).size(); k++){
				buttons.add(buttonCategories.get(x).get(k));
			}
			x++;
		}
		assignButtonPositions();
		assignButtonKeys();
	}
	
	static void assignButtonPositions(){
		for(int i = 0; i < buttons.size(); i++){
			buttons.get(i).calculateYPosition();
		}
	}
	
	static void assignButtonKeys(){
		UpgradeButton.nextValidKey = 1;
		for(int i = 0; i < buttons.size(); i++){
			if(!buttons.get(i).isDivider){
				buttons.get(i).setKey(UpgradeButton.nextValidKey);
				UpgradeButton.nextValidKey++;
			}
		}
	}
	
	void updateStats() {
		int[] i = findUpgradeButtonIndexes();
		getPlayer().setFireCooldown((long) (600 / buttons.get(i[0]).getValue()));
		getPlayer().setBulletSpeed(2 + buttons.get(i[1]).getValue());
		getPlayer().setDamage(buttons.get(i[2]).getValue());
		getPlayer().setSpeed(buttons.get(i[3]).getValue());

		Soldier.setFireCooldown((long) (5000 / buttons.get(i[5]).getValue()));
		Soldier.setBulletSpeed(1 + buttons.get(i[6]).getValue());
		Soldier.setDamage(buttons.get(i[7]).getValue());
		Soldier.setMoveSpeed(buttons.get(i[8]).getValue());
		
		Player.maxGrenades = (int) buttons.get(i[9]).getValue();
		Player.grenadeCooldown = (long)buttons.get(i[10]).getValue();
		Grenade.damage = buttons.get(i[11]).getValue();
		Grenade.maxDamage = buttons.get(i[11]).getValue()*5;
		Grenade.diameter = (int)buttons.get(i[12]).getValue();
	}
	
	int[] findUpgradeButtonIndexes(){
		int[] i = new int[13];
		i[0] = findButtonIndex("T.Fire Rate");
		i[1] = findButtonIndex("T.Bullet Speed");
		i[2] = findButtonIndex("T.Bullet Dmg");
		i[3] = findButtonIndex("T.Move Speed");

		i[4] = findButtonIndex("Buy Soldier");
		i[5] = findButtonIndex("S.Fire Rate");
		i[6] = findButtonIndex("S.Bullet Speed");
		i[7] = findButtonIndex("S.Bullet Dmg");
		i[8] = findButtonIndex("S.Move Speed");

		i[9] = findButtonIndex("G.Count");
		i[10] = findButtonIndex("G.Refill rate");
		i[11] = findButtonIndex("G.Dmg");
		i[12] = findButtonIndex("G.Area");
		return i;
	}
	
	int findButtonIndex(String s){
		for(int i = 0; i < buttons.size(); i++){
			if(buttons.get(i).getText().equals(s)){
				return i;
			}
		}
		return -1;
	}

	void checkCollisions() {
		for (Projectile p : bullets) {
			for (Emu e : emus) {
				if (e.isAlive() && e.collidesWith(p)) {
					p.setAlive(false);
				}
			}
			for (Shield s : shields) {
				if (s.isAlive() && s.collidesWith(p)) {
					s.takeDamage(p.getDamage());
					p.setAlive(false);
				}
			}
		}
		for(Emu e : emus){
			for(Soldier s : soldiers){
				if(s.isFarmer && e.getX() < EmuCore.WIDTH*0.90 && s.getCollisionBox().intersects(e.getCollisionBox())){
					s.takeDamage(1);
					e.setMoveSpeedPercent(0.2);
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
		
		for (int i = jerkies.size() - 1; i >= 0; i--) {
			if (jerkies.get(i).isAlive() == false) {
				jerkies.remove(i);
			}
		}
		
		for (int i = soldiers.size() - 1; i >= 0; i--) {
			if (soldiers.get(i).isAlive() == false) {
				soldiers.remove(i);
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

	public static void addGrenade(Grenade g) {
		grenades.add(g);
	}
	
	public static void addJerky(Jerky j) {
		jerkies.add(j);
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
		Point play = new Point((int) getPlayer().getCenterX(),
				(int) getPlayer().getCenterY());
		bullets.add(new Projectile((int) getPlayer().getCenterX(), (int) getPlayer()
				.getCenterY(), getPlayer().getBulletSpeed(), Color.ORANGE, play, p,
				getPlayer().getDamage()));
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
		soldiers.clear();
		shields.clear();
		UpgradeButton.nextValidKey = 1;
		UpgradeButton.nextValidY = 65;
		//createButtons();
		createButtonsWithCategories();
		currentState = GAME_STATE;
	}

	public static void restart() {
		setDifficultyStats();
		createButtonsWithCategories();
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
			obj = new Soldier((int) getPlayer().getCenterX(),
					(int) getPlayer().getCenterY(), 100, 100, Color.GREEN);
			((Soldier) obj).trainAsSoldier();
			soldiers.add((Soldier) obj);
		}

	}

	public static void explodeAt(int x, int y, int size,
			ArrayList<BufferedImage> anim) {
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

	public static void damageArea(int x, int y, double size, double damage,
			double maxDamage) {
		for (Emu e : emus) {
			double dist = dist(e.getCenterX(), e.getCenterY(), x, y)
					- e.getAverageSize() / 2;
			if (dist <= 0) {
				dist = 1;
			}
			if (dist <= size) {
				double d = Math.min(damage * (size / dist), maxDamage);
				e.takeDamage(d);
				// System.out.println(d);
			}
		}
	}

	public static Point getClickedPoint() {
		return clicked;
	}

	public static Player getPlayer() {
		return player;
	}

	public static void setPlayer(Player player) {
		GameManager.player = player;
	}
}
