import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.Timer;


public class JTimerLabel extends JLabel implements ActionListener{
	
	private int hours = 0;
	private int minutes = 0;
	private int seconds = 0;
	
	private Timer timeElapsed = new Timer(1000, this);
	
	public JTimerLabel(String s){super(s);}
	
	public JTimerLabel(){this(" Time: 00:00:00 ");}
	
	public void start(){timeElapsed.start();}
	
	public void stop(){timeElapsed.stop();}
	
	public void reset(){
		hours = 0;
		minutes = 0;
		seconds = 0;
		setText(toString());
	}
	public String toString(){
		String s = "";
		if(hours < 10) s += " Time: 0" + hours;
		else s += " " + hours;
		
		if(minutes < 10) s += ":0" + minutes;
		else s += ":" + minutes;
		
		if(seconds < 10) s += ":0" + seconds + " ";
		else s += ":" + seconds + " ";
		return s;
	}
	public boolean isRunning(){return timeElapsed.isRunning();}

	public void actionPerformed(ActionEvent e) {
		seconds++;
		if(seconds == 60){
			seconds = 0;
			minutes++;
			if(minutes == 60){
				minutes = 0;
				hours++;
			}
		}
		setText(toString());
	}
}
