package utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class genericBlockCipher {

	private SecretKeySpec key;
	private String method;
	private String mode;
	private String padding;

	public genericBlockCipher(ciphersuiteConfig ciphersuite) {
		super();
		this.key = ciphersuite.getSessionKey();
		this.method = ciphersuite.getAlg();
		this.mode = ciphersuite.getMode();
		this.padding = ciphersuite.getPadding();
	}

	public byte[] encrypt(byte[] input) throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {

		byte[] keyBytes = key.getEncoded();

		SecretKeySpec key = new SecretKeySpec(keyBytes, method);
		Cipher cipher = Cipher.getInstance(method + "/" + mode + "/" + padding, "SunJCE");

		System.out.println("key   : " + Utils.toHex(keyBytes));
		System.out.println("input : " + Utils.toHex(input));
		// encryption
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
//		int ctLength = 0;
		int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
		ctLength += cipher.doFinal(cipherText, ctLength);

		System.out.println("cipher: " + Utils.toHex(cipherText, ctLength) + " bytes: " + ctLength);
		System.out.println("inputPlain: " + Utils.toHex(input));

		return cipherText;

	}

	public byte[] decrypt(byte[] encryptedMessage)
			throws ShortBufferException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
		System.out.println(Utils.toHex(encryptedMessage));
		byte[] keyBytes = key.getEncoded();

		SecretKeySpec key = new SecretKeySpec(keyBytes, method);
		Cipher cipher = Cipher.getInstance(method + "/" + mode + "/" + padding, "SunJCE");

		System.out.println("key   : " + Utils.toHex(keyBytes));

		// decryption
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] plainText = new byte[cipher.getOutputSize(encryptedMessage.length)];
		int ptLength = cipher.update(encryptedMessage, 0, encryptedMessage.length, plainText, 0);
		ptLength += cipher.doFinal(encryptedMessage, ptLength);

		System.out.println("plain : " + Utils.toHex(plainText, ptLength) + " bytes: " + ptLength);
		System.out.println("plaintext: " + new String(plainText));

		return plainText;
	}
}
