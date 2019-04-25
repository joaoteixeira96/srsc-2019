package utils;

import java.io.IOException;
import java.nio.ByteBuffer;

public class IvGenerator {

	static byte[] generate8ByteIV() {
		ByteBuffer buff = ByteBuffer.allocate(8);
		try {
			buff.put(BytesUtils.long2byte((long) (Math.random() + System.currentTimeMillis())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buff.array();
	}

	public static int ivLength(String mode) {
		switch (mode) {
		case "AES":
		case "rc6-gmac":
			return 16;
		case "DES":
		case "DESede":
		default:
			return 8;
		}
	}

	static byte[] generate16ByteIV() {
		ByteBuffer buff = ByteBuffer.allocate(16);
		try {
			buff.put(BytesUtils.long2byte((long) (Math.random() + System.currentTimeMillis())));
			buff.put(BytesUtils.long2byte((long) (Math.random() + System.currentTimeMillis())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buff.array();
	}

	static byte[] generateIV(String mode) {
		switch (mode) {
		case "AES":
		case "rc6-gmac":
			return generate16ByteIV();
		case "DES":
		case "DESede":
		default:
			return generate8ByteIV();
		}
	}

} // DESede
