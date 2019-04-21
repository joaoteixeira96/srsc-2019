package message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Header {

	private String versionRelease;
	private String payloadType;

	public Header(String versionRelease, String payloadType) {
		super();
		this.versionRelease = versionRelease;
		this.payloadType = payloadType;
	}

	public String getVersionRelease() {
		return versionRelease;
	}

	public byte[] generateHeader(byte[] message) {
		byte[] versionReleaseBytes = versionRelease.getBytes();
		byte[] payloadTypeBytes = payloadType.getBytes();
		byte[] separator = { 0x00 };
		byte[] header = new byte[6];
		short messageSize = (short) message.length;
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putShort(messageSize);
		byte[] size = buffer.array();
		System.arraycopy(versionReleaseBytes, 0, header, 0, 1);
		System.arraycopy(separator, 0, header, 1, 1);
		System.arraycopy(payloadTypeBytes, 0, header, 2, 1);
		System.arraycopy(separator, 0, header, 3, 1);
		System.arraycopy(size, 0, header, 4, 2);
		System.out.println("send: " + header[4]);
		return header;
	}

	public short getMessageLength(byte[] header) {
		System.out.println("Receive:" + header[4]);
		byte[] messageSize = new byte[2];
		System.arraycopy(header, 4, messageSize, 0, 2);
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(messageSize);
		return buffer.getShort(0);
	}

	public void setVersionRelease(String versionRelease) {
		this.versionRelease = versionRelease;
	}

	public String getPayloadType() {
		return payloadType;
	}

	public void setPayloadType(String payloadType) {
		this.payloadType = payloadType;
	}

}
