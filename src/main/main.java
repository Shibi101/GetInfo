package main;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class main {

	public static void main(String[] args) {
		
		String osName = ("OS name: " + System.getProperty("os.name"));
		JFrame frame = new JFrame("IP adress");
		JLabel OsName = new JLabel(("OS name: " + System.getProperty("os.name")));
		JLabel OsVersion = new JLabel(("OS version: " + System.getProperty("os.version")));
		JLabel OsArch = new JLabel(("OS architecture: " + System.getProperty("os.arch")));
		frame.setLayout(new GridLayout(5,2));
		
		frame.add(OsName);
		frame.add(OsVersion);
		frame.add(OsArch);
		frame.setSize(300,200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}

}
