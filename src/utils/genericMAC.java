package utils;

import java.io.ByteArrayOutputStream;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;

public class genericMAC {
	private ciphersuiteConfig ciphersuite;

	public genericMAC(ciphersuiteConfig ciphersuite) {
		super();
		this.ciphersuite = ciphersuite;
	}

	public int macKMSize() {
		return 16; // TODO
	}

	public int macKASize() {
		return 16; // TODO
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
//		System.out.println("generateMessageWithMacAppended: " + Utils.toHex(mac));
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
			AlgorithmParameterSpec params = new IvParameterSpec(ciphersuite.getIV().getBytes());
			mac.init(ciphersuite.getMacKMKey(), params);
			mac.update(ciphersuite.getIV().getBytes());
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
			AlgorithmParameterSpec params = new IvParameterSpec(ciphersuite.getIV().getBytes());
			mac.init(ciphersuite.getMacKAKey(), params);
			mac.update(ciphersuite.getIV().getBytes());
			mac.update(message);
			return mac.doFinal();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Hash failed");
		}
	}

}
