package message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import utils.ciphersuiteConfig;
import utils.genericBlockCipher;
import utils.genericMAC;

public class Payload {

	private final long id;
	private long nonce;
	private genericBlockCipher genericBlockCipher;
	private genericMAC genericMac;

	public Payload(long id, long nonce, ciphersuiteConfig ciphersuite) {
		super();
		this.id = id;
		this.nonce = nonce;
		this.genericMac = new genericMAC(ciphersuite);
		this.genericBlockCipher = new genericBlockCipher(ciphersuite);
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
//		System.out.println("ID :" + Utils.toHex(getID()));
//		System.out.println("Nonce: " + Utils.toHex(getNonce()));
//		System.out.println("M: " + Utils.toHex(message));
		return array.toByteArray();
	}

	public byte[] createPayload(byte[] message) throws IOException {
		try {
			byte[] finalMessage = genericBlockCipher
					.encrypt(genericMac.generateMessageWithMacAppended(appendIdNonceMessage(message)));
			//
			// genericMac.generateMessageWithMacAppended(messageToApplyMac(
			return finalMessage;
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
		return Arrays.copyOfRange(message, 0, message.length - 16); // TODO: Change Magic Number
	}

	private boolean WrongID(byte[] message) {
		try {
			return !checkID(BytesUtils.byte2long(Arrays.copyOfRange(message, 0, getID().length)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} // TODO: Change Magic
			// Number
	}

	private boolean WrongNonce(byte[] message) {
		try {
			return !checkNonce(BytesUtils
					.byte2long(Arrays.copyOfRange(message, getID().length, getID().length + getNonce().length)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} // TODO:
			// Change
			// Magic
			// Number
	}

	private byte[] getMac(byte[] message) {
		return Arrays.copyOfRange(message, message.length - 16, message.length); // TODO: Change Magic Number
	}

	private byte[] getMessage(byte[] message) {
		int startIndex, endIndex;
		try {
			startIndex = getID().length + getNonce().length;
			endIndex = message.length - 16; // TODO: Change Magic number
			return Arrays.copyOfRange(message, startIndex, endIndex);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new byte[0];
		}
	}

	public byte[] processPayload(byte[] message) throws InvalidKeyException, ShortBufferException,
			IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {

		if (WrongID(message))
			return new byte[0];

		if (WrongNonce(message))
			return new byte[0];

		if (!genericMac.confirmKMac(getMessageToHash(message), getMac(message)))
			return new byte[0];

		return getMessage(message);
	}

}
