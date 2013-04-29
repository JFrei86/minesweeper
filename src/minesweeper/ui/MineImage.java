package minesweeper.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JPanel;



@SuppressWarnings("serial")
public class MineImage extends JPanel implements Icon{
	
	public static final int NOT_A_MINE = -3;
	public static final int CHECK_MARK = -4;
	public static final int FLAG = -2;
	private BufferedImage img = null;
	
	public MineImage(int n){
		try{
			File f = new File(n + ".png");
			img = ImageIO.read(f);
		}
		catch(Exception e){System.out.println("Loading Failed.");}
	}
	public Image getImage(){
		return img;
	}
	public void paint(Graphics g){
		super.paint(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
	}
	public int getIconHeight() {
		return img.getHeight();
	}
	public int getIconWidth() {
		return img.getWidth();
	}
	public void paintIcon(Component arg0, Graphics g, int x, int y) {
		g.drawImage(img, 0,0, arg0.getWidth(), arg0.getHeight(), this);
	}
}
