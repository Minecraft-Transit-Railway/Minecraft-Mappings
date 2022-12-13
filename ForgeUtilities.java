package @package@;

import dev.architectury.event.events.client.ClientTextureStitchEvent;
import dev.architectury.platform.forge.EventBuses;
import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgeUtilities {

	private static Runnable renderTickAction = () -> {
	};
	private static Consumer<Object> renderGameOverlayAction = matrices -> {
	};
	private static final Map<ResourceLocation, CreativeModeTab> CREATIVE_MODE_TABS = new HashMap<>();
	private static final Set<EntityRendererPair<?>> ENTITY_RENDERER_PAIRS = new HashSet<>();

	public static void registerModEventBus(String modId, IEventBus eventBus) {
		EventBuses.registerModEventBus(modId, eventBus);
	}

	public static void registerKeyBinding(KeyMapping keyMapping) {
		ClientRegistry.registerKeyBinding(keyMapping);
	}

	public static Packet<?> createAddEntityPacket(Entity entity) {
		return NetworkHooks.getEntitySpawningPacket(entity);
	}

	public static Supplier<CreativeModeTab> createCreativeModeTab(ResourceLocation resourceLocation, Supplier<ItemStack> iconSupplier, String translationKey) {
		if (!CREATIVE_MODE_TABS.containsKey(resourceLocation)) {
			CREATIVE_MODE_TABS.put(resourceLocation, CreativeTabRegistry.create(resourceLocation, iconSupplier));
		}
		return () -> CREATIVE_MODE_TABS.get(resourceLocation);
	}

	public static void registerCreativeModeTab(ResourceLocation resourceLocation, Item item) {
	}

	public static ResourceKey<Registry<Item>> registryGetItem() {
		return Registry.ITEM_REGISTRY;
	}

	public static ResourceKey<Registry<Block>> registryGetBlock() {
		return Registry.BLOCK_REGISTRY;
	}

	public static ResourceKey<Registry<BlockEntityType<?>>> registryGetBlockEntityType() {
		return Registry.BLOCK_ENTITY_TYPE_REGISTRY;
	}

	public static ResourceKey<Registry<EntityType<?>>> registryGetEntityType() {
		return Registry.ENTITY_TYPE_REGISTRY;
	}

	public static ResourceKey<Registry<SoundEvent>> registryGetSoundEvent() {
		return Registry.SOUND_EVENT_REGISTRY;
	}

	public static void renderTickAction(Runnable runnable) {
		renderTickAction = runnable;
	}

	public static void renderGameOverlayAction(Consumer<Object> consumer) {
		renderGameOverlayAction = consumer;
	}

	public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<? extends T>> entityType, EntityRendererProvider<T> entityRendererProvider) {
		ENTITY_RENDERER_PAIRS.add(new EntityRendererPair<>(entityType, entityRendererProvider));
	}

	public static void registerTextureStitchEvent(Consumer<TextureAtlas> consumer) {
		ClientTextureStitchEvent.POST.register(consumer::accept);
	}

	public static class Events {

		@SubscribeEvent
		public static void onRenderTickEvent(net.minecraftforge.client.event.RenderWorldLastEvent event) {
			renderTickAction.run();
		}

		@SubscribeEvent
		public static void onRenderGameOverlayEvent(RenderGameOverlayEvent.Post event) {
			renderGameOverlayAction.accept(event.getMatrixStack());
		}
	}

	public static class ClientsideEvents {

		@SubscribeEvent
		public static void onEntityRendererEvent(EntityRenderersEvent.RegisterRenderers event) {
			ENTITY_RENDERER_PAIRS.forEach(entityRendererPair -> entityRendererPair.register(event));
		}
	}

	private static class EntityRendererPair<T extends Entity> {

		private final Supplier<EntityType<? extends T>> entityTypeSupplier;
		private final EntityRendererProvider<T> entityRendererProvider;

		private EntityRendererPair(Supplier<EntityType<? extends T>> entityTypeSupplier, EntityRendererProvider<T> entityRendererProvider) {
			this.entityTypeSupplier = entityTypeSupplier;
			this.entityRendererProvider = entityRendererProvider;
		}

		private void register(EntityRenderersEvent.RegisterRenderers event) {
			event.registerEntityRenderer(entityTypeSupplier.get(), entityRendererProvider);
		}
	}

	public static class RegisterCreativeTabs {
	}
}
