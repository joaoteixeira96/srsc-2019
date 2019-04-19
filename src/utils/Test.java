package utils;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

public class Test {
	public static void main(String[] args) throws Exception {
		// System.out.println(secDatagramSockets.ciphersuitReader());
		ciphersuiteConfig cs = new ciphersuiteConfig();
		System.out.println(cs.toString());
		secDatagramSocket sec = new secDatagramSocket();
		byte[] buff = new byte[65000];
		InetSocketAddress addr = new InetSocketAddress("localhost", 1236);
		DatagramPacket p = new DatagramPacket(buff, buff.length, addr);
		String data = "asdasdasdasdasdasdsa";
		p.setData(data.getBytes(), 0, data.getBytes().length);
		byte[] buf = new byte[4 * 300];
		DatagramPacket in = new DatagramPacket(buf, buf.length);
		sec.send(p);
		/*
		 * If listen a remote unicast server uncomment the following line
		 */

	}
}
