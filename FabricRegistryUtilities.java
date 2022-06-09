package @package@;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;
import java.util.function.Function;

public interface FabricRegistryUtilities {

	static void registerItemModelPredicate(String id, Item item, String tag) {
		FabricModelPredicateProviderRegistry.register(item, new ResourceLocation(id), (itemStack, clientWorld, livingEntity, i) -> itemStack.getOrCreateTag().contains(tag) ? 1 : 0);
	}

	static <T extends BlockEntityMapper> void registerTileEntityRenderer(BlockEntityType<T> type, Function<BlockEntityRenderDispatcher, BlockEntityRendererMapper<T>> factory) {
		BlockEntityRendererRegistry.register(type, context -> factory.apply(null));
	}

	static <T extends Entity> void registerEntityRenderer(EntityType<T> type, Function<EntityRendererProvider.Context, EntityRendererMapper<T>> factory) {
		EntityRendererRegistry.register(type, factory::apply);
	}

	static void registerCommand(Consumer<CommandDispatcher<CommandSourceStack>> callback){
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, commandSelection) -> callback.accept(dispatcher));
	}
}
