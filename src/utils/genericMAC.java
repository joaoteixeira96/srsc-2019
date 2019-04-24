package utils;

import java.io.ByteArrayOutputStream;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;

public class genericMAC {
	private ciphersuiteConfig ciphersuite;

	public genericMAC(ciphersuiteConfig ciphersuite) {
		super();
		this.ciphersuite = ciphersuite;
	}

	public byte[] generateMessageWithMacAppended(byte[] message) throws Exception {
		byte[] mac = calculateMacKM(message);
		System.out.println("generateMessageWithMacAppended: " + Utils.toHex(mac));
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		array.write(message);
		array.write(mac);
		return array.toByteArray();
	}

	public boolean confirmKMac(byte[] message, byte[] mac) {
		try {
			byte[] messageMac = calculateMacKM(message);
			System.out.println("confirmKMac:messageMac " + Utils.toHex(messageMac) + "mac: " + Utils.toHex(mac));
			return Utils.toHex(calculateMacKM(message)).equals(Utils.toHex(mac));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;// TODO
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
			System.err.println("Hash failed");
			throw new Exception("Hash failed");
		}
	}

}
