package utils;

import java.io.File;
import java.util.Scanner;

import javax.crypto.spec.SecretKeySpec;

public class ciphersuiteConfig {
	private String alg;
	private String mode;
	private String padding;
	private String sessionkeysize;
	private String IV;

	public String getIV() {
		return IV;
	}

	private String sessionkeyvalue;
	private String MACKM;
	private String MACKMSIZE;
	private String MACKMVALUE;
	private String MACKA;
	private String MACKASIZE;
	private String MACKAVALUE;
	private SecretKeySpec sessionKey;
	private SecretKeySpec macKAKey;
	private SecretKeySpec macKMKey;

	public ciphersuiteConfig() {
		try {
			File file = new File("ciphersuite.conf");
			Scanner sc = new Scanner(file);
			String[] ciphersuite = sc.nextLine().split(": ")[1].split("/");
			alg = ciphersuite[0];
			mode = ciphersuite[1];
			padding = ciphersuite[2];
			IV = sc.nextLine().split(": ")[1];
			sessionkeysize = sc.nextLine().split(": ")[1];
			sessionkeyvalue = sc.nextLine().split(": ")[1];
			sessionKey = new SecretKeySpec(sessionkeyvalue.getBytes(), alg);
			MACKM = sc.nextLine().split(": ")[1];
			MACKMSIZE = sc.nextLine().split(": ")[1];
			MACKMVALUE = sc.nextLine().split(": ")[1];
			macKMKey = new SecretKeySpec(MACKMVALUE.getBytes(), MACKM);
			MACKA = sc.nextLine().split(": ")[1];
			MACKASIZE = sc.nextLine().split(": ")[1];
			MACKAVALUE = sc.nextLine().split(": ")[1];
			macKAKey = new SecretKeySpec(MACKAVALUE.getBytes(), MACKA);
			sc.close();
		} catch (Exception e) {
			System.err.println(ciphersuiteConfig.class + ": constructor failed");
		}

	}

	public SecretKeySpec getSessionKey() {
		return sessionKey;
	}

	public SecretKeySpec getMacKAKey() {
		return macKAKey;
	}

	public SecretKeySpec getMacKMKey() {
		return macKMKey;
	}

	@Override
	public String toString() {
		return "ciphersuiteConfig [alg=" + alg + ", mode=" + mode + ", padding=" + padding + ", sessionkeysize="
				+ sessionkeysize + ", iv=" + IV + ", sessionkeyvalue=" + sessionkeyvalue + ", MACKM=" + MACKM
				+ ", MACKMSIZE=" + MACKMSIZE + ", MACKMVALUE=" + MACKMVALUE + ", MACKA=" + MACKA + ", MACKASIZE="
				+ MACKASIZE + ", MACKAVALUE=" + MACKAVALUE + ", sessionKey=" + sessionKey + ", macKAKey=" + macKAKey
				+ ", macKMKey=" + macKMKey + "]";
	}

	public String getAlg() {
		return alg;
	}

	public void setAlg(String alg) {
		this.alg = alg;
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

	public String getMACKMSIZE() {
		return MACKMSIZE;
	}

	public void setMACKMSIZE(String mACKMSIZE) {
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

	public String getMACKASIZE() {
		return MACKASIZE;
	}

	public void setMACKASIZE(String mACKASIZE) {
		MACKASIZE = mACKASIZE;
	}

	public String getMACKAVALUE() {
		return MACKAVALUE;
	}

	public void setMACKAVALUE(String mACKAVALUE) {
		MACKAVALUE = mACKAVALUE;
	}

}
