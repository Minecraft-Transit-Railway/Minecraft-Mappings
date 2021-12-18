package minecraftmappings;

import me.shedaniel.architectury.event.events.TextureStitchEvent;
import me.shedaniel.architectury.event.events.client.ClientPlayerEvent;
import me.shedaniel.architectury.registry.BlockEntityRenderers;
import me.shedaniel.architectury.registry.ColorHandlers;
import me.shedaniel.architectury.registry.ItemPropertiesRegistry;
import me.shedaniel.architectury.registry.RenderTypes;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;
import java.util.function.Function;

public interface RegistryUtilitiesClient {

	static void registerItemModelPredicate(String id, Item item, String tag) {
		ItemPropertiesRegistry.register(item, new ResourceLocation(id), (itemStack, clientWorld, livingEntity) -> itemStack.getOrCreateTag().contains(tag) ? 1 : 0);
	}

	static <T extends BlockEntityMapper> void registerTileEntityRenderer(BlockEntityType<T> type, Function<BlockEntityRenderDispatcher, BlockEntityRendererMapper<T>> factory) {
		BlockEntityRenderers.registerRenderer(type, context -> factory.apply(null));
	}

	static void registerRenderType(RenderType renderType, Block block) {
		RenderTypes.register(renderType, block);
	}

	static void registerBlockColors(BlockColor blockColor, Block block) {
		ColorHandlers.registerBlockColors(blockColor, block);
	}

	static void registerPlayerJoinEvent(Consumer<LocalPlayer> consumer) {
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(consumer::accept);
	}

	static void registerTextureStitchEvent(Consumer<TextureAtlas> consumer) {
		TextureStitchEvent.POST.register(consumer::accept);
	}
}
