package @package@;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTextureStitchEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;
import java.util.function.Function;

public interface RegistryUtilitiesClient {

	static void registerItemModelPredicate(String id, Item item, String tag) {
		ItemPropertiesRegistry.register(item, new ResourceLocation(id), (itemStack, clientWorld, livingEntity, i) -> itemStack.getOrCreateTag().contains(tag) ? 1 : 0);
	}

	static <T extends BlockEntityMapper> void registerTileEntityRenderer(BlockEntityType<T> type, Function<BlockEntityRenderDispatcher, BlockEntityRendererMapper<T>> factory) {
		BlockEntityRendererRegistry.register(type, context -> factory.apply(null));
	}

	static <T extends Entity> void registerEntityRenderer(EntityType<T> type, Function<EntityRendererProvider.Context, EntityRendererMapper<T>> factory) {
	}

	static void registerRenderType(RenderType renderType, Block block) {
		RenderTypeRegistry.register(renderType, block);
	}

	static void registerBlockColors(BlockColor blockColor, Block block) {
		ColorHandlerRegistry.registerBlockColors(blockColor, block);
	}

	static void registerPlayerJoinEvent(Consumer<LocalPlayer> consumer) {
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(consumer::accept);
	}

	static void registerClientStoppingEvent(Consumer<Minecraft> consumer) {
		ClientLifecycleEvent.CLIENT_STOPPING.register(consumer::accept);
	}

	static void registerClientTickEvent(Consumer<Minecraft> consumer) {
		ClientTickEvent.CLIENT_PRE.register(consumer::accept);
	}

	static void registerTextureStitchEvent(Consumer<TextureAtlas> consumer) {
		ClientTextureStitchEvent.POST.register(consumer::accept);
	}
}
