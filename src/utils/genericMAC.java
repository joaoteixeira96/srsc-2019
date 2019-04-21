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
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		array.write(message);
		array.write(mac);
		return array.toByteArray();
	}

	public boolean confirmKMac(byte[] message, byte[] mac) {
		return true;// TODO
	}

	private byte[] calculateMacKM(byte[] message) throws Exception {
		try {
			Mac mac = Mac.getInstance(ciphersuite.getMACKM());
			AlgorithmParameterSpec params = new IvParameterSpec(ciphersuite.getIV().getBytes());
			mac.init(ciphersuite.getMacKMKey(), params);
			mac.update(ciphersuite.getIV().getBytes());
			return mac.doFinal(message);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Hash failed");
			throw new Exception("Hash failed");
		}
	}

}
