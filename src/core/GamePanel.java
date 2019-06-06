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

	public static BufferedImage shield;

	// Colored Variant objects
	int variantFrame = 0;
	Color variantColor = new Color(0, 0, 0);
	public static ArrayList<BufferedImage> runAnim = new ArrayList<BufferedImage>();
	public static ArrayList<ArrayList<BufferedImage>> runAnims = new ArrayList<ArrayList<BufferedImage>>();
	ArrayList<Character> typed = new ArrayList<Character>();

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
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		try {
			shield = ImageIO.read(new File(s + "\\src\\spr_shield.png"));
		} catch (IOException e) {
			System.out.println("failed to load " + s + "\\src\\spr_shield.png");
		}

	}

	void loadEmuImages() {
		BufferedImage img = null;
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		try {
			img = ImageIO.read(new File(s + "\\src\\bird.png"));
		} catch (IOException e) {
			System.out.println("failed to load " + s + "\\src\\bird.png");
		}
		for (int i = 0; i < 4; i++) {
			emuStand.add(img.getSubimage(i * (img.getWidth() / 4), 0*(img.getHeight() / 6), img.getWidth() / 4,	(img.getHeight() / 6)));
		}
		for (int i = 0; i < 4; i++) {
			  emuRun.add(img.getSubimage(i * (img.getWidth() / 4), 1*(img.getHeight() / 6), img.getWidth() / 4,	(img.getHeight() / 6) ));
		}
		for (int i = 0; i < 4; i++) {
			  emuSit.add(img.getSubimage(i * (img.getWidth() / 4), 2*(img.getHeight() / 6), img.getWidth() / 4,	(img.getHeight() / 6)));
		}
		for (int i = 0; i < 4; i++) {
			emuFloat.add(img.getSubimage(i * (img.getWidth() / 4), 5*(img.getHeight() / 6), img.getWidth() / 4,	(img.getHeight() / 6)));
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

	}

	void makeStandardizedAnimVariants() {
		if (runAnims.size() < 100) {
			int[] arr = { gen.nextInt(3), gen.nextInt(3), gen.nextInt(3) };
			runAnim.add(shuffleImageColorComponents(emuRun.get(variantFrame),
					arr));
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
			runAnim.add(getColoredImage(emuRun.get(variantFrame), variantColor));
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

	static ArrayList<BufferedImage> getColoredAnimation(
			ArrayList<BufferedImage> anim, Color cmod) {
		ArrayList<BufferedImage> newAnim = new ArrayList<BufferedImage>();
		for (BufferedImage frame : anim) {
			newAnim.add(getColoredImage(frame, cmod));
		}
		return newAnim;
	}

	static BufferedImage getColoredImage(BufferedImage image, Color cMod) {
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
			int[] order) {
		BufferedImage newImage = new BufferedImage(image.getWidth(),
				image.getHeight(), image.getType());
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int color = image.getRGB(x, y);
				Color c = new Color(color);
				int[] colorOrder = { c.getRed(), c.getGreen(), c.getBlue() };
				Color newC = new Color(colorOrder[order[0]],
						colorOrder[order[1]], colorOrder[order[2]]);
				newImage.setRGB(x, y, newC.getRGB());
			}
		}
		return newImage;
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
		// System.out.println(e.getKeyCode());
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
				GameManager.menuEmu.setAnim(emuSit);
				GameManager.setDifficulty(0);
			}
			if (keyCode == 50) {
				GameManager.menuEmu.setAnim(emuStand);
				GameManager.setDifficulty(1);
			}
			if (keyCode == 51) {
				GameManager.menuEmu.setAnim(emuRun);
				GameManager.setDifficulty(2);
			}
			if (keyCode == 57) {
				GameManager.menuEmu.setAnim(emuFloat);
				GameManager.setDifficulty(9);
			}
			if(keyCode == 10){
				GameManager.menuEmu.setX(EmuCore.WIDTH*2);
				GameManager.start();
			}
		}
		if(GameManager.currentState == GameManager.END_STATE){
			if (keyCode == 10) {
				GameManager.restart();
			}
		}

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
		// TODO Auto-generated method stub

		dm.player.setFiring(true);
		GameManager.setClickPoint(new Point(e.getX(), e.getY()));
		// dm.fire();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

		dm.player.setFiring(false);
	}

}
