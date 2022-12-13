package @package@;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface FabricRegistryUtilities {

	static void registerItemModelPredicate(String id, Item item, String tag) {
		FabricModelPredicateProviderRegistry.register(item, new ResourceLocation(id), (itemStack, clientWorld, livingEntity) -> itemStack.getOrCreateTag().contains(tag) ? 1 : 0);
	}

	static <T extends BlockEntityMapper> void registerTileEntityRenderer(BlockEntityType<T> type, Function<BlockEntityRenderDispatcher, BlockEntityRendererMapper<T>> factory) {
		BlockEntityRendererRegistry.INSTANCE.register(type, context -> factory.apply(null));
	}

	static <T extends Entity> void registerEntityRenderer(EntityType<T> type, Function<EntityRenderDispatcher, EntityRendererMapper<T>> factory) {
		EntityRendererRegistry.INSTANCE.register(type, (manager, context) -> factory.apply(manager));
	}

	static void registerCommand(Consumer<CommandDispatcher<CommandSourceStack>> callback) {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> callback.accept(dispatcher));
	}

	static void registerCreativeModeTab(CreativeModeTab creativeModeTab, Item item) {
	}

	static CreativeModeTab createCreativeModeTab(ResourceLocation id, Supplier<ItemStack> supplier) {
		return FabricItemGroupBuilder.build(id, supplier);
	}
}
