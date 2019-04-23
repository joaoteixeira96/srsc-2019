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
	private int messageLength;

	public secDatagramSocket(SocketAddress socketAddress) throws SocketException {
		super(socketAddress);
		ciphersuite = new ciphersuiteConfig();
		payload = new Payload(123456L, 9999L, ciphersuite);
		byte[] version = { 0x11 };
		byte[] payloadType = { 0x01 };
		header = new Header(version, payloadType);
	}

	public secDatagramSocket() throws SocketException {
		super();
		ciphersuite = new ciphersuiteConfig();
		payload = new Payload(123456L, 9999L, ciphersuite);
		byte[] version = { 0x11 };
		byte[] payloadType = { 0x01 };
		header = new Header(version, payloadType);
	}

	public void send(DatagramPacket datagram) throws IOException {
		byte[] fullMessage = datagram.getData();
		byte[] shortMessage = Arrays.copyOfRange(fullMessage, 0, datagram.getLength());

//		System.out.println("message size: " + message.length);
//		System.out.println("data size: " + datagram.getData().length);
		byte[] finalMessagePayload = payload.createPayload(shortMessage);
//		byte[] finalMessageHeader = header.generateHeader(shortMessage);
//		byte[] finalMessage = new byte[finalMessageHeader.length + finalMessagePayload.length];
//		System.arraycopy(finalMessageHeader, 0, finalMessage, 0, finalMessageHeader.length);
//		System.arraycopy(finalMessagePayload, 0, finalMessage, finalMessageHeader.length, finalMessagePayload.length);
//		datagram.setData(finalMessagePayload, 0, finalMessagePayload.length);
		datagram.setData(finalMessagePayload);
		messageLength = finalMessagePayload.length;
		super.send(datagram);
	}

	public void receive(DatagramPacket datagram) throws IOException {
		super.receive(datagram);

		byte[] fullMessage = datagram.getData();
		byte[] shortMessage = Arrays.copyOfRange(fullMessage, 0, datagram.getLength());
		// header = new byte[6],payload = new byte[shortMessage.length - 6];
		try {
//			System.arraycopy(shortMessage, 0, header, 0, 6);
//			System.arraycopy(shortMessage, 6, payload, 0, shortMessage.length - 6);
			// shortMessage = this.payload.processPayload(payload,
			// this.header.getMessageLength(header));
//			int plaintTextLength = this.header.getMessageLength(header);
//			System.out.println("plaintTextLength: " + plaintTextLength);
			genericBlockCipher genericBlockCipher = new genericBlockCipher(ciphersuite);
			byte[] decryptedMessage = genericBlockCipher.decrypt(shortMessage, messageLength);
//			byte[] finalMessage = new byte[plaintTextLength];
//			System.arraycopy(decryptedMessage, 0, finalMessage, 0, plaintTextLength);
			datagram.setData(decryptedMessage);
			// genericBlockCipher.decrypt(
		} catch (Exception e) {
			e.printStackTrace();
			datagram.setData(new byte[0]);
		}
	}

}
