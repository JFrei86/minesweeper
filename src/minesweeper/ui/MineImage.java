package minesweeper.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * MineImage is a utility extention of the JPanel class for use with displaying
 * a set collection of images.
 * 
 * 
 * @author Jesse Freitas 2013
 * 
 */
@SuppressWarnings("serial")
public class MineImage extends JPanel implements Icon
{

	public static final int NOT_A_MINE = -3;
	public static final int CHECK_MARK = -4;
	public static final int FLAG = -2;
	private BufferedImage img = null;

	/**
	 * Constructor that reads in an image file from the enumerated images based
	 * on the number of mines, or the finite constants defined in MineImage
	 * 
	 * @param n
	 *            the number of mines or the MineImage Constants.
	 */
	public MineImage(int n)
	{
		try
		{
			File f = new File(n + ".png");
			img = ImageIO.read(f);
		} catch (Exception e)
		{
			System.out.println("Loading Failed.");
		}
	}

	/**
	 * Accessor of the image read from IO.
	 * 
	 * @return The image.
	 */
	public Image getImage()
	{
		return img;
	}

	/**
	 * Overridden to paint the image read in.
	 */
	public void paint(Graphics g)
	{
		super.paint(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
	}

	/**
	 * The image height
	 * 
	 * @return the height
	 */
	public int getIconHeight()
	{
		return img.getHeight();
	}

	/**
	 * The image width
	 * 
	 * @return the width
	 */
	public int getIconWidth()
	{
		return img.getWidth();
	}

	/**
	 * Paints the Icon img into the the Panel component
	 */
	public void paintIcon(Component arg0, Graphics g, int x, int y)
	{
		g.drawImage(img, 0, 0, arg0.getWidth(), arg0.getHeight(), this);
	}
}
