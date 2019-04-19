package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
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

	public secDatagramSocket() throws SocketException {
		super();
		ciphersuite = new ciphersuiteConfig();
	}

	public void send(DatagramPacket datagram) throws IOException {
		Random random = new Random();
		long id = 78151919;
		long nonce = 9915469;
		String message = id + nonce + new String(datagram.getData());
		String mac = "";
		try {
			mac = new String(createHashFromByteArray(message.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("hash failed");
		}
		String send = message + mac;
		byte[] finalMessage = send.getBytes();
		String received = new String(send.getBytes());
		System.out.println("Sent: " + send);
		datagram.setData(finalMessage, 0, finalMessage.length);
		DatagramSocket s = new DatagramSocket();
		InetSocketAddress addr2 = new InetSocketAddress("localhost", 1236);
		DatagramSocket inSocket = new DatagramSocket(addr2);
		byte[] buf = new byte[finalMessage.length];
		DatagramPacket in = new DatagramPacket(buf, buf.length);
		s.send(datagram);
		inSocket.receive(in);
		received = new String(in.getData());
		System.out.println("Received: " + received);
		System.out.println("Equal: " + received.equals(send));

		inSocket.close();
		s.close();
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

	public DatagramPacket receive() {
		return null;
	}

}
