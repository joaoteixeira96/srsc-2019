package utils;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

public class TestReceive {
	public static void main(String[] args) throws Exception {
		// System.out.println(secDatagramSockets.ciphersuitReader());
		InetSocketAddress addr = new InetSocketAddress("localhost", 1236);
		secDatagramSocket sec = new secDatagramSocket(addr);
		byte[] buffer = new byte[65000];
		DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
		sec.receive(inPacket);
		System.out.println(new String(inPacket.getData()));
		/*
		 * If listen a remote unicast server uncomment the following line
		 */
//		long x = 1234567L;
//		System.out.println(BytesUtils.byte2long(BytesUtils.long2byte(x)));

	}
}
