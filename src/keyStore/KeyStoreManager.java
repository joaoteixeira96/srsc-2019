package keyStore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeyStoreManager {

	private static final char[] PASSWORD = "password".toCharArray();
	private static final String NO_MAC_TYPE = "";

	public static SecretKeySpec getEncryptionKey(String algorithm) throws KeyStoreException, NoSuchAlgorithmException,
			FileNotFoundException, CertificateException, IOException, UnrecoverableKeyException {
		KeyStore keyStore = KeyStore.getInstance("JCEKS");
		FileInputStream stream = new FileInputStream("mykeystore.jceks");
		keyStore.load(stream, PASSWORD);

		return fetchKey(keyStore, algorithm, NO_MAC_TYPE);
	}

	public static SecretKeySpec getHashKey(String algorithm, String macType)
			throws KeyStoreException, NoSuchAlgorithmException, FileNotFoundException, CertificateException,
			IOException, UnrecoverableKeyException {
		KeyStore keyStore = KeyStore.getInstance("JCEKS");
		FileInputStream stream = new FileInputStream("mykeystore.jceks");
		keyStore.load(stream, PASSWORD);
		return fetchKey(keyStore, algorithm, macType);
	}

	private static SecretKeySpec fetchKey(KeyStore keyStore, String algorithm, String macType)
			throws NoSuchAlgorithmException, KeyStoreException, FileNotFoundException, CertificateException,
			IOException, UnrecoverableKeyException {
		String keyToFetch = macType.isEmpty() ? algorithm : algorithm + macType;
		SecretKeySpec key = (SecretKeySpec) keyStore.getKey(keyToFetch, PASSWORD);
		if (key == null) {
			key = generateKey(algorithm);
			if (!macType.isEmpty())
				algorithm += macType;
			storeKey(keyStore, key, algorithm);
		}
		return key;

	}

	public static void deleteOldKey() {

	}

	private static SecretKeySpec generateKey(String algorithm) throws NoSuchAlgorithmException {
		KeyGenerator generator;
		generator = KeyGenerator.getInstance(algorithm);

		switch (algorithm) {
		case "AES":
			generator.init(256);
			break;
		case "blowfish":
			generator.init(448);
			break;
		case "DES":
			generator.init(64);
			break;
		case "DESede":
			generator.init(192);
			break;
		case "HMacSHA256":
			generator.init(256);
		default:
			break;
		}
		return (SecretKeySpec) generator.generateKey();
	}

	private static void storeKey(KeyStore keyStore, SecretKey key, String algorithm) throws KeyStoreException,
			FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException {
		KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(key);
		ProtectionParameter protParam = new KeyStore.PasswordProtection(PASSWORD);
		keyStore.setEntry(algorithm, secret, protParam);

		try (FileOutputStream fos = new FileOutputStream("mykeystore.jceks")) {
			keyStore.store(fos, PASSWORD);
		}
	}

}
