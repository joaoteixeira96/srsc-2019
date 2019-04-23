package message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Header {

	private byte[] versionRelease;
	private byte[] payloadType;

	public Header(byte[] versionRelease, byte[] payloadType) {
		super();
		this.versionRelease = versionRelease;
		this.payloadType = payloadType;
	}

	public byte[] getVersionRelease() {
		return versionRelease;
	}

	public byte[] generateHeader(byte[] message) {
		byte[] separator = { 0x00 };
		byte[] header = new byte[6];
		short messageSize = (short) message.length;
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putShort(messageSize);
		short size = (short) message.length;
		System.arraycopy(versionRelease, 0, header, 0, 1);
		System.arraycopy(separator, 0, header, 1, 1);
		System.arraycopy(payloadType, 0, header, 2, 1);
		System.arraycopy(separator, 0, header, 3, 1);
		System.arraycopy(size, 0, header, 4, 2);
//		System.out.println("send: " + header[4]);
		return header;
	}

	public short getMessageLength(byte[] header) {
//		System.out.println("Receive:" + header[4]);
		byte[] messageSize = new byte[2];
		System.arraycopy(header, 4, messageSize, 0, 2);
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(messageSize);
		return buffer.getShort(0);
	}

	public void setVersionRelease(byte[] versionRelease) {
		this.versionRelease = versionRelease;
	}

	public byte[] getPayloadType() {
		return payloadType;
	}

	public void setPayloadType(byte[] payloadType) {
		this.payloadType = payloadType;
	}

}
