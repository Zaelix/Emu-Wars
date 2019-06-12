package core;


import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class EmuCore {
	public static final int WIDTH = 1800;
	public static final int HEIGHT = 1000;
	public static JFrame frame;
	GamePanel panel;
	
	static BufferedImage blankCursorImg;
	static BufferedImage customCursorImg;
	
	static Cursor blankCursor;
	static Cursor customCursor;

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EmuCore es = new EmuCore();
		es.setup();
	}
	void setup(){
		frame = new JFrame();
		panel = new GamePanel();
		frame.setVisible(true);
		frame.setSize(WIDTH,HEIGHT);
		frame.setTitle("The Second Great Emu War");
		frame.add(panel);
		frame.addKeyListener(panel);
		frame.addMouseListener(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
		loadCursors();
		setCursor(1);
		frame.pack();
		
	}
	
	void loadCursors(){
		// Transparent 16 x 16 pixel cursor image.
		blankCursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		
		try {
			customCursorImg = ImageIO.read(new File(s + "\\src\\cursor.png"));
			
			// Create a new blank cursor.
			blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
					blankCursorImg, new Point(0, 0), "blank cursor");
			customCursor = Toolkit.getDefaultToolkit().createCustomCursor(
					customCursorImg, new Point(0, 0), "custom cursor");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setCursor(int type){
		if(type == 0){
			frame.getContentPane().setCursor(blankCursor);
		}
		else if(type == 1){
			frame.getContentPane().setCursor(customCursor);
		}
	}
	
	
	/*
	 * CREDITS:
	 * explosion by J-Robot, OpenGameArt.org
	 * gungirl by Spring, OpenGameArt.org
	 * kir by Spring, OpenGameArt.org
	 * shield by Bonsaiheldin, OpenGameArt.org
	 * explosives by BizmasterStudios, OpenGameArt.org
	 */
}
