package utils;

import java.io.IOException;
import java.nio.ByteBuffer;

public class IvGenerator {

	static byte[] generate8ByteIV() {
		ByteBuffer buff = ByteBuffer.allocate(8);
		try {
			buff.put(BytesUtils.long2byte((long) (Math.random() * System.currentTimeMillis())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buff.array();
	}

	public static int ivLength(String mode) {
		switch (mode) {
		case "AES":
		case "rc6-gmac":
		case "CTR":
			return 16;
		case "ECB":
		case "HMacSHA256":
		case "SHA1":
			return 0;
		case "DES":
		case "DESede":
		default:
			return 8;
		}
	}

	static byte[] generate16ByteIV() {
		ByteBuffer buff = ByteBuffer.allocate(16);
		try {
			buff.put(BytesUtils.long2byte((long) (Math.random() * System.currentTimeMillis())));
			buff.put(BytesUtils.long2byte((long) (Math.random() * System.currentTimeMillis())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buff.array();
	}

	public static boolean needsIV(String mode) {
		switch (mode) {
		case "ECB":
		case "HMacSHA256":
		case "SHA1":
			return false;
		default:
			return true;
		}
	}

	public static byte[] generateIV(String method) {
		switch (method) {
		case "AES":
			return generate16ByteIV();
		case "DES":
		case "DESede":
		case "blowfish":
			return generate8ByteIV();
		default:
			return generate8ByteIV();
		}
	}

} // DESede
