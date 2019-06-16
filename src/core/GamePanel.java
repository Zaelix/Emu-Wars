package core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import objects.GameObject;
import objects.Player;

public class GamePanel extends JPanel implements ActionListener, KeyListener,
		MouseListener {
	Timer timer;

	public GameManager dm = new GameManager();
	public static Random gen = new Random();
	float winScreenTimer = 0;

	public static ArrayList<BufferedImage> emuStand = new ArrayList<BufferedImage>();
	public static ArrayList<BufferedImage> emuRun = new ArrayList<BufferedImage>();
	public static ArrayList<BufferedImage> emuSit = new ArrayList<BufferedImage>();
	public static ArrayList<BufferedImage> emuFloat = new ArrayList<BufferedImage>();

	public static ArrayList<BufferedImage> explosion = new ArrayList<BufferedImage>();
	public static ArrayList<BufferedImage> turretFire = new ArrayList<BufferedImage>();
	public static ArrayList<BufferedImage> fireball = new ArrayList<BufferedImage>();

	public static BufferedImage shield;
	public static BufferedImage grenade;
	
	public static BufferedImage tankBase;
	public static BufferedImage tankTurret;

	// Colored Variant objects
	public static ArrayList<BufferedImage> emuExplosion = new ArrayList<BufferedImage>();

	// Random variants
	int variantFrame = 0;
	static Color variantColor = new Color(0, 0, 0);
	public static ArrayList<BufferedImage> runAnim = new ArrayList<BufferedImage>();
	public static ArrayList<ArrayList<BufferedImage>> runAnims = new ArrayList<ArrayList<BufferedImage>>();
	ArrayList<Character> typed = new ArrayList<Character>();

	private static int menuDifficultyChoice = 0;

	GamePanel() {
		timer = new Timer(1000 / 100, this);
		startGame();
	}

	void startGame() {
		dm.createButtons();
		loadImages();
		createWorld();
		timer.start();
	}

	void createWorld() {
	}

	void loadImages() {
		loadEmuImages();
		loadExplosionImages();
		loadTurretImages();
		loadFireballImages();
		shield = loadImage("spr_shield.png");
		grenade = loadImage("explosives.png").getSubimage(32, 0, 32, 32);
		tankBase = loadImage("tank_base.png");
		tankTurret = loadImage("tank_turret.png");
		
	}

	BufferedImage loadImage(String fileName) {
		BufferedImage img = null;
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		try {
			img = ImageIO.read(new File(s + "\\src\\" + fileName));
		} catch (IOException e) {
			System.out.println("failed to load " + s + "\\src\\" + fileName);
		}
		return img;
	}

	void loadEmuImages() {
		BufferedImage img = loadImage("bird.png");
		int xSize = (img.getWidth() / 4);
		int ySize = (img.getHeight() / 6);
		for (int i = 0; i < 4; i++) {
			emuStand.add(img.getSubimage(i * xSize, 0 * ySize, xSize, ySize));
		}
		for (int i = 0; i < 4; i++) {
			emuRun.add(img.getSubimage(i * xSize, 1 * ySize, xSize, ySize));
		}
		for (int i = 0; i < 4; i++) {
			emuSit.add(img.getSubimage(i * xSize, 2 * ySize, xSize, ySize));
		}
		for (int i = 0; i < 4; i++) {
			emuFloat.add(img.getSubimage(i * xSize, 5 * ySize, xSize, ySize));
		}
	}

	void loadExplosionImages() {
		BufferedImage img = loadImage("explosion.png");
		for (int i = 0; i < 10; i++) {
			explosion.add(img.getSubimage(i * 96, 0, 96, 96));
		}
		int[] arr = { gen.nextInt(3), gen.nextInt(3), gen.nextInt(3) };
		variantColor = new Color(gen.nextInt(150), gen.nextInt(150),
				gen.nextInt(150));
		for (int i = 0; i < 10; i++) {
			emuExplosion.add(getColoredImage(
					img.getSubimage(i * 96, 0, 96, 96), arr, variantColor));
		}
	}

	void loadTurretImages() {
		BufferedImage img = loadImage("kir-shoot.png");
		for (int i = 0; i < 5; i++) {
			turretFire.add(img.getSubimage(i * 96, 0, 96, 96));
		}
	}

	void loadFireballImages() {
		BufferedImage img = loadImage("fireball.png");
		for (int i = 0; i < 4; i++) {
			fireball.add(img.getSubimage(i * 26, 0, 26, 14));
		}
	}

	void checkForCheatKeyword() {
		String t = "";
		for (char c : typed) {
			t += c;
		}
		if (t.contains("taco")) {
			GameManager.gainPoints(777_777);
			typed.clear();
		}
		if (t.contains("diediedie")) {
			GameManager.spawnCooldown = 100;
			typed.clear();
		}
		if (t.contains("ineedmorebooms")) {
			Player.grenadeCooldown = 5;
			Player.maxGrenades = 10;
			typed.clear();
		}

	}

	void makeStandardizedAnimVariants() {
		if (runAnims.size() < 100) {
			int[] arr = { gen.nextInt(3), gen.nextInt(3), gen.nextInt(3) };
			runAnim.add(shuffleImageColorComponents(emuRun.get(variantFrame),
					arr, variantColor));
			variantFrame++;
			if (variantFrame > 3) {
				variantFrame = 0;

				runAnims.add(runAnim);
				runAnim = new ArrayList<BufferedImage>();
				variantColor = new Color(gen.nextInt(150), gen.nextInt(150),
						gen.nextInt(150));
			}
		}
	}

	void makeAnimVariants() {
		// System.out.println(runAnims.size());
		if (runAnims.size() < 100) {
			runAnim.add(getColoredImage(emuRun.get(variantFrame), null,
					variantColor));
			variantFrame++;
			if (variantFrame > 3) {
				variantFrame = 0;

				runAnims.add(runAnim);
				runAnim = new ArrayList<BufferedImage>();
				variantColor = new Color(gen.nextInt(150), gen.nextInt(150),
						gen.nextInt(150));
			}
		}
	}

	static BufferedImage getColoredImage(BufferedImage image, int[] order,
			Color cMod) {
		BufferedImage newImage = new BufferedImage(image.getWidth(),
				image.getHeight(), image.getType());
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int color = image.getRGB(x, y);
				int red = (color & 0xff0000) >> 16;
				int green = (color & 0xff00) >> 8;
				int blue = color & 0xff;
				int a = (color & 0xff000000) >>> 24;

				int c = cMod.getRGB();
				int r = red | ((c & 0xff0000) >> 16);
				int g = green | ((c & 0xff00) >> 8);
				int b = blue | (c & 0xff);
				c = (a << 24) | (r << 16) | (g << 8) | (b << 0);
				newImage.setRGB(x, y, c);
			}
		}
		return newImage;
	}

	static BufferedImage shuffleImageColorComponents(BufferedImage image,
			int[] order, Color cMod) {

		BufferedImage newImage = new BufferedImage(image.getWidth(),
				image.getHeight(), image.getType());
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int color = image.getRGB(x, y);
				int red = (color & 0xff0000) >> 16;
				int green = (color & 0xff00) >> 8;
				int blue = color & 0xff;
				int a = (color & 0xff000000) >>> 24;

				int c = cMod.getRGB();
				int r = red | ((c & 0xff0000) >> 16);
				int g = green | ((c & 0xff00) >> 8);
				int b = blue | (c & 0xff);
				c = (a << 24) | (r << 16) | (g << 8) | (b << 0);
				newImage.setRGB(x, y, c);
			}
		}
		return newImage;
	}

	public static BufferedImage setAlpha(BufferedImage img, int cx, int cy,
			byte alpha) {
		alpha %= 0xff;
		int color = img.getRGB(cx, cy);

		int mc = (alpha << 24) | 0x00ffffff;
		int newcolor = color & mc;
		img.setRGB(cx, cy, newcolor);

		return img;
	}

	@Override
	public void paintComponent(Graphics g) {
		// manager.draw(g);
		super.paintComponent(g);
		dm.draw(g);
		// g.drawString(runAnims.size()+"", 300, 30);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (GameManager.isPaused == false) {
			makeAnimVariants();
			// makeStandardizedAnimVariants();
			dm.update();
		}
		repaint();

	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println(e.getKeyCode());
		int keyCode = e.getKeyCode();
		char keyChar = e.getKeyChar();
		if (GameManager.currentState == GameManager.GAME_STATE) {
			if (keyChar == 'w') {
				Player.up = true;
			}
			if (keyChar == 's') {
				Player.down = true;
			}
			if (keyChar == 'p') {
				GameManager.togglePaused();
			}
			if (keyChar == 'b') {
				GameObject.debugRenderMode++;
				if (GameObject.debugRenderMode > 1) {
					GameObject.debugRenderMode = 0;
				}
			}
			if (keyCode == 127) {
				GameManager.gameOver();
			}
			if (keyCode == 10) {
				GameManager.restart();
			}

			if (keyCode == 32) {

				dm.player.setFiringScattered(true);
			}

			// 49 is the 1 key
			if (keyCode >= 49 && keyCode <= 58) {
				dm.buy(keyCode - 49);
			}
		}
		if (GameManager.currentState == GameManager.MENU_STATE) {
			if (keyCode == 49) {
				setDifficulty(0);
			}
			if (keyCode == 50) {
				setDifficulty(1);
			}
			if (keyCode == 51) {
				setDifficulty(2);
			}
			if (keyCode == 57) {
				setDifficulty(9);
			}
			if (keyCode == 10 || keyCode == 32) {
				GameManager.menuEmu.setX(EmuCore.WIDTH * 2);
				GameManager.start();
			}
			if (keyCode == 38 || keyCode == 87) {
				setDifficulty(menuDifficultyChoice - 1);
			}

			if (keyCode == 40 || keyCode == 83) {
				setDifficulty(menuDifficultyChoice + 1);
			}
		}
		if (GameManager.currentState == GameManager.END_STATE) {
			if (keyCode == 10) {
				GameManager.currentState = GameManager.MENU_STATE;
				GameManager.menuEmu.setX(EmuCore.WIDTH / 2);
			}
		}

	}

	public static void setDifficulty(int d) {
		if (d > 2 && d != 9) {
			d = 2;
		} else if (d < 0) {
			d = 0;
		}
		menuDifficultyChoice = d;
		if (d == 0) {
			GameManager.menuEmu.setAnim(emuSit);
		} else if (d == 1) {
			GameManager.menuEmu.setAnim(emuStand);
		} else if (d == 2) {
			GameManager.menuEmu.setAnim(emuRun);
		} else if (d == 9) {
			GameManager.menuEmu.setAnim(emuFloat);
		}
		GameManager.setDifficulty(d);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		char keyChar = e.getKeyChar();
		if (keyChar == 'w') {
			Player.up = false;
		}
		if (keyChar == 's') {
			Player.down = false;
		}
		if (keyCode == 32) {
			dm.player.setFiringScattered(false);
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		char keyChar = e.getKeyChar();
		typed.add(keyChar);

		if (typed.size() > 10) {
			typed.remove(0);
		}
		checkForCheatKeyword();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {

		GameManager.setClickPoint(new Point(e.getX(), e.getY()));
		if (GameManager.currentState == GameManager.GAME_STATE) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getX() > 230) {
				dm.player.setFiring(true);
			}
			if (e.getButton() == MouseEvent.BUTTON3) {
				dm.player.throwGrenade();
			}
			GameManager.checkIfUpgradeButtonsClicked(e);
		}
		if (GameManager.currentState == GameManager.MENU_STATE) {
			for (int i = 0; i < GameManager.menuDifficultyRects.length; i++) {
				if (GameManager
						.mouseIntersects(GameManager.menuDifficultyRects[i])) {
					GamePanel.setDifficulty(i);

					GameManager.menuEmu.setX(EmuCore.WIDTH * 2);
					GameManager.start();
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			dm.player.setFiring(false);
		}
	}

}
