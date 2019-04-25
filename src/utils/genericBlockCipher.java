package utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

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
	private String ciphersuite;
	private boolean useIV;

	public genericBlockCipher(ciphersuiteConfig ciphersuiteConfig, boolean useIV) {
		super();
		this.useIV = useIV;
		this.ciphersuite = ciphersuiteConfig.getCiphersuite();
		this.key = ciphersuiteConfig.getSessionKey();
		method = ciphersuite.split("/")[0];
		mode = ciphersuite.split("/")[1];
		padding = ciphersuite.split("/")[2];
	}

	public byte[] encrypt(byte[] input) throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
			InvalidAlgorithmParameterException {

		byte[] keyBytes = key.getEncoded();
		SecretKeySpec key = new SecretKeySpec(keyBytes, method);
		Cipher cipher = Cipher.getInstance(method + "/" + mode + "/" + padding, "SunJCE");
		byte[] cipherText = null;
		int ctLength = 0;

		// encryption
		if (useIV) {
			IvParameterSpec ivSpec = new IvParameterSpec(new byte[IvGenerator.ivLength(method)]);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			byte[] iv = IvGenerator.generateIV(method);
			cipherText = new byte[cipher.getOutputSize(input.length + iv.length)];
			ctLength = cipher.update(iv, 0, iv.length, cipherText, 0);
		} else {
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = new byte[cipher.getOutputSize(input.length)];
		}
		ctLength += cipher.update(input, 0, input.length, cipherText, ctLength);
		ctLength += cipher.doFinal(cipherText, ctLength);

		return cipherText;

	}

	public byte[] decrypt(byte[] encryptedMessage, int plaintextLength) throws ShortBufferException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		byte[] keyBytes = key.getEncoded();

		SecretKeySpec key = new SecretKeySpec(keyBytes, method);
		Cipher cipher = Cipher.getInstance(method + "/" + mode + "/" + padding, "SunJCE");

		int iv = IvGenerator.ivLength(method);
		if (useIV) {
			IvParameterSpec ivSpec = new IvParameterSpec(new byte[iv]);
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		} else {
			cipher.init(Cipher.DECRYPT_MODE, key);
			iv = 0;
		}

		// decryption

		byte[] buf = new byte[cipher.getOutputSize(encryptedMessage.length)];
		int ptLength = cipher.update(encryptedMessage, 0, encryptedMessage.length, buf, 0);
		ptLength += cipher.doFinal(buf, ptLength);

		byte[] plainText = new byte[ptLength - iv];
		System.arraycopy(buf, iv, plainText, 0, plainText.length);
		byte[] shortMessage = Arrays.copyOfRange(plainText, 0, ptLength - iv);
		return shortMessage;
	}
}
