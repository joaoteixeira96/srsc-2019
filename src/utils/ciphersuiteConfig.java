package utils;

import java.io.FileInputStream;
import java.util.Properties;

import javax.crypto.spec.SecretKeySpec;

public class ciphersuiteConfig {
	private String ciphersuite;
	private String sessionkeysize;
	private String sessionkeyvalue;
	private String MACKM;
	private int MACKMSIZE;
	private String MACKMVALUE;
	private String MACKA;
	private int MACKASIZE;
	private String MACKAVALUE;
	private SecretKeySpec sessionKey;
	private SecretKeySpec macKAKey;
	private SecretKeySpec macKMKey;

	public ciphersuiteConfig() {
		try {
			FileInputStream inputStream = new FileInputStream("ciphersuite.properties");
			Properties properties = new Properties();
			properties.load(inputStream);
			ciphersuite = properties.getProperty("CIPHERSUITE");
			sessionkeysize = properties.getProperty("SESSIONKEYSIZE");
			sessionkeyvalue = properties.getProperty("SESSIONKEYVALUE");
			sessionKey = new SecretKeySpec(sessionkeyvalue.getBytes(), ciphersuite.split("/")[0]);
			MACKM = properties.getProperty("MACKM");
			MACKMSIZE = Integer.parseInt(properties.getProperty("MACKMEYSIZE"));
			MACKMVALUE = properties.getProperty("MACKMEYVALUE");
			macKMKey = new SecretKeySpec(MACKMVALUE.getBytes(), MACKM);
			MACKA = properties.getProperty("MACKA");
			MACKASIZE = Integer.parseInt(properties.getProperty("MACKAKEYSIZE"));
			MACKAVALUE = properties.getProperty("MACKAMEYVALUE");
			macKAKey = new SecretKeySpec(MACKAVALUE.getBytes(), MACKA);
		} catch (Exception e) {
			System.err.println(ciphersuiteConfig.class + ": constructor failed");
		}

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

	@Override
	public String toString() {
		return "ciphersuiteConfig [ciphersuite=" + ciphersuite + ", sessionkeysize=" + sessionkeysize
				+ ", sessionkeyvalue=" + sessionkeyvalue + ", MACKM=" + MACKM + ", MACKMSIZE=" + MACKMSIZE
				+ ", MACKMVALUE=" + MACKMVALUE + ", MACKA=" + MACKA + ", MACKASIZE=" + MACKASIZE + ", MACKAVALUE="
				+ MACKAVALUE + ", sessionKey=" + sessionKey + ", macKAKey=" + macKAKey + ", macKMKey=" + macKMKey + "]";
	}

	public SecretKeySpec getMacKAKey() {
		return macKAKey;
	}

	public SecretKeySpec getMacKMKey() {
		return macKMKey;
	}

	public String getSessionkeysize() {
		return sessionkeysize;
	}

	public void setSessionkeysize(String sessionkeysize) {
		this.sessionkeysize = sessionkeysize;
	}

	public String getSessionkeyvalue() {
		return sessionkeyvalue;
	}

	public void setSessionkeyvalue(String sessionkeyvalue) {
		this.sessionkeyvalue = sessionkeyvalue;
	}

	public String getMACKM() {
		return MACKM;
	}

	public void setMACKM(String mACKM) {
		MACKM = mACKM;
	}

	public int getMACKMSIZE() {
		return MACKMSIZE / 8;
	}

	public void setMACKMSIZE(int mACKMSIZE) {
		MACKMSIZE = mACKMSIZE;
	}

	public String getMACKMVALUE() {
		return MACKMVALUE;
	}

	public void setMACKMVALUE(String mACKMVALUE) {
		MACKMVALUE = mACKMVALUE;
	}

	public String getMACKA() {
		return MACKA;
	}

	public void setMACKA(String mACKA) {
		MACKA = mACKA;
	}

	public int getMACKASIZE() {
		return MACKASIZE / 8;
	}

	public void setMACKASIZE(int mACKASIZE) {
		MACKASIZE = mACKASIZE;
	}

	public String getMACKAVALUE() {
		return MACKAVALUE;
	}

	public void setMACKAVALUE(String mACKAVALUE) {
		MACKAVALUE = mACKAVALUE;
	}

}
