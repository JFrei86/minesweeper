package minesweeper.ui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")

public class AIControlFrame extends JFrame
{
	private JPanel status = new JPanel();
	private JButton toggleAIControl = new JButton("Start/Stop");
	private JSlider AIspeed = new JSlider(JSlider.VERTICAL, 0, 1000, 500);
	private JLabel certainty = new JLabel();
	
	public AIControlFrame(final Frame f){
		toggleAIControl.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if(f.getAI().getTimer().isRunning()){
					f.getAI().getTimer().stop();
				}
				else{
					f.getAI().getTimer().start();
				}
					
			}
		});
		JMenu speed = new JMenu("AI Speed");
		AIspeed.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				f.getAI().setMoveDelay(AIspeed.getValue());
			}
		});
		speed.add(AIspeed);
		JMenuBar bar = new JMenuBar();
		bar.add(speed);
		bar.add(toggleAIControl);
		update(100);
		status.add(certainty);
		status.setForeground(Color.WHITE);
		add(status);
		
		setJMenuBar(bar);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(new Dimension(200,200));
	}
	public void update(int x){
		double green = 0;
		double red = 0;
		double blue = 0;
		if(x > 100 || x < 0)
			return;
		if(x >= 75){
			green = 255;
			red = ((100 - x) * 255 * 4) / 100;
		}
		else if(x <= 75 && x >= 50){
			red = 255;
			green = (((x - 50) / 25) * 255);
		}
		else{
			green = 0;
			red = 255;
		}
		
		if(x > 100 || x < 0)
			return;
		else if(x >= 90){
			blue = 255;
			red = 0;
			green = (((100 - x) / 10.0) * 255);
		}
		else if(x >= 80){
			blue = 255 - (((90 - x) / 10.0) * 255);
			red = 0;
			green = 255;
		}
		else if(x >= 70){
			blue = 0;
			red = (((80 - x) / 10.0) * 255);
			green = 255;
		}
		else if(x >= 60){
			blue = 0;
			red = 255;
			green = 255 - (((70 - x) / 10.0) * 255);
		}
		else{
			red = 255;
			green = 0;
			blue = 0;
		}
		System.out.println( red + " " + green + " " + blue);
		status.setBackground(new Color((int)red, (int)green, (int)blue));
		certainty.setText("Certainty: " + x);
	}
	/**
	 * @param args
	 */
	/*public static void main(String[] args)
	{
		final AIControlFrame aicontrol = new AIControlFrame(null);
		aicontrol.setVisible(true);
		for (int j = 0; j < 100; j++)
		{
			aicontrol.update(j);
			try{
				Thread.sleep(100);
			}catch(Exception e){}
		}
	}*/

}
