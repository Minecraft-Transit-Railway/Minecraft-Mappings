package org.mtr.mapping.registry;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.PacketBuffer;
import org.mtr.mapping.tool.Dummy;

public abstract class PacketHandler extends Dummy {

	@MappedMethod
	public abstract void write(PacketBuffer packetBuffer);

	@MappedMethod
	public abstract void run();

	@MappedMethod
	public static String readString(PacketBuffer packetBuffer) {
		return packetBuffer.data.readUtf(32767);
	}

	@MappedMethod
	public static void writeString(PacketBuffer packetBuffer, String text) {
		packetBuffer.data.writeUtf(text, 32767);
	}
}
