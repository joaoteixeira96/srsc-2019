package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BytesUtils {

	private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

	public static byte[] long2byte(long l) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(Long.SIZE / 8);
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeLong(l);
		byte[] result = baos.toByteArray();
		dos.close();
		return result;
	}

	public static long byte2long(byte[] b) throws IOException {
		ByteArrayInputStream baos = new ByteArrayInputStream(b);
		DataInputStream dos = new DataInputStream(baos);
		long result = dos.readLong();
		dos.close();
		return result;
	}

}
