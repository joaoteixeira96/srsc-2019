package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import message.Header;
import message.Payload;

public class secDatagramSocket extends DatagramSocket {

	private static final int ID = (int) (Math.random() + System.currentTimeMillis());
	private static final long NONCE = (long) (Math.random() + System.currentTimeMillis());
	private static final int HEADER_SIZE = 6;
	private static final byte[] VERSION = { 0x11 };
	private static final byte[] PLAYLOAD_TYPE = { 0x01 };
	private static final int DEFAULT_ID = -1;
	private static final long DEFAULT_LONG = -1L;

	private ciphersuiteConfig ciphersuite;
	private Payload payload;
	private Header header;

	public secDatagramSocket(SocketAddress socketAddress) throws SocketException {
		super(socketAddress);
		ciphersuite = new ciphersuiteConfig();
		payload = new Payload(DEFAULT_ID, DEFAULT_LONG, ciphersuite);
		header = new Header(VERSION, PLAYLOAD_TYPE);
	}

	public secDatagramSocket() throws SocketException {
		super();
		ciphersuite = new ciphersuiteConfig();
		payload = new Payload(ID, NONCE, ciphersuite);
		byte[] version = { 0x11 };
		byte[] payloadType = { 0x01 };
		header = new Header(version, payloadType);
	}

	public void send(DatagramPacket datagram) throws IOException {
		byte[] messageWihoutGargabe = messageWithoutGarbage(datagram.getData(), datagram.getLength());
		byte[] Header = header.generateHeader(messageWihoutGargabe);
		byte[] Payload = payload.createPayload(messageWihoutGargabe);
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		array.write(Header);
		array.write(Payload);
		datagram.setData(array.toByteArray());
		super.send(datagram);
	}

	private byte[] messageWithoutGarbage(byte[] message, int length) {
		return Arrays.copyOf(message, length);
	}

	private byte[] getHeaderFromMessage(byte[] message, int length) {
		return Arrays.copyOf(message, length);
	}

	public void receive(DatagramPacket datagram) throws IOException {
		super.receive(datagram);
		byte[] messageWithoutGargabe = messageWithoutGarbage(datagram.getData(), datagram.getLength());
		byte[] header = getHeaderFromMessage(messageWithoutGargabe, HEADER_SIZE); // TODO MAGIC NUMBER
		byte[] cipherMessage = Arrays.copyOfRange(messageWithoutGargabe, header.length, messageWithoutGargabe.length);
		try {
			byte[] processPayload = payload.processPayload(cipherMessage);
			datagram.setData(processPayload);
		} catch (Exception e) {
			e.printStackTrace();
			datagram.setData(new byte[0]);
		}
	}

}
