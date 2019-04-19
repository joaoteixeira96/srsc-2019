package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class secDatagramSocket extends DatagramSocket {

	private ciphersuiteConfig ciphersuite;
	private SocketAddress socketAddress;

	public secDatagramSocket(SocketAddress socketAddress) throws SocketException {
		ciphersuite = new ciphersuiteConfig();
		this.socketAddress = socketAddress;
	}

	public secDatagramSocket() throws SocketException {
		super();
		ciphersuite = new ciphersuiteConfig();

	}

	public void send(DatagramPacket datagram) throws IOException {
		Random random = new Random();
		byte[] id = BytesUtils.long2byte(1234567L);
		byte[] nonce = BytesUtils.long2byte(9915469L);
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		array.write(id);
		array.write(nonce);
		array.write(datagram.getData());
		byte[] message = array.toByteArray();
		byte[] mac;
		try {
			mac = createHashFromByteArray(message);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("hash failed");
		}
		array.write(mac);
		byte[] finalMessage = array.toByteArray();
		datagram.setData(finalMessage, 0, finalMessage.length);
		DatagramSocket s = new DatagramSocket();
		s.send(datagram);
	}

	private byte[] createHeader(byte versionRelease, byte payloadType, byte[] playloadSize) throws IOException {
		ByteArrayOutputStream header = new ByteArrayOutputStream();
		byte[] seperator = new byte[] { 0x00 };
		header.write(versionRelease);
		header.write(seperator);
		header.write(payloadType);
		header.write(seperator);
		header.write(playloadSize);
		header.close();
		return header.toByteArray();
	}

	private byte[] createPayload(byte[] message, int size) throws IOException, IllegalBlockSizeException,
			BadPaddingException, ShortBufferException, NoSuchAlgorithmException, NoSuchPaddingException {

		try (ByteArrayOutputStream messageOutputStream = new ByteArrayOutputStream()) {

			byte[] enctryptedMessage = encryptMessageWithGivenKey(ciphersuite.getSessionKey(),
					messageWithCheckSumAppended(message));

			messageOutputStream.write(enctryptedMessage);
			messageOutputStream.write(createFastControlHashFromByteArray(enctryptedMessage));

			return messageOutputStream.toByteArray();

		} catch (Exception e) {

			new Exception("Cannot create payload");

		}
		return null;

	}

	private byte[] messageWithCheckSumAppended(byte[] message) throws IOException {
		try (ByteArrayOutputStream messageOutputStream = new ByteArrayOutputStream()) {

			messageOutputStream.write(message);
			messageOutputStream.write(createHashFromByteArray(message));

			return messageOutputStream.toByteArray();

		} catch (Exception e) {

			new Exception("Cannot create array of message with checksum appended");

		}
		return null;
	}

	private byte[] createFastControlHashFromByteArray(byte[] message) throws Exception {
		return hashMessage(message, ciphersuite.getMacKAKey());
	}

	private byte[] createHashFromByteArray(byte[] message) throws Exception {
		return hashMessage(message, ciphersuite.getMacKMKey());
	}

	private byte[] hashMessage(byte[] message, SecretKeySpec key) throws Exception {
		try {
			Mac mac = Mac.getInstance(ciphersuite.getMACKM());
			AlgorithmParameterSpec params = new IvParameterSpec(ciphersuite.getIV().getBytes());
			mac.init(key, params);
			mac.update(ciphersuite.getIV().getBytes());
			return mac.doFinal(message);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Hash failed");
			throw new Exception("Hash failed");
		}
	}

	private byte[] encryptMessageWithGivenKey(Key key, byte[] message) throws IllegalBlockSizeException,
			BadPaddingException, ShortBufferException, NoSuchAlgorithmException, NoSuchPaddingException {

		SecureRandom random = new SecureRandom();
		IvParameterSpec ivSpec = Utils.createCtrIvForAES(1, random);
//		Key key = Utils.createKeyForAES(256, random);
		Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
		Mac hMac = Mac.getInstance("HMacSHA256");
		Key hMacKey = new SecretKeySpec(key.getEncoded(), "HMacSHA256");
		String messageToString = new String(message);

		// byte[] cipherText = new byte[cipher.getOutputSize(input.length() +
		// hMac.getMacLength())];
		byte[] cipherText = cipher.doFinal(Utils.toByteArray(messageToString));

		int ctLength = cipher.update(Utils.toByteArray(messageToString), 0, messageToString.length(), cipherText, 0);

		// hMac.init(hMacKey);
		// hMac.update(Utils.toByteArray(input));

		ctLength += cipher.doFinal(hMac.doFinal(), 0, hMac.getMacLength(), cipherText, ctLength);

		return cipherText;
	}

	public void receive(DatagramPacket datagram) throws IOException {
		DatagramSocket inSocket = new DatagramSocket(socketAddress);
		byte[] buffer = new byte[4 * 1024];
		DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
		inSocket.receive(inPacket);
		byte[] message = inPacket.getData();

		byte[] id2 = new byte[8];
		System.arraycopy(message, 0, id2, 0, 8);
		byte[] nonce2 = new byte[8];
		System.arraycopy(message, 8, nonce2, 0, 8);
		System.out.println("ID: " + BytesUtils.byte2long(id2));
		System.out.println("Nonce: " + BytesUtils.byte2long(nonce2));
		byte[] message2 = new byte[message.length - 48];
		System.arraycopy(message, 16, message2, 0, 20);
		System.out.println("Sent: " + new String(message2));
		inSocket.close();
		datagram.setData(message2);
	}

}
