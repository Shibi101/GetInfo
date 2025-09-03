package main;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class main {

	private static final boolean INCLUDE_LINK_LOCAL = true;
	private static final boolean INCLUDE_LOOPBACK = true;
    private static JLabel IP;
    private static JLabel osName;
    private static JLabel osVersion;
    private static JLabel osArch;
    private static JLabel NetworkIPs;
    private static JTextArea textArea;

	public static void main(String[] args) throws Exception {
		
		Font defaultFont = new Font("SansSerif", Font.PLAIN, 30); // Adjust size as needed

		UIManager.put("Label.font", defaultFont);
		UIManager.put("Button.font", defaultFont);
		UIManager.put("TextArea.font", defaultFont);
		UIManager.put("TextField.font", defaultFont);
		UIManager.put("Panel.font", defaultFont);
		UIManager.put("ScrollPane.font", defaultFont);
		
		
		
		JFrame frame = new JFrame("IP adress");
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		//frame.setUndecorated(true);
		//frame.setSize(1080,1920);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		
		// System info labels
        osName = new JLabel("OS name: " + System.getProperty("os.name"));
        osVersion = new JLabel("OS version: " + System.getProperty("os.version"));
        osArch = new JLabel("OS architecture: " + System.getProperty("os.arch"));
	    //List<String> arpEntries = parseArpTable();

        // IP label
        IP = new JLabel("Local IP: " + getIp());
		
		JButton RefreshButton = new JButton("refresh");
		RefreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String newIp = getIp();
					IP.setText("Local IP: "+ newIp);
					
					osName.setText("OS name: "+ System.getProperty("os.name"));
					osVersion.setText("OS version: "+ System.getProperty("os.version"));
					osArch.setText("OS architecture: "+ System.getProperty("os.arch"));
					
			        // Scan new devices
		            List<String> arpEntries = GetListofIp();
			        // Populate text area
			        updateTextArea(textArea, arpEntries);
					
					frame.revalidate();
					frame.repaint();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				}
			}
		);
		
		// Top panel for system info
		JPanel topPanel = new JPanel(new GridLayout(4, 1));
		topPanel.add(osName);
		topPanel.add(osVersion);
		topPanel.add(osArch);
		topPanel.add(IP);
		frame.add(topPanel, BorderLayout.NORTH);
		
		// Center scrollable area
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		frame.add(scrollPane, BorderLayout.CENTER);
		
		// Bottom panel for refresh button
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(RefreshButton);
		frame.add(bottomPanel, BorderLayout.SOUTH);
		
//		frame.add(osName);
//		frame.add(osVersion);
//		frame.add(osArch);
//		frame.add(IP);
//		frame.add(RefreshButton);
//		GetListofIp();
	}
	
    private static void updateTextArea(JTextArea textArea, List<String> list) {
        textArea.setText("");  // clear previous content
        for (String item : list) {
            textArea.append(item + "\n");  // add each item on a new line
        }
    }
	
	public static String getIp() throws SocketException, UnknownHostException {
		//make string to store localadress which reaches 8.8.8.8
		String ipadres;
		
		//create socket which connects to 8.8.8.8 (google)
		try(final DatagramSocket socket = new DatagramSocket()){
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			ipadres = socket.getLocalAddress().getHostAddress();
		}
		return ipadres;
	}
	
	private static List<String> getIpAddresses(boolean includeLinkLocal, boolean includeLoopback)throws SocketException {
		List<String> ipAddresses = new ArrayList<>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			for(InterfaceAddress interfaceAddress : interfaces.nextElement().getInterfaceAddresses()) {
				InetAddress addr = interfaceAddress.getAddress();
				boolean include = true;
				if(addr.isLinkLocalAddress() && !includeLinkLocal) {
					include = false;
				}
				if(addr.isLoopbackAddress() && ! includeLoopback) {
					include = false;
				}
				if(include) {
					ipAddresses.add(addr.toString().substring(1));
				}
			}
		}
		return ipAddresses;
	}
	
	public static String ExtractIp(List<String> ipAddresses) {
		for(String ip : ipAddresses) {
			return ip;
		}
		return null;
		
	}
	
	public static String getSubnet() throws UnknownHostException, SocketException {
		try(DatagramSocket socket = new DatagramSocket()){
		socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
        InetAddress localHost = socket.getLocalAddress();
		
		byte[] ipAddr = localHost.getAddress();
		return String.format("%d.%d.%d", (ipAddr[0] & 0xFF), (ipAddr[1] & 0xFF), (ipAddr[2]& 0xFF));
		}
	}
	
	public static List<String> GetListofIp() throws Exception {
		String subnet = getSubnet();
		List<String> connectedIPs = new ArrayList<>();
		
		for(int i = 1; i <= 254; i++) {
			String ip = subnet + "." + i;
			if(InetAddress.getByName(ip).isReachable(100)) {
				connectedIPs.add(ip);

			}
		}
	    // Add ARP table entries
	    List<String> arpEntries = parseArpTable();
	    for (String entry : arpEntries) {
	        System.out.println("ARP: " + entry);
	    }
	    
	    return arpEntries;
		
	}
	private static List<String> parseArpTable() {
	    List<String> result = new ArrayList<>();
	    try {
	        Process p = Runtime.getRuntime().exec("arp -a");
	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                    int start = line.indexOf("(");
	                    int end = line.indexOf(")");
	                    if (start != -1 && end != -1) {
	                        String ip = line.substring(start + 1, end);
	                        result.add(ip);
	                    }
	                
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}



}
