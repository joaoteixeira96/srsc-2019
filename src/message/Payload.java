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

import utils.BytesUtils;
import utils.Utils;
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

	public byte[] processPayload(byte[] message) throws InvalidKeyException, ShortBufferException,
			IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {
//		genericBlockCipher.decrypt(message, length);
		int idSize = getID().length;
		byte[] idReceived = new byte[idSize];
		System.arraycopy(message, 0, idReceived, 0, idSize);
		if (!checkID(BytesUtils.byte2long(idReceived)))
			return new byte[1];
		int nonceSize = getNonce().length;
		byte[] nonceReceived = new byte[nonceSize];
		System.arraycopy(message, 8, nonceReceived, 0, nonceSize);
		System.out.println(BytesUtils.byte2long(nonceReceived));
		if (!checkNonce(BytesUtils.byte2long(nonceReceived)))
			return new byte[1];

//		System.out.println("TamanhoMensagem: " + message.length);
		// TODO get correct mac size with getMacLength()
		byte[] mac = new byte[16];
		System.arraycopy(message, message.length - mac.length, mac, 0, mac.length);
		int messageSize = message.length - idSize - nonceSize - mac.length;
		byte[] messageToHash = new byte[messageSize + idSize + nonceSize];
		System.arraycopy(message, 0, messageToHash, 0, messageToHash.length);
		byte[] finalMessage = new byte[messageSize];
		System.arraycopy(message, idSize + nonceSize, finalMessage, 0, messageSize);
		System.out.println("processPayload: " + Utils.toHex(mac));
		if (!genericMac.confirmKMac(messageToHash, mac))
			return new byte[8];

		return finalMessage;
	}

}
