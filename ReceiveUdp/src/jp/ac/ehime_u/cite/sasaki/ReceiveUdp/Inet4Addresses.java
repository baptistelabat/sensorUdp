package jp.ac.ehime_u.cite.sasaki.ReceiveUdp;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.net.InterfaceAddress;
import java.util.List;

public class Inet4Addresses {

	private ArrayList<InetAddress> unicastInetAddresses;
	private ArrayList<InetAddress> broadcastInetAddresses;

	public Inet4Addresses() throws SocketException {
		unicastInetAddresses = new ArrayList<InetAddress>();
		broadcastInetAddresses = new ArrayList<InetAddress>();

		java.util.Enumeration<NetworkInterface> enumeration_network_interfaces = NetworkInterface
				.getNetworkInterfaces();
		if (null != enumeration_network_interfaces) {
			while (enumeration_network_interfaces.hasMoreElements()) {
				NetworkInterface network_interface = enumeration_network_interfaces
						.nextElement();
				List<InterfaceAddress> interface_addresses = network_interface
						.getInterfaceAddresses();
				Iterator<InterfaceAddress> interface_addresses_iterator = interface_addresses
						.iterator();
				while (interface_addresses_iterator.hasNext()) {
					InterfaceAddress interface_address = interface_addresses_iterator
							.next();
					InetAddress unicast_inet_address = interface_address
							.getAddress();
					if (null != unicast_inet_address) {
						String unicast_address_string = unicast_inet_address
								.getHostAddress();
						if (unicast_address_string.indexOf(":") == -1) {
							unicastInetAddresses.add(unicast_inet_address);
						}
					}
					InetAddress broadcast_inet_address = interface_address
							.getBroadcast();
					if (null != broadcast_inet_address) {
						String broadcast_address_string = broadcast_inet_address
								.getHostAddress();
						if (broadcast_address_string.indexOf(":") == -1) {
							broadcastInetAddresses.add(broadcast_inet_address);
						}
					}
				}
			}
		}
	}

	public ArrayList<InetAddress> getUnicastInetAddresses() {
		return unicastInetAddresses;
	}

	public ArrayList<InetAddress> getBroadcastInetAddresses() {
		return broadcastInetAddresses;
	}
	
	public ArrayList<String> getBroadcastAddresses(){
		ArrayList<String> array_list = new ArrayList<String>();
		Iterator<InetAddress> broadcast_inet_address_iterator = getBroadcastInetAddresses().iterator();
		while(broadcast_inet_address_iterator.hasNext()){
			array_list.add(broadcast_inet_address_iterator.next().getHostAddress());
		}
		return array_list;
	}

	public static void main(String[] args) throws SocketException {
		Inet4Addresses inet_addresses = new Inet4Addresses();
		Iterator<InetAddress> unicode_addresses_iterator = inet_addresses
				.getUnicastInetAddresses().iterator();
		while (unicode_addresses_iterator.hasNext()) {
			System.out.println("unicast "
					+ unicode_addresses_iterator.next().getHostAddress());
		}
		Iterator<InetAddress> broadcast_addresses_iterator = inet_addresses
				.getBroadcastInetAddresses().iterator();
		while (broadcast_addresses_iterator.hasNext()) {
			System.out.println("broadcast "
					+ broadcast_addresses_iterator.next().getHostAddress());
		}
	}
	
}
