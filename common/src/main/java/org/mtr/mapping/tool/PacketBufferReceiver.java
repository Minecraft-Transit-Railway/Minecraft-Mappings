package org.mtr.mapping.tool;

import io.netty.buffer.ByteBuf;
import org.mtr.mapping.annotation.MappedMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class PacketBufferReceiver extends DummyClass {

	private int receivedCount;
	private int readIndex;
	private final int count;
	private final ByteBuf[] byteBufArray;
	private final Consumer<PacketBufferReceiver> onComplete;

	private static final Map<Long, PacketBufferReceiver> RECEIVED_PACKETS = new HashMap<>();

	private PacketBufferReceiver(int count, Consumer<PacketBufferReceiver> onComplete) {
		this.count = count;
		this.onComplete = onComplete;
		byteBufArray = new ByteBuf[count];
	}

	@MappedMethod
	public boolean readBoolean() {
		read();
		return byteBufArray[readIndex].readBoolean();
	}

	@MappedMethod
	public char readChar() {
		read();
		return byteBufArray[readIndex].readChar();
	}

	@MappedMethod
	public int readInt() {
		read();
		return byteBufArray[readIndex].readInt();
	}

	@MappedMethod
	public float readFloat() {
		read();
		return byteBufArray[readIndex].readFloat();
	}

	@MappedMethod
	public long readLong() {
		read();
		return byteBufArray[readIndex].readLong();
	}

	@MappedMethod
	public double readDouble() {
		read();
		return byteBufArray[readIndex].readDouble();
	}

	@MappedMethod
	public String readString() {
		final int stringLength = readInt();
		final char[] characters = new char[stringLength];
		for (int i = 0; i < stringLength; i++) {
			characters[i] = readChar();
		}
		return new String(characters);
	}

	private void read() {
		if (byteBufArray[readIndex].readableBytes() == 0) {
			readIndex++;
		}
	}

	private boolean receive(int index, ByteBuf byteBuf) {
		byteBufArray[index] = byteBuf;
		receivedCount++;
		if (receivedCount == count) {
			onComplete.accept(this);
			return true;
		} else {
			return false;
		}
	}

	@MappedMethod
	public static void receive(ByteBuf byteBuf, Consumer<PacketBufferReceiver> onComplete, Consumer<Runnable> scheduler) {
		final ByteBuf byteBufCopy = byteBuf.copy();
		scheduler.accept(() -> {
			final long id = byteBufCopy.readLong();
			final int index = byteBufCopy.readInt();
			final int count = byteBufCopy.readInt();
			if (RECEIVED_PACKETS.computeIfAbsent(id, key -> new PacketBufferReceiver(count, onComplete)).receive(index, byteBufCopy)) {
				RECEIVED_PACKETS.remove(id);
			}
		});
	}
}
