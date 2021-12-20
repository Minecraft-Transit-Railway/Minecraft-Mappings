package @package@;

import me.shedaniel.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface NetworkUtilities {

	static void registerReceiverS2C(ResourceLocation id, NetworkManager.NetworkReceiver receiver) {
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, id, receiver);
	}

	static void registerReceiverC2S(ResourceLocation id, PacketCallback packetCallback) {
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, id, (packet, context) -> {
			final Player player = context.getPlayer();
			if (player != null) {
				packetCallback.packetCallback(player.getServer(), (ServerPlayer) player, packet);
			}
		});
	}

	static void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf packet) {
		NetworkManager.sendToPlayer(player, id, packet);
	}

	static void sendToServer(ResourceLocation id, FriendlyByteBuf packet) {
		NetworkManager.sendToServer(id, packet);
	}

	@FunctionalInterface
	interface PacketCallback {
		void packetCallback(MinecraftServer server, ServerPlayer player, FriendlyByteBuf packet);
	}
}
