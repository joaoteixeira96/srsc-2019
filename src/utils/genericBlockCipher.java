package utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class genericBlockCipher {

	private SecretKeySpec key;
	private String method;
	private String mode;
	private String padding;
	private String iv;

	public genericBlockCipher(ciphersuiteConfig ciphersuite) {
		super();
		this.key = ciphersuite.getSessionKey();
		this.method = ciphersuite.getAlg();
		this.mode = ciphersuite.getMode();
		this.padding = ciphersuite.getPadding();
		this.iv = ciphersuite.getIV();
	}

	public byte[] encrypt(byte[] input) throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
			InvalidAlgorithmParameterException {

		byte[] keyBytes = key.getEncoded();

		System.out.println("plaintext M in encrypt Method: " + Utils.toHex(input) + "Bytes: " + input.length);

		SecretKeySpec key = new SecretKeySpec(keyBytes, method);
		IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
		Cipher cipher = Cipher.getInstance(method + "/" + mode + "/" + padding, "SunJCE");

//		System.out.println("key   : " + Utils.toHex(keyBytes));
//		System.out.println("input : " + Utils.toHex(input));
		// encryption
		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
		System.out.println(cipher.getOutputSize(input.length));
		int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
		ctLength += cipher.doFinal(cipherText, ctLength);

		System.out.println("cipher M in encrypt Method: " + Utils.toHex(cipherText, ctLength) + " bytes: " + ctLength);

		return cipherText;

	}

	public byte[] decrypt(byte[] encryptedMessage, int plaintextLength) throws ShortBufferException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		System.out.println(
				"Cipher M in decrypt Method: " + Utils.toHex(encryptedMessage) + " bytes: " + encryptedMessage.length);
		byte[] keyBytes = key.getEncoded();

		SecretKeySpec key = new SecretKeySpec(keyBytes, method);
		IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
		Cipher cipher = Cipher.getInstance(method + "/" + mode + "/" + padding, "SunJCE");

//		System.out.println("key   : " + Utils.toHex(keyBytes));

		// decryption
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		byte[] plainText = new byte[cipher.getOutputSize(encryptedMessage.length)];
		int ptLength = cipher.update(encryptedMessage, 0, encryptedMessage.length, plainText, 0);
		ptLength += cipher.doFinal(plainText, ptLength);
		System.out.println("ptLength: " + ptLength);

		System.out
				.println("Plaintext M in decrypt Method: " + Utils.toHex(plainText, ptLength) + " bytes: " + ptLength);
		return plainText;
	}
}
