package utils;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

public class Test {
	public static void main(String[] args) throws Exception {
		// System.out.println(secDatagramSockets.ciphersuitReader());
		ciphersuiteConfig cs = new ciphersuiteConfig();
		System.out.println(cs.toString());
		secDatagramSockets sec = new secDatagramSockets();
		byte[] buff = new byte[65000];
		InetSocketAddress addr = new InetSocketAddress("localhost", 1233);
		DatagramPacket p = new DatagramPacket(buff, buff.length, addr);
		String data = "asdasdasdasdasdasdsa";
		p.setData(data.getBytes(), 0, data.getBytes().length);
		sec.send(p);
	}
}
