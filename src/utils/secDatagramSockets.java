package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class secDatagramSockets extends DatagramSocket {
	int key = 0x0;
	Key sharedKey; // TODO: Mudar para shared key
	Key fastControlMACKey; // TODO: Mudar para shared key
	Key MAC_Key;

	public secDatagramSockets() throws SocketException {
		super();
		key = 0x00;
		sharedKey = new SecretKeySpec(new byte[2], "AES"); // TODO: Mudar para shared key
		fastControlMACKey = new SecretKeySpec(new byte[2], "AES"); // TODO: Mudar para shared key
		MAC_Key = new SecretKeySpec(new byte[2], "AES");
		// TODO Auto-generated constructor stub
	}

	public void send(DatagramPacket datagram) {
		// Pegar mensagem
		// String message = encrypt();
		// Encriptar
		// Enviar

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

			byte[] enctryptedMessage = encryptMessageWithGivenKey(sharedKey, messageWithCheckSumAppended(message));

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

	private byte[] createFastControlHashFromByteArray(byte[] message) {
		return hashMessage(message, fastControlMACKey);
	}

	private byte[] createHashFromByteArray(byte[] message) {
		return hashMessage(message, MAC_Key);
	}

	private byte[] hashMessage(byte[] message, Key key) {
		return null;
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
