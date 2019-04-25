package utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import message.Header;
import message.Payload;

public class secDatagramSocket extends DatagramSocket {

	private ciphersuiteConfig ciphersuite;
	private Payload payload;
	private Header header;

	public secDatagramSocket(SocketAddress socketAddress) throws SocketException {
		super(socketAddress);
		ciphersuite = new ciphersuiteConfig();
		payload = new Payload(123456, 9999L, ciphersuite);
		byte[] version = { 0x11 };
		byte[] payloadType = { 0x01 };
		header = new Header(version, payloadType);
	}

	public secDatagramSocket() throws SocketException {
		super();
		ciphersuite = new ciphersuiteConfig();
		payload = new Payload(123456, 9999L, ciphersuite);
		byte[] version = { 0x11 };
		byte[] payloadType = { 0x01 };
		header = new Header(version, payloadType);
	}

	public void send(DatagramPacket datagram) throws IOException {
		byte[] finalMessagePayload = payload
				.createPayload(messageWithoutGarbage(datagram.getData(), datagram.getLength()));
		byte[] macDoS = new byte[8];
		datagram.setData(finalMessagePayload);
		super.send(datagram);
	}

	private byte[] messageWithoutGarbage(byte[] message, int length) {
		return Arrays.copyOf(message, length);
	}

	public void receive(DatagramPacket datagram) throws IOException {
		super.receive(datagram);
		try {
			byte[] processPayload = payload
					.processPayload(messageWithoutGarbage(datagram.getData(), datagram.getLength()));
			datagram.setData(processPayload);
		} catch (Exception e) {
			e.printStackTrace();
			datagram.setData(new byte[0]);
		}
	}

}
