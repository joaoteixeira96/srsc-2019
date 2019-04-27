package utils;

import java.io.ByteArrayOutputStream;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;

public class genericMAC {

	private final static byte[] iv = "1234567890123456".getBytes();

	private ciphersuiteConfig ciphersuite;
	private boolean kAusesIV;
	private boolean kMusesIV;

	public genericMAC(ciphersuiteConfig ciphersuite, boolean kAusesIV, boolean kMusesIV) {
		super();
		this.ciphersuite = ciphersuite;
		this.kAusesIV = kAusesIV;
		this.kMusesIV = kMusesIV;
	}

	public int macKMSize() {
		return ciphersuite.getMACKMSIZE();
	}

	public int macKASize() {
		return ciphersuite.getMACKASIZE();
	}

	public byte[] generateMessageWithMacAppended(byte[] message, String type) throws Exception {
		byte[] mac = null;
		switch (type) {
		case "KM":
			mac = calculateMacKM(message);
			break;
		case "KA":
			mac = calculateMacKA(message);
			break;
		default:
			mac = calculateMacKM(message);
			break;
		}
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		array.write(message);
		array.write(mac);
		return array.toByteArray();
	}

	public boolean confirmKMac(byte[] message, byte[] mac, String type) {
		try {
			switch (type) {
			case "KM":
				return Arrays.equals(mac, calculateMacKM(message));
			case "KA":
				return Arrays.equals(mac, calculateMacKA(message));
			default:
				return Arrays.equals(mac, calculateMacKM(message));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private byte[] calculateMacKM(byte[] message) throws Exception {
		try {
			Mac mac = Mac.getInstance(ciphersuite.getMACKM());
			if (kMusesIV) {
				AlgorithmParameterSpec params = new IvParameterSpec(iv);
				mac.init(ciphersuite.getMacKMKey(), params);
				mac.update(iv);
			} else {
				mac.init(ciphersuite.getMacKMKey());
			}
			mac.update(message);
			return mac.doFinal();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Hash failed");
		}
	}

	private byte[] calculateMacKA(byte[] message) throws Exception {
		try {
			Mac mac = Mac.getInstance(ciphersuite.getMACKA());
			if (kAusesIV) {
				AlgorithmParameterSpec params = new IvParameterSpec(iv);
				mac.init(ciphersuite.getMacKAKey(), params);
				mac.update(iv);
			} else {
				mac.init(ciphersuite.getMacKAKey());
			}
			mac.update(message);
			return mac.doFinal();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Hash failed");
		}
	}

}
