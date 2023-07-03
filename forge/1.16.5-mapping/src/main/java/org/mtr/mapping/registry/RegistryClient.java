package org.mtr.mapping.registry;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityRendererArgument;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.ResourceLocation;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.tool.Dummy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class RegistryClient extends Dummy {

	private static final List<Runnable> OBJECTS_TO_REGISTER = new ArrayList<>();

	@MappedMethod
	public static void init() {
		OBJECTS_TO_REGISTER.forEach(Runnable::run);
	}

	@MappedMethod
	public static <T extends BlockEntityType<U>, U extends BlockEntityExtension> void registerBlockEntityRenderer(T blockEntityType, Function<BlockEntityRendererArgument, BlockEntityRenderer<U>> rendererInstance) {
		OBJECTS_TO_REGISTER.add(() -> ClientRegistry.bindTileEntityRenderer(blockEntityType.data, dispatcher -> rendererInstance.apply(new BlockEntityRendererArgument(dispatcher))));
	}

	@MappedMethod
	public static void setupPackets(ResourceLocation resourceLocation) {
	}

	@MappedMethod
	public static <T extends PacketHandler> void sendPacketToServer(T data) {
		if (Registry.simpleChannel != null) {
			Registry.simpleChannel.sendToServer(data);
		}
	}
}
