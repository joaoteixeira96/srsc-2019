package utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

import org.bouncycastle.util.Arrays;

import message.Header;
import message.Payload;

public class secDatagramSocket extends DatagramSocket {

	private ciphersuiteConfig ciphersuite;
	private SocketAddress socketAddress;
	private Payload payload;
	private Header header;

	public secDatagramSocket(SocketAddress socketAddress) throws SocketException {
		ciphersuite = new ciphersuiteConfig();
		this.socketAddress = socketAddress;
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
		byte[] finalMessageHeader = header.generateHeader(shortMessage);
		byte[] finalMessage = new byte[finalMessageHeader.length + finalMessagePayload.length];
		System.arraycopy(finalMessageHeader, 0, finalMessage, 0, finalMessageHeader.length);
		System.arraycopy(finalMessagePayload, 0, finalMessage, finalMessageHeader.length, finalMessagePayload.length);
		datagram.setData(finalMessage, 0, finalMessage.length);
		DatagramSocket s = new DatagramSocket();
		s.send(datagram);
	}

	public void receive(DatagramPacket datagram) throws IOException {
		DatagramSocket inSocket = new DatagramSocket(socketAddress);
		byte[] buffer = new byte[datagram.getLength()];
		DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
		inSocket.receive(inPacket);
		byte[] message = datagram.getData(), header = new byte[6], payload = new byte[message.length - 6];
		try {
			message = inPacket.getData();
			System.arraycopy(message, 0, header, 0, 6);
			System.arraycopy(message, 6, payload, 0, message.length - 6);
			message = this.payload.processPayload(payload, this.header.getMessageLength(header));
			datagram.setData(message);
		} catch (Exception e) {
			e.printStackTrace();
			datagram.setData(new byte[0]);
		}
	}

}
