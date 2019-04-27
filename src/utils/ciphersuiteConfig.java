package utils;

import java.io.FileInputStream;
import java.util.Properties;

import javax.crypto.spec.SecretKeySpec;

import keyStore.KeyStoreManager;

public class ciphersuiteConfig {
	private String ciphersuite;
	private int sessionkeysize;
	private String MACKM;
	private int MACKMSIZE;
	private String MACKA;
	private int MACKASIZE;
	private SecretKeySpec sessionKey;
	private SecretKeySpec macKAKey;
	private SecretKeySpec macKMKey;
	private String method;
	private String mode;
	private String padding;

	public ciphersuiteConfig() {
		try {
			FileInputStream inputStream = new FileInputStream("ciphersuite.properties");
			Properties properties = new Properties();
			properties.load(inputStream);
			ciphersuite = properties.getProperty("CIPHERSUITE");
			MACKM = properties.getProperty("MACKM");
			MACKA = properties.getProperty("MACKA");
			String[] cipher = ciphersuite.split("/");
			sessionKey = KeyStoreManager.getEncryptionKey(cipher[0]);
			method = cipher[0];
			mode = cipher[1];
			padding = cipher[2];
			macKAKey = KeyStoreManager.getHashKey(MACKA, "KA");
			macKMKey = KeyStoreManager.getHashKey(MACKM, "KM");
			setSessionkeysize(sessionKey.getEncoded().length);
			MACKMSIZE = hashSize(MACKM);
			MACKASIZE = hashSize(MACKA);
			System.out.println(this.toString());
		} catch (Exception e) {
			System.err.println(ciphersuiteConfig.class + ": constructor failed");
		}

	}

	private int hashSize(String macType) {
		switch (macType) {
		case "rc6-gmac":
			return 16;
		default:
			return 32;
		}
	}

	@Override
	public String toString() {
		return "ciphersuiteConfig [ciphersuite=" + ciphersuite + ", sessionkeysize=" + sessionkeysize + ", MACKM="
				+ MACKM + ", MACKMSIZE=" + MACKMSIZE + ", MACKA=" + MACKA + ", MACKASIZE=" + MACKASIZE + ", sessionKey="
				+ sessionKey + ", macKAKey=" + macKAKey + ", macKMKey=" + macKMKey + ", method=" + method + ", mode="
				+ mode + ", padding=" + padding + "]";
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getPadding() {
		return padding;
	}

	public void setPadding(String padding) {
		this.padding = padding;
	}

	public SecretKeySpec getSessionKey() {
		return sessionKey;
	}

	public String getCiphersuite() {
		return ciphersuite;
	}

	public void setCiphersuite(String ciphersuite) {
		this.ciphersuite = ciphersuite;
	}

	public void setSessionKey(SecretKeySpec sessionKey) {
		this.sessionKey = sessionKey;
	}

	public void setMacKAKey(SecretKeySpec macKAKey) {
		this.macKAKey = macKAKey;
	}

	public void setMacKMKey(SecretKeySpec macKMKey) {
		this.macKMKey = macKMKey;
	}

	public SecretKeySpec getMacKAKey() {
		return macKAKey;
	}

	public SecretKeySpec getMacKMKey() {
		return macKMKey;
	}

	public String getMACKM() {
		return MACKM;
	}

	public void setMACKM(String mACKM) {
		MACKM = mACKM;
	}

	public int getMACKMSIZE() {
		return MACKMSIZE;
	}

	public void setMACKMSIZE(int mACKMSIZE) {
		MACKMSIZE = mACKMSIZE;
	}

	public String getMACKA() {
		return MACKA;
	}

	public void setMACKA(String mACKA) {
		MACKA = mACKA;
	}

	public int getMACKASIZE() {
		return MACKASIZE;
	}

	public void setMACKASIZE(int mACKASIZE) {
		MACKASIZE = mACKASIZE;
	}

	public int getSessionkeysize() {
		return sessionkeysize;
	}

	public void setSessionkeysize(int sessionkeysize) {
		this.sessionkeysize = sessionkeysize;
	}

}
