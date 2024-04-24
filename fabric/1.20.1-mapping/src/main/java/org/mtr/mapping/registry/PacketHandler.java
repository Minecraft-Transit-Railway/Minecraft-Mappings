package org.mtr.mapping.registry;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.tool.DummyClass;
import org.mtr.mapping.tool.PacketBufferSender;

public abstract class PacketHandler extends DummyClass {

	@MappedMethod
	public abstract void write(PacketBufferSender packetBufferSender);

	@MappedMethod
	public void runServer(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
	}

	@MappedMethod
	public void runClient() {
	}
}
