package message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import utils.BytesUtils;
import utils.ciphersuiteConfig;
import utils.genericBlockCipher;
import utils.genericMAC;

public class Payload {

	private long id;
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

	private byte[] messageToApplyMac(byte[] message) throws IOException {
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		array.write(getID());
		array.write(getNonce());
		array.write(message);
		return array.toByteArray();
	}

	public byte[] createPayload(byte[] message) throws IOException {
		try {
			byte[] finalMessage = genericBlockCipher
					.encrypt(genericMac.generateMessageWithMacAppended(messageToApplyMac(message)));
			nonce++;
			return finalMessage;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("send failed");
		}
	}

	public boolean checkID(long id) {
		return true; // TODO
	}

	public boolean checkNonce(long nonce) {
		return true; // TODO
	}

	public byte[] processPayload(byte[] message, int messageLength)
			throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IOException {
		genericBlockCipher.decrypt(message);
		byte[] idReceived = new byte[8];
		System.arraycopy(message, 0, id, 0, 8);
		if (!checkID(BytesUtils.byte2long(idReceived)))
			return new byte[1];
		byte[] nonceReceived = new byte[8];
		System.arraycopy(message, 8, nonceReceived, 0, 8);
		if (!checkNonce(BytesUtils.byte2long(nonceReceived)))
			return new byte[1];

		System.out.println("TamanhoMensagem: " + message.length);
		byte[] messageToHash = new byte[messageLength + 16];
		System.arraycopy(message, 0, messageToHash, 0, 16 + messageLength);
		byte[] mac = new byte[16];
		System.arraycopy(message, 16 + messageLength, mac, 0, 16);
		if (!genericMac.confirmKMac(messageToHash, mac))
			return new byte[8];

		byte[] finalMessage = new byte[messageLength];
		System.arraycopy(message, 16, finalMessage, 0, messageLength);
		return finalMessage;
	}

}
