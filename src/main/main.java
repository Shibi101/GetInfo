package main;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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

import javax.swing.JFrame;
import javax.swing.JLabel;

public class main {

	private static final boolean INCLUDE_LINK_LOCAL = true;
	private static final boolean INCLUDE_LOOPBACK = true;
	
	public static void main(String[] args) throws UnknownHostException, SocketException {
		// Waar wordt deze voor gebruikt ??
//		InetAddress IAddress = InetAddress.getLocalHost();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		for(NetworkInterface netint : Collections.list(interfaces)) {
			System.out.println(netint.getDisplayName());
		}
		
		//make string to store local-adress which reaches 8.8.8.8
		String ipadres;
		
		//create socket which connects to 8.8.8.8 (google)
		try(final DatagramSocket socket = new DatagramSocket()){
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			ipadres = socket.getLocalAddress().getHostAddress();
		}
		
		JFrame frame = new JFrame("IP adress");
		JLabel OsName = new JLabel(("OS name: " + System.getProperty("os.name")));
		JLabel OsVersion = new JLabel(("OS version: " + System.getProperty("os.version")));
		JLabel OsArch = new JLabel(("OS architecture: " + System.getProperty("os.arch")));
		frame.setLayout(new GridLayout(5,2));
		List<String> ipAddresses = getIpAddresses(!INCLUDE_LINK_LOCAL, !INCLUDE_LOOPBACK);
		ipAddresses.forEach(System.out::println);
		
		InetAddress myIP = InetAddress.getLocalHost();
		System.out.println(myIP.getHostAddress());
		// waar wordt deze voor gebruikt ??
//		String Ip = ExtractIp(ipAddresses);
		JLabel IP = new JLabel("local IP: " + ipadres);
		
		frame.add(OsName);
		frame.add(OsVersion);
		frame.add(OsArch);
		frame.add(IP);
		frame.setSize(600,400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}
	
	private static List<String> getIpAddresses(boolean includeLinkLocal, boolean includeLoopback)throws SocketException {
		List<String> ipAddresses = new ArrayList<>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
//			kan cleaner
//			for(InterfaceAddress interfaceAddress : interfaces.nextElement().getInterfaceAddresses()) {
//				InetAddress addr = interfaceAddress.getAddress();
//				boolean include = true;
//				if(addr.isLinkLocalAddress() && !includeLinkLocal) {
//					include = false;
//				}
//				if(addr.isLoopbackAddress() && ! includeLoopback) {
//					include = false;
//				}
//				if(include) {
//					ipAddresses.add(addr.toString().substring(1));
//				}
//			}
			
			NetworkInterface netint = interfaces.nextElement();
			for (InterfaceAddress interfaceAddress : netint.getInterfaceAddresses()) {
				InetAddress addr = interfaceAddress.getAddress();
				if (addr.isLinkLocalAddress() && !includeLinkLocal) continue;
				if (addr.isLoopbackAddress() && !includeLoopback) continue;
				ipAddresses.add(addr.getHostAddress());
			}
		}
		return ipAddresses;
	}
	
	// is deze wel gebruikt ??
//	public static String ExtractIp(List<String> ipAddresses) {
//		for(String ip : ipAddresses) {
//			return ip;
//		}
//		return null;
//		
//	}
}