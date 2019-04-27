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
	private boolean useIV;

	public genericBlockCipher(ciphersuiteConfig ciphersuiteConfig, boolean useIV) {
		super();
		this.useIV = useIV;
		this.key = ciphersuiteConfig.getSessionKey();
		method = ciphersuiteConfig.getMethod();
		mode = ciphersuiteConfig.getMode();
		padding = ciphersuiteConfig.getPadding();
	}

	public byte[] encrypt(byte[] input, byte[] iv) throws InvalidKeyException, ShortBufferException,
			IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, InvalidAlgorithmParameterException {

		byte[] keyBytes = key.getEncoded();
		SecretKeySpec key = new SecretKeySpec(keyBytes, method);
		Cipher cipher = Cipher.getInstance(method + "/" + mode + "/" + padding, "SunJCE");
		byte[] cipherText = null;
		int ctLength = 0;

		// encryption
		if (useIV) {
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
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

	public byte[] decrypt(byte[] encryptedMessage, int plaintextLength, byte[] iv) throws ShortBufferException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		byte[] keyBytes = key.getEncoded();

		SecretKeySpec key = new SecretKeySpec(keyBytes, method);
		Cipher cipher = Cipher.getInstance(method + "/" + mode + "/" + padding, "SunJCE");

		if (useIV) {
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		} else {
			cipher.init(Cipher.DECRYPT_MODE, key);
		}

		// decryption

		byte[] buf = new byte[cipher.getOutputSize(encryptedMessage.length)];
		int ptLength = cipher.update(encryptedMessage, 0, encryptedMessage.length, buf, 0);
		ptLength += cipher.doFinal(buf, ptLength);

		byte[] plainText = new byte[ptLength - iv.length];
		System.arraycopy(buf, iv.length, plainText, 0, plainText.length);
		byte[] shortMessage = Arrays.copyOfRange(plainText, 0, plainText.length);
		return shortMessage;
	}
}
