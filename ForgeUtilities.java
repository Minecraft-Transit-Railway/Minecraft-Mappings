package @package@;

import me.shedaniel.architectury.event.events.TextureStitchEvent;
import me.shedaniel.architectury.platform.forge.EventBuses;
import me.shedaniel.architectury.registry.CreativeTabs;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
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
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ForgeUtilities {

	private static Runnable renderTickAction = () -> {
	};
	private static Consumer<Object> renderGameOverlayAction = matrices -> {
	};
	private static final Map<ResourceLocation, CreativeModeTab> CREATIVE_MODE_TABS = new HashMap<>();

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
			CREATIVE_MODE_TABS.put(resourceLocation, CreativeTabs.create(resourceLocation, iconSupplier));
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

	public static ResourceKey<Registry<ParticleType<?>>> registryGetParticleType() {
		return Registry.PARTICLE_TYPE_REGISTRY;
	}

	public static void renderTickAction(Runnable runnable) {
		renderTickAction = runnable;
	}

	public static void renderGameOverlayAction(Consumer<Object> consumer) {
		renderGameOverlayAction = consumer;
	}

	public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<? extends T>> entityType, Function<Object, EntityRendererMapper<T>> entityRendererProvider) {
	}

	public static void registerTextureStitchEvent(Consumer<TextureAtlas> consumer) {
		TextureStitchEvent.POST.register(consumer::accept);
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
	}

	public static class RegisterCreativeTabs {
	}
}
