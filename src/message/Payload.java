package message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.bouncycastle.util.Arrays;

import utils.BytesUtils;
import utils.IvGenerator;
import utils.ciphersuiteConfig;
import utils.genericBlockCipher;
import utils.genericMAC;

public class Payload {

	private static final String KA = "KA";
	private static final String KM = "KM";
	private long id;
	private long nonce;
	private genericBlockCipher genericBlockCipher;
	private genericMAC genericMac;
	private ciphersuiteConfig ciphersuite;
	private boolean cipherUsesIV;

	public Payload(long id, long nonce, ciphersuiteConfig ciphersuite) {
		super();
		this.id = id;
		this.nonce = nonce;
		this.ciphersuite = ciphersuite;
		this.cipherUsesIV = IvGenerator.needsIV(ciphersuite.getMode());
		this.genericMac = new genericMAC(ciphersuite, IvGenerator.needsIV(ciphersuite.getMACKA()),
				IvGenerator.needsIV(ciphersuite.getMACKM()));
		this.genericBlockCipher = new genericBlockCipher(ciphersuite, cipherUsesIV);

	}

	private byte[] getID() throws IOException {
		return BytesUtils.long2byte(id);
	}

	private byte[] getNonce() throws IOException {
		return BytesUtils.long2byte(nonce);
	}

	private byte[] appendIdNonceMessage(byte[] message) throws IOException {
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		nonce++;
		array.write(getID());
		array.write(getNonce());
		array.write(message);
		return array.toByteArray();
	}

	public byte[] createPayload(byte[] message) throws IOException {
		try {
			byte[] IV = new byte[0];
			if (cipherUsesIV) {

				IV = IvGenerator.generateIV(ciphersuite.getMethod());
			}
			byte[] finalMessage = genericBlockCipher
					.encrypt(genericMac.generateMessageWithMacAppended(appendIdNonceMessage(message), KM), IV);
			ByteBuffer buf = ByteBuffer.allocate(finalMessage.length + IV.length);
			buf.put(finalMessage);
			buf.put(IV);
			byte[] messageWithIV = buf.array();
			byte[] messageWithDOS = genericMac.generateMessageWithMacAppended(messageWithIV, KA);
			return messageWithDOS;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("send failed");
		}
	}

	public boolean checkID(long id) {
		return id == this.id;
	}

	public boolean checkNonce(long nonce) {
		if (nonce <= this.nonce)
			return false;
		this.nonce = nonce;
		return true;
	}

	private byte[] getMessageToHash(byte[] message) {
		return Arrays.copyOfRange(message, 0, message.length - genericMac.macKMSize()); // TODO: Change Magic Number
	}

	private boolean WrongID(byte[] message) {
		try {
			long messageId = BytesUtils.byte2long(Arrays.copyOf(message, getID().length));
			if (id == -1) {
				id = messageId;
				return true;
			}
			return !checkID(messageId);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean WrongNonce(byte[] message) {
		try {
			long messageNonce = BytesUtils
					.byte2long(Arrays.copyOfRange(message, getID().length, getID().length + getNonce().length));
			if (nonce < 0) {
				nonce = messageNonce;
				return true;
			}
			return !checkNonce(messageNonce);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private byte[] getMac(byte[] message) {
		return Arrays.copyOfRange(message, message.length - genericMac.macKMSize(), message.length); // TODO: Change
																										// Magic Number
	}

	private byte[] getMessage(byte[] message) {
		int startIndex, endIndex;
		try {
			startIndex = getID().length + getNonce().length;
			endIndex = message.length - genericMac.macKMSize(); // TODO: Change Magic number
			return Arrays.copyOfRange(message, startIndex, endIndex);
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	private byte[] messageWithoutDOS(byte[] message) {
		return Arrays.copyOf(message, message.length - genericMac.macKASize());
	}

	private byte[] macDOS(byte[] message) {
		return Arrays.copyOfRange(message, message.length - genericMac.macKASize(), message.length);
	}

	public byte[] processPayload(byte[] message) throws InvalidKeyException, ShortBufferException,
			IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {

		byte[] messageWithoutDos = messageWithoutDOS(message);

		if (!genericMac.confirmKMac(messageWithoutDos, macDOS(message), KA))
			return new byte[0];

		byte[] iv = new byte[0];
		if (cipherUsesIV) {
			int ivSize = getIvSize();
			iv = getIV(messageWithoutDos, ivSize);
			messageWithoutDos = removeIv(messageWithoutDos, ivSize);
		}
		byte[] decryptedMessage = genericBlockCipher.decrypt(messageWithoutDos, messageWithoutDos.length, iv);
		if (WrongID(decryptedMessage))
			return new byte[0];

		if (WrongNonce(decryptedMessage))
			return new byte[0];

		if (!genericMac.confirmKMac(getMessageToHash(decryptedMessage), getMac(decryptedMessage), KM))
			return new byte[0];

		return getMessage(decryptedMessage);
	}

	private int getIvSize() {
		return IvGenerator.ivLength(ciphersuite.getMethod());
	}

	private byte[] removeIv(byte[] message, int ivSize) {
		return Arrays.copyOf(message, message.length - ivSize);
	}

	private byte[] getIV(byte[] message, int ivSize) {
		return Arrays.copyOfRange(message, message.length - ivSize, message.length);
	}

}
